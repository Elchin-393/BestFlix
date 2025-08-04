package com.bestflix.movie.controller;

import com.bestflix.movie.entity.Movie;
import com.bestflix.movie.service.IMovieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private IMovieService movieService;

    @InjectMocks
    private MovieController movieController;

    @BeforeEach
    void setup() {
        movieController = new MovieController(movieService);
        mockMvc = MockMvcBuilders.standaloneSetup(movieController).build();
    }

    @Test
    void shouldReturnAllMovies() throws Exception {
        Movie movie1 = new Movie();
        movie1.setMovieName("Inception");

        Movie movie2 = new Movie();
        movie2.setMovieName("The Dark Knight");

        List<Movie> movies = List.of(movie1, movie2);

        when(movieService.getMovies()).thenReturn(movies);

        mockMvc.perform(get("/rest/api/movie/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].movieName").value("Inception"))
                .andExpect(jsonPath("$[1].movieName").value("The Dark Knight"));
    }

    @Test
    void shouldReturnUserMovies() throws Exception {
        String username = "elcin";

        Movie movie1 = new Movie();
        movie1.setMovieName("Interstellar");

        Movie movie2 = new Movie();
        movie2.setMovieName("Tenet");

        List<Movie> userMovies = List.of(movie1, movie2);

        when(movieService.getMyMovies(username)).thenReturn(userMovies);

        mockMvc.perform(post("/rest/api/movie/mymovies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"elcin\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].movieName").value("Interstellar"))
                .andExpect(jsonPath("$[1].movieName").value("Tenet"));
    }

    @Test
    void shouldReturnAllMovieById() throws Exception {
        Movie movie1 = new Movie();
        movie1.setMovieName("Inception");
        movie1.setId(1);

        when(movieService.getMovieById(movie1.getId())).thenReturn(Optional.of(movie1));

        mockMvc.perform(get("/rest/api/movie/{id}",movie1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.movieName").value("Inception"));
    }


    @Test
    void shouldUploadMovieSuccessfully() throws Exception {
        Movie movie = new Movie();
        movie.setMovieName("Interstellar");
        movie.setId(42);

        MockMultipartFile imageFile = new MockMultipartFile(
                "image", "poster.jpg", MediaType.IMAGE_JPEG_VALUE, "fake image".getBytes());

        MockMultipartFile videoFile = new MockMultipartFile(
                "video", "trailer.mp4", MediaType.APPLICATION_OCTET_STREAM_VALUE, "fake video".getBytes());

        MockMultipartFile movieJson = new MockMultipartFile(
                "movie", "", "application/json",
                new ObjectMapper().writeValueAsBytes(movie));

        mockMvc.perform(multipart("/rest/api/movie/upload")
                        .file(imageFile)
                        .file(videoFile)
                        .file(movieJson)
                        .param("username", "elcin")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(content().string("Movie Uploaded Successfully!"));

        verify(movieService).uploadMovie("elcin", movie, imageFile, videoFile);
    }

    @Test
    void shouldReturnImageDataWithProperHeaders() throws Exception {
        Long movieId = 1L;
        byte[] imageData = "fake image".getBytes();
        String imageType = MediaType.IMAGE_JPEG_VALUE;

        Movie movie = new Movie();
        movie.setImageData(imageData);
        movie.setImageType(imageType);

        when(movieService.getMovieById(movieId)).thenReturn(Optional.of(movie));

        mockMvc.perform(get("/rest/api/movie/image/{id}", movieId))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", imageType))
                .andExpect(content().bytes(imageData));
    }

    @Test
    void shouldReturnNotFoundWhenMovieMissing() throws Exception {
        Long movieId = 999L;

        when(movieService.getMovieById(movieId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/rest/api/movie/image/{id}", movieId))
                .andExpect(status().isNotFound());
    }


    @Test
    void shouldReturnVideoStreamWithHeaders() throws Exception {
        Long movieId = 1L;
        String videoPath = "src/test/resources/test-video.mp4";
        File file = new File(videoPath);
        byte[] videoContent = Files.readAllBytes(file.toPath());

        Movie movie = new Movie();
        movie.setVideoPath(videoPath);

        when(movieService.getMovieById(movieId)).thenReturn(Optional.of(movie));

        mockMvc.perform(get("/rest/api/movie/video/{id}", movieId))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + file.getName()))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.valueOf("video/mp4").toString()))
                .andExpect(header().longValue(HttpHeaders.CONTENT_LENGTH, videoContent.length))
                .andExpect(content().bytes(videoContent));
    }
    @Test
    void shouldReturnNotFoundForMissingMovie() throws Exception {
        Long missingId = 404L;

        when(movieService.getMovieById(missingId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/rest/api/movie/video/{id}", missingId))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteMovieById_shouldReturnOkStatus() throws Exception {
        Long movieId = 1L;

        String mockResponse = "Movie deleted successfully";
        when(movieService.deleteMovieById(movieId)).thenReturn(mockResponse);

        mockMvc.perform(delete("/rest/api/movie/delete/{id}", movieId))
                .andExpect(status().isOk())
                .andExpect(content().string(mockResponse));
    }


    @Test
    void updateMovie_shouldReturnOkStatus() throws Exception {
        Long movieId = 1L;

        Movie movie = new Movie();
        movie.setMovieName("Inception");
        movie.setCategory("Movie");

        // Convert Movie object to JSON
        ObjectMapper mapper = new ObjectMapper();
        MockMultipartFile moviePart = new MockMultipartFile(
                "movie", "", "application/json", mapper.writeValueAsBytes(movie));

        MockMultipartFile image = new MockMultipartFile(
                "image", "poster.jpg", "image/jpeg", "random-image-content".getBytes());

        MockMultipartFile video = new MockMultipartFile(
                "video", "trailer.mp4", "video/mp4", "random-video-content".getBytes());

        when(movieService.updateMovie(eq(movieId), any(Movie.class), any(), any()))
                .thenReturn(movie);

        mockMvc.perform(multipart("/rest/api/movie/update")
                        .file(moviePart)
                        .file(image)
                        .file(video)
                        .param("movieId", String.valueOf(movieId))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(content().string("Movie Updated Successfully!"));
    }
}






