package com.bestflix.movie.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload for user registration or authentication requests.
 * Includes validation rules for username and password fields.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsersRequest {

    /**
     * Unique username for the account.
     * Must be between 3 and 30 characters.
     */
    @NotNull(message = "Username cannot be null")
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    private String username;

    /**
     * Password used for authentication.
     * Must be at least 6 characters long.
     */
    @NotNull(message = "Password cannot be null")
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;
}
