package com.bestflix.movie.repository;

import com.bestflix.movie.entity.UsersMovie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for managing associations between Users and Movies.
 * Facilitates queries like finding movies uploaded by a user and managing user-movie relationships.
 */
@Repository
public interface UsersMovieRepository extends JpaRepository<UsersMovie, Long> {

    /**
     * Finds all movie associations for the given username.
     *
     * @param username the unique username of the user
     * @return list of UsersMovie entities associated with that user
     */
    List<UsersMovie> findByUserUsername(String username);

    /**
     * Deletes all user-movie associations for a specific movie.
     *
     * @param movieId the ID of the movie whose associations should be removed
     */
    void deleteAllByMovieId(Long movieId);

    /**
     * Finds the user-movie relation for a given movie.
     *
     * @param movieId the ID of the movie
     * @return the UsersMovie entity linking a user to the specified movie
     */
    UsersMovie findByMovieId(Long movieId);
}
