package com.bestflix.movie.service;

import com.bestflix.movie.entity.Movie;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IMovieService {
     Movie uploadMovie(String token, Movie movie, MultipartFile image, MultipartFile video) throws IOException;

     List<Movie> getMovies();

    List<Movie> getMyMovies(String username);

    Optional<Movie> getMovieById(Long id);


    String deleteMovieById(Long id);

    Movie updateMovie(Long id, Movie movie, MultipartFile image, MultipartFile video) throws IOException;

}
