package com.bestflix.movie.security.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing registered users in the system.
 * Includes credentials and contact information with field-level validation constraints.
 */
@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Users {

    /**
     * Unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique username of the user account.
     * Must be between 3 and 30 characters.
     */
    @NotNull(message = "Username cannot be null")
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    private String username;

    /**
     * Encrypted password of the user.
     * Must be at least 6 characters long.
     */
    @NotNull(message = "Password cannot be null")
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    /**
     * Email address associated with the user.
     * Must be in valid format.
     */
    @NotNull(message = "Email cannot be null")
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    private String email;
}

