package com.bestflix.movie.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload for resetting a user's password using a valid token.
 * Ensures the token is provided and the new password meets minimum security standards.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {

    /**
     * Password reset token sent to the user.
     */
    private String token;

    /**
     * New password to be set for the user account.
     * Must be at least 6 characters long.
     */
    @NotNull(message = "Password cannot be null")
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String newPassword;
}
