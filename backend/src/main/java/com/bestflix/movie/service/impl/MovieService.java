package com.bestflix.movie.service.impl;

import com.bestflix.movie.entity.Movie;
import com.bestflix.movie.entity.UsersMovie;
import com.bestflix.movie.exception.MovieNotFoundException;
import com.bestflix.movie.exception.UserMoviesNotFoundException;
import com.bestflix.movie.exception.UserNotFoundException;
import com.bestflix.movie.repository.MovieRepository;
import com.bestflix.movie.repository.UsersMovieRepository;
import com.bestflix.movie.security.entity.Users;
import com.bestflix.movie.security.repository.UserRepository;
import com.bestflix.movie.service.IMovieService;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service implementation for handling Movie-related operations such as upload, fetch, update, and delete.
 * Interacts with repositories and file storage services for managing media content and user-movie relationships.
 */

@Service
public class MovieService implements IMovieService {

    private final FileStorageService fileStorageService;
    private final VideoFileStorageService videoFileStorageService;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final UsersMovieRepository usersMovieRepository;

    public MovieService(FileStorageService fileStorageService, VideoFileStorageService videoFileStorageService, MovieRepository movieRepository,
                 UserRepository userRepository, UsersMovieRepository usersMovieRepository){
        this.fileStorageService = fileStorageService;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.usersMovieRepository = usersMovieRepository;
        this.videoFileStorageService = videoFileStorageService;
    }


    /**
     * Uploads a movie along with its image and video files, associates it with a user, and persists it to the database.
     *
     * <p><strong>Workflow:</strong></p>
     * <ul>
     *   <li>Validates and stores image and video files using FileStorageService and VideoFileStorageService.</li>
     *   <li>Builds a Movie entity including file metadata and binary content.</li>
     *   <li>Saves the Movie entity in the MovieRepository.</li>
     *   <li>Looks up the uploading user by username via UserRepository.</li>
     *   <li>If found, associates the user with the uploaded movie via UsersMovieRepository.</li>
     * </ul>
     *
     * @param username the unique username of the user uploading the movie
     * @param movie the Movie object containing metadata (title, cast, category, etc.)
     * @param image the image file (e.g. poster or thumbnail) to be saved
     * @param video the video file representing the movie content
     * @return the saved Movie entity with populated image/video metadata and persisted content
     * @throws IOException if an error occurs during file reading or saving
     * @throws UserNotFoundException if the provided username does not match any existing user
     */
    @Override
    public Movie uploadMovie(String username, Movie movie, MultipartFile image, MultipartFile video) throws IOException {



        Movie movieFile = Movie.builder()

                .movieName(movie.getMovieName())
                .country(movie.getCountry())
                .releaseDate(movie.getReleaseDate())
                .casts(movie.getCasts())
                .duration(movie.getDuration())
                .about(movie.getAbout())
                .category(movie.getCategory())
                .imageName(fileStorageService.save(image))
                .imageType(image.getContentType())
                .imageData(image.getBytes())
                .videoName(fileStorageService.save(video))
                .videoType(video.getContentType())
                .videoPath(videoFileStorageService.save(video))

                .build();

        Movie savedMovie = movieRepository.save(movieFile);

        Users user = userRepository.findByUsername(username);

        if(user == null)
            throw new UserNotFoundException(username);

        UsersMovie myMovie = UsersMovie.builder()

                .user(user)
                .movie(movieFile)
                .build();

        usersMovieRepository.save(myMovie);

        return savedMovie;
    }



    /**
     * Retrieves all movies uploaded by the specified user.
     * Uses transactional consistency to ensure associated data is fully fetched.
     *
     * @param username the unique identifier of the user
     * @return list of Movies owned by the user
     * @throws MovieNotFoundException if the user has no associated movies
     */
    @Transactional
    @Override
    public List<Movie> getMyMovies(String username)  {
        List<UsersMovie> userMovies = usersMovieRepository.findByUserUsername(username);

        if(userMovies == null || userMovies.isEmpty())
            throw new MovieNotFoundException();

        List<Movie> allMyMovies = userMovies.stream()
                .map(UsersMovie::getMovie)
                .collect(Collectors.toList());

        return allMyMovies;
    }



    /**
     * Retrieves all movies in the system from the repository.
     * Used for homepage listings, search, or catalog population.
     *
     * @return list of all Movie entities
     * @throws MovieNotFoundException if no movies are available in the database
     */
    @Transactional
    @Override
    public List<Movie> getMovies() {

        List<Movie> movies = movieRepository.findAll();

        if(movies == null || movies.isEmpty())
            throw new MovieNotFoundException();

        return movies;
    }



    /**
     * Fetches a Movie by its unique identifier.
     *
     * @param id the ID of the movie
     * @return Optional container with Movie if found
     * @throws MovieNotFoundException if the movie is missing or was deleted
     */
    @Override
    public Optional<Movie> getMovieById(Long id) {

        Optional<Movie> movie = movieRepository.findById(id);

        if(movie == null || movie.isEmpty())
            throw new MovieNotFoundException();

        return movie;
    }


    /**
     * Deletes a movie and its associated user mappings.
     * Ensures referential integrity by removing `UsersMovie` references before deletion.
     *
     * @param id the ID of the movie to delete
     * @return confirmation message upon successful deletion
     */
    @Override
    public String deleteMovieById(Long id) {
        usersMovieRepository.deleteAllByMovieId(id);

        movieRepository.deleteById(id);

        return "Movie deleted completely";
    }




    /**
     * Updates an existing movie entry with new metadata and media files.
     * Replaces image/video files and updates `UsersMovie` references.
     *
     * @param movieId ID of the movie to be updated
     * @param movie updated metadata fields
     * @param image new image file (poster/thumbnail)
     * @param video new video file
     * @return updated Movie entity reflecting new media and fields
     * @throws IOException if file processing fails
     * @throws MovieNotFoundException if the target movie doesn't exist
     * @throws UserMoviesNotFoundException if the movie-user relationship is missing
     */
    @Override
    public Movie updateMovie(Long movieId, Movie movie, MultipartFile image, MultipartFile video) throws IOException {

        Movie updatedMovie = movieRepository.findById(movieId).orElseThrow(()-> new MovieNotFoundException());


        updatedMovie.setMovieName(movie.getMovieName());
        updatedMovie.setCountry(movie.getCountry());
        updatedMovie.setReleaseDate(movie.getReleaseDate());
        updatedMovie.setCasts(movie.getCasts());
        updatedMovie.setDuration(movie.getDuration());
        updatedMovie.setAbout(movie.getAbout());
        updatedMovie.setCategory(movie.getCategory());
        updatedMovie.setImageName(fileStorageService.save(image));
        updatedMovie.setImageType(image.getContentType());
        updatedMovie.setImageData(image.getBytes());
        updatedMovie.setVideoName(fileStorageService.save(video));
        updatedMovie.setVideoType(video.getContentType());
        updatedMovie.setVideoPath(videoFileStorageService.save(video));

        movieRepository.save(updatedMovie);


        UsersMovie usersMovie = usersMovieRepository.findByMovieId(movieId);

        if(usersMovie == null)
            throw new UserMoviesNotFoundException();

        usersMovie.setMovie(updatedMovie);

        usersMovieRepository.save(usersMovie);


        return updatedMovie;
    }

}
