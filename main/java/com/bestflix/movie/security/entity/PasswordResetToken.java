package com.bestflix.movie.security.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

/**
 * Represents a password reset token issued to a user for password recovery.
 * Stores token string, expiration timestamp, and a one-to-one mapping to the {@link Users} entity.
 */
@Entity
@Table(name = "password_reset_token")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetToken {

    /**
     * Primary key identifier for the token entry.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique token string sent to the user's email for verification.
     */
    @Column(nullable = false, unique = true)
    private String token;

    /**
     * Associated user for whom this token was generated.
     */
    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    /**
     * Timestamp indicating when the token expires.
     */
    private LocalDateTime expiry;
}
