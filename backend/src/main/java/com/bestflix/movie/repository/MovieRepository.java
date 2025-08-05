package com.bestflix.movie.repository;

import com.bestflix.movie.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Movie entities.
 * Provides CRUD operations and supports custom queries via JPA.
 */
@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    // Inherits: save, findById, delete, findAll, etc.
}
