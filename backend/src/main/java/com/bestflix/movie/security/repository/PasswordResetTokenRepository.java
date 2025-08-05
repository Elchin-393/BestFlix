package com.bestflix.movie.security.repository;

import com.bestflix.movie.security.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link PasswordResetToken} entities.
 * Provides CRUD operations and custom token lookup.
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /**
     * Retrieves a password reset token entity by its token value.
     *
     * @param token the token string to search
     * @return an {@link Optional} containing the token entity if found
     */
    Optional<PasswordResetToken> findByToken(String token);
}
