package com.bestflix.movie.security.repository;

import com.bestflix.movie.security.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for accessing {@link Users} entities.
 * Supports username-based lookup and email-based retrieval.
 */
@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    /**
     * Retrieves a user entity by exact username.
     *
     * @param username the user's login name
     * @return the {@link Users} entity if matched
     */
    Users findByUsername(String username);

    /**
     * Retrieves a user entity by email address.
     *
     * @param email user's registered email
     * @return an {@link Optional} containing the user if matched
     */
    Optional<Users> findByEmail(String email);
}
