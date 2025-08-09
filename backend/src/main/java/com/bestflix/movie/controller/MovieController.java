package com.bestflix.movie.controller;

import com.bestflix.movie.entity.Movie;
import com.bestflix.movie.repository.UsersMovieRepository;
import com.bestflix.movie.service.IMovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("rest/api/movie/")
@Tag(name = "Movies", description = "Operations related to uploading, fetching, streaming and managing movies")
public class MovieController {

    private final IMovieService movieService;
    private final S3Client s3Client;

    @Value("${cloud.aws.bucket.name}")
    private String bucketName;


    /**
     * Retrieves all movies available in the system.
     *
     * @return list of all movies
     */
    @Operation(summary = "Fetch all available movies")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of movies")
    @GetMapping(path = "/all")
    public ResponseEntity<List<Movie>> getAllMovies() {
        List<Movie> movies = movieService.getMovies();
        return ResponseEntity.ok(movies);
    }


    /**
     * Retrieves movies uploaded by a specific user.
     *
     * @param requestBody map containing the username
     * @return list of movies uploaded by the user
     */
    @Operation(summary = "Fetch movies uploaded by a specific user")
    @ApiResponse(responseCode = "200", description = "Movies fetched by the given username")
    @Transactional
    @PostMapping(path = "/mymovies")
    public ResponseEntity<List<Movie>> getMyMovies(@RequestBody Map<String, String> requestBody) {
        String username = requestBody.get("username");
        List<Movie> myMovies = movieService.getMyMovies(username);
        return ResponseEntity.ok(myMovies);
    }


    /**
     * Retrieves a movie by its ID.
     *
     * @param id movie ID
     * @return movie details or 404 if not found
     */
    @Operation(summary = "Fetch a specific movie by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movie found"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    @GetMapping(path = "/{id}")
    public ResponseEntity<Optional<Movie>> getMovie(@PathVariable Long id){
        return ResponseEntity.ok(movieService.getMovieById(id));
    }


    /**
     * Uploads a new movie with image and video files.
     *
     * @param username the uploader's username
     * @param movie movie metadata
     * @param image image file
     * @param video video file
     * @return success message or error
     */
    @Operation(
            summary = "Upload a new movie with image and video",
            description = "Requires multipart/form-data and a valid username",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Movie uploaded successfully"),
            @ApiResponse(responseCode = "500", description = "Uploading failed")
    })
    @PostMapping(path = "/upload")
    public ResponseEntity<?> uploadMovie(
            @RequestParam String username,
            @RequestPart Movie movie,
            @RequestPart MultipartFile image,
            @RequestPart MultipartFile video) {

        try {
          movieService.uploadMovie(username, movie, image, video);
            return new ResponseEntity<>("Movie Uploaded Successfully!", HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>("Uploading Failed!", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }



    /**
     * Fetches the image associated with a movie.
     *
     * @param id movie ID
     * @return image byte array or 404
     */
    @Operation(summary = "Fetch image for a specific movie")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Image found"),
            @ApiResponse(responseCode = "404", description = "Image not found")
    })
    @GetMapping("/image/{id}")
    public ResponseEntity<?> getMovieImage(@PathVariable Long id) {
        return movieService.getMovieById(id)
                .map(movie -> {
                    try {
                        // Ensure the key includes the folder prefix
                        String key = "images/" + movie.getImageName(); // e.g., "images/movie123.jpg"

                        byte[] imageBytes = s3Client.getObject(GetObjectRequest.builder()
                                .bucket(bucketName)
                                .key(key)
                                .build()).readAllBytes();

                        return ResponseEntity.ok()
                                .header("Content-Type", movie.getImageType())
                                .body(imageBytes);

                    } catch (IOException e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    } catch (S3Exception e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                    }
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }




    /**
     * Streams the video file for a specific movie.
     *
     * @param id movie ID
     * @return video stream or 404
     */
    @Operation(summary = "Stream video for a specific movie")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Video streaming initiated"),
            @ApiResponse(responseCode = "404", description = "Video not found")
    })
    @GetMapping("/video/{id}")
    public ResponseEntity<?> getVideo(@PathVariable Long id) {
        return movieService.getMovieById(id)
                .map(movie -> {
                    try {
                        // Ensure the key includes the folder prefix
                        String key = "videos/" + movie.getVideoName(); // e.g., "videos/movie123.mp4"

                        byte[] videoBytes = s3Client.getObject(GetObjectRequest.builder()
                                .bucket(bucketName)
                                .key(key)
                                .build()).readAllBytes();

                        return ResponseEntity.ok()
                                .contentType(MediaType.valueOf(movie.getVideoType())) // e.g., "video/mp4"
                                .contentLength(videoBytes.length)
                                .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + movie.getVideoName())
                                .body(videoBytes);

                    } catch (IOException e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    } catch (S3Exception e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                    }
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    /**
     * Deletes a movie by ID.
     *
     * @param id movie ID
     * @return confirmation message or 404
     */
    @Operation(
            summary = "Delete a movie by ID",
            description = "Requires authentication and valid movie ID",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movie deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    @Transactional
    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<?> deleteMovieByUsername(@PathVariable Long id){
        return ResponseEntity.ok(movieService.deleteMovieById(id));
    }



    /**
     * Updates a movie with new details and files.
     *
     * @param movieId ID of the movie to update
     * @param movie updated metadata
     * @param image updated image
     * @param video updated video
     * @return success or error message
     */
    @Operation(
            summary = "Update a movie with new metadata and files",
            description = "Multipart update endpoint that replaces movie data",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movie updated successfully"),
            @ApiResponse(responseCode = "500", description = "Update failed due to server error")
    })
    @Transactional
    @PutMapping(path = "/update")
    public ResponseEntity<String> updateMovie(
            @RequestParam Long movieId,
            @RequestPart Movie movie,
            @RequestPart MultipartFile image,
            @RequestPart MultipartFile video) {

        try {
            movieService.updateMovie(movieId, movie, image, video);
            return new ResponseEntity<>("Movie Updated Successfully!", HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Updating Failed!", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


}
