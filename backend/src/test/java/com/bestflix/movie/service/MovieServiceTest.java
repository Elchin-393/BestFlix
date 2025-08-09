package com.bestflix.movie.service;

import com.bestflix.movie.entity.Movie;
import com.bestflix.movie.entity.UsersMovie;
import com.bestflix.movie.exception.MovieNotFoundException;
import com.bestflix.movie.exception.UserMoviesNotFoundException;
import com.bestflix.movie.exception.UserNotFoundException;
import com.bestflix.movie.repository.MovieRepository;
import com.bestflix.movie.repository.UsersMovieRepository;
import com.bestflix.movie.security.entity.Users;
import com.bestflix.movie.security.repository.UserRepository;
import com.bestflix.movie.service.impl.FileStorageService;
import com.bestflix.movie.service.impl.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UsersMovieRepository usersMovieRepository;

    @InjectMocks
    private MovieService movieService;

    @Mock
    private MultipartFile imageFile;

    @Mock
    private MultipartFile videoFile;

    @Mock
    private S3Client s3Client;


    private final String bucketName = "test-bucket";
    private final String region = "eu-central-1";


    Users user;
    Movie movie;
    UsersMovie usersMovie;
    @BeforeEach
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        user = Users.builder()
                .username("Elcin")
                .email("Mamedovelchin@gmail.com")
                .password("111111")
                .build();


        movie = Movie.builder()
                .movieName("Room")
                .country("Tailand")
                .releaseDate(LocalDate.now())
                .casts("Jenifer Lawrence")
                .duration("230 min")
                .about("Very Interesting Story")
                .category("Movie")
                .imageName("ifie-3242dss-ssdfs")
                .imageType("jpeg")
                .videoName("jewrm3-3453r34-34f")
                .videoType("MP4")
                .imageData("Success".getBytes())
                .videoPath("C:/Users/User/Desktop/BestFlix/video")
                .build();



        usersMovie = UsersMovie.builder()
                .user(user)
                .movie(movie)
                .build();

        MovieService movieService = new MovieService(
                fileStorageService,
                movieRepository,
                userRepository,
                usersMovieRepository,
                s3Client
        );

        // Inject @Value fields manually
        Field bucketField = MovieService.class.getDeclaredField("bucketName");
        bucketField.setAccessible(true);
        bucketField.set(movieService, bucketName);

        Field regionField = MovieService.class.getDeclaredField("region");
        regionField.setAccessible(true);
        regionField.set(movieService, region);




}

    @Test
    void uploadMovie_shouldUploadSuccessfully() throws IOException {
        // Arrange
        String username = "Elcin";
        Users mockUser = Users.builder()
                .username(username)
                .email("elcin@gmail.com")
                .password("1234")
                .build();

        Movie inputMovie = Movie.builder()
                .movieName("Interstellar")
                .country("USA")
                .releaseDate(LocalDate.of(2014, 11, 7))
                .casts("Matthew McConaughey, Anne Hathaway")
                .duration("2h 49m")
                .about("Sci-fi space adventure")
                .category("Sci-Fi")
                .build();


        byte[] imageBytes = new byte[] {1, 2, 3};
        String savedImageName = "image123.jpg";
        String savedVideoName = "video123.mp4";
        String videoPath = "https://test-bucket.s3.eu-central-1.amazonaws.com/video123.mp4";

        when(userRepository.findByUsername(username)).thenReturn(mockUser);
        when(fileStorageService.saveImage(imageFile)).thenReturn(savedImageName);
        when(fileStorageService.saveVideo(videoFile)).thenReturn(savedVideoName);
        when(imageFile.getContentType()).thenReturn("image/jpeg");
        when(videoFile.getContentType()).thenReturn("video/mp4");

        when(fileStorageService.saveVideo(videoFile)).thenReturn(videoPath);


        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Movie result = movieService.uploadMovie(username, inputMovie, imageFile, videoFile);

        // Assert
        assertNotNull(result);

        assertEquals("USA", result.getCountry());
        assertEquals(savedImageName, result.getImageName());
        assertEquals(videoPath, result.getVideoName());
        assertEquals("image/jpeg", result.getImageType());

        verify(usersMovieRepository).save(any(UsersMovie.class));

    }

    @Test
    void uploadMovie_shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        when(userRepository.findByUsername("UnknownUser")).thenReturn(null);

        Movie movie = new Movie();
        MultipartFile image = mock(MultipartFile.class);
        MultipartFile video = mock(MultipartFile.class);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            movieService.uploadMovie("UnknownUser", movie, image, video);
        });
    }

    @Test
    void test_get_myMovies(){
        //arrange
        when(usersMovieRepository.findByUserUsername("Elcin")).thenReturn(List.of(usersMovie));
        //act
        List<UsersMovie> result = usersMovieRepository.findByUserUsername("Elcin");

        //Assert

        assertNotNull(result);
        assertEquals(1,result.size());
        assertEquals("Elcin", result.get(0).getUser().getUsername());
        assertEquals("Room", result.get(0).getMovie().getMovieName());

    }

    @Test
    void should_throwMovieNotFoundException(){
        when(usersMovieRepository.findByUserUsername("Invalid")).thenReturn(null);

        assertThrows(MovieNotFoundException.class, () -> {
            movieService.getMyMovies("Invalid");
        });
    }

    @Test
    void test_get_Movies(){
        //arrange
        when(movieRepository.findAll()).thenReturn(List.of(movie));
        //act
        List<Movie> result = movieRepository.findAll();

        //Assert

        assertNotNull(result);
        assertEquals(1,result.size());
        assertEquals("Room", result.get(0).getMovieName());

    }

    @Test
    void should_throwMovieNotFoundException_for_getMovies(){
        when(movieRepository.findAll()).thenReturn(null);

        assertThrows(MovieNotFoundException.class, () -> {
            movieService.getMovies();
        });
    }

    @Test
    void test_get_Movie_By_Id(){
        //arrange
        when(movieRepository.findById(movie.getId())).thenReturn(Optional.of(movie));
        //act
        Optional<Movie> result = movieRepository.findById(movie.getId());

        //Assert

        assertNotNull(result);
        assertEquals("Room", result.get().getMovieName());

    }

    @Test
    void should_throwMovieNotFoundException_for_getMovieById(){
        when(movieRepository.findById(4L)).thenReturn(null);

        assertThrows(MovieNotFoundException.class, () -> {
            movieService.getMovieById(4L);
        });
    }

    @Test
    void test_Delete_Movie_By_Id() {

        when(movieRepository.findById(movie.getId())).thenReturn(Optional.of(movie));

        String result = movieService.deleteMovieById(movie.getId());

        verify(usersMovieRepository).deleteAllByMovieId(movie.getId());
        verify(movieRepository).deleteById(movie.getId());

        assertEquals("Movie deleted completely", result);
    }

    @Test
    void updateMovie_shouldUpdateAndReturnMovie() throws IOException {
        // Arrange
        Long movieId = 1L;

        Movie existingMovie = new Movie();
        existingMovie.setId(movieId);

        Movie movieInput = new Movie();
        movieInput.setMovieName("Updated Name");
        movieInput.setCountry("Updated Country");
        movieInput.setReleaseDate(LocalDate.of(2024, 1, 1));
        movieInput.setCasts("Updated Casts");
        movieInput.setDuration("2h");
        movieInput.setAbout("Updated About");
        movieInput.setCategory("Action");

        MultipartFile imageMock = mock(MultipartFile.class);
        MultipartFile videoMock = mock(MultipartFile.class);

        // Mock image & video
        when(imageMock.getContentType()).thenReturn("image/png");
        when(fileStorageService.saveImage(imageMock)).thenReturn("savedImage.png");

        when(videoMock.getContentType()).thenReturn("video/mp4");
        when(fileStorageService.saveVideo(videoMock)).thenReturn("savedVideo.mp4");
        when(fileStorageService.saveVideo(videoMock)).thenReturn("savedVideo.mp4");

        // Repositories
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(existingMovie));
        when(usersMovieRepository.findByMovieId(movieId)).thenReturn(new UsersMovie());

        // Act
        Movie result = movieService.updateMovie(movieId, movieInput, imageMock, videoMock);

        // Assert
        assertEquals("Updated Name", result.getMovieName());
        assertEquals("image/png", result.getImageType());
        assertEquals("video/mp4", result.getVideoType());
        assertEquals("savedImage.png", result.getImageName());
        assertEquals("savedVideo.mp4", result.getVideoName());
        assertEquals("https://null.s3.null.amazonaws.com/savedVideo.mp4", result.getVideoPath());

        verify(movieRepository).findById(movieId);
        verify(fileStorageService).saveImage(imageMock);
        verify(fileStorageService).saveVideo(videoMock);
        verify(fileStorageService).saveVideo(videoMock);
        verify(movieRepository).save(any(Movie.class));
        verify(usersMovieRepository).findByMovieId(movieId);
        verify(usersMovieRepository).save(any(UsersMovie.class));
    }


    @Test
    void updateMovie_shouldThrowMovieNotFoundException_whenMovieDoesNotExist() {
        Long movieId = 999L;
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());


        assertThrows(MovieNotFoundException.class, () -> {
            movieService.updateMovie(movieId, new Movie(), mock(MultipartFile.class), mock(MultipartFile.class));
        });

        verify(movieRepository).findById(movieId);
        verifyNoMoreInteractions(movieRepository);
    }


    @Test
    void updateMovie_shouldThrowUserMoviesNotFoundException_whenUsersMovieIsNull() {
        Long movieId = 1L;
        Movie existing = new Movie();
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(existing));
        when(usersMovieRepository.findByMovieId(movieId)).thenReturn(null);

        assertThrows(UserMoviesNotFoundException.class, () -> {
            movieService.updateMovie(movieId, new Movie(), mock(MultipartFile.class), mock(MultipartFile.class));
        });

        verify(movieRepository).findById(movieId);
        verify(usersMovieRepository).findByMovieId(movieId);
    }

}


