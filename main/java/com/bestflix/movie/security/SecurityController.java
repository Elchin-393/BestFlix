package com.bestflix.movie.security;

import com.bestflix.movie.security.dto.ForgotPasswordRequest;
import com.bestflix.movie.security.dto.ResetPasswordRequest;
import com.bestflix.movie.security.dto.UsersRequest;
import com.bestflix.movie.security.entity.Users;
import com.bestflix.movie.security.userService.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://127.0.0.1:5500")
@AllArgsConstructor
@Tag(name = "Authentication", description = "Handles user registration, login, and password management")
public class SecurityController {

    private final UserService userService;


    /**
     * Registers a new user into the system.
     *
     * @param user the user credentials and profile data
     * @return success message or 400 if required fields are missing
     */
    @Operation(
            summary = "Register new user",
            description = "Creates a new user account with provided credentials"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Missing required fields")
    })
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody Users user) {

        userService.register(user);

        return ResponseEntity.ok("User registered successfully");
    }


    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param user login credentials
     * @return JWT token or 400 if missing fields
     */
    @Operation(
            summary = "User login",
            description = "Verifies user credentials and returns access token"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token returned successfully"),
            @ApiResponse(responseCode = "400", description = "Missing username or password")
    })
    @PostMapping(path = "/login")
    public ResponseEntity<String> login(@Valid @RequestBody UsersRequest user){

        String token = userService.verify(user);
        return ResponseEntity.ok(token);
    }



    /**
     * Initiates password reset process by sending email.
     *
     * @param request the reset request containing email
     * @return success message or 400 if email is invalid
     */
    @Operation(
            summary = "Forgot password",
            description = "Sends password reset link to user if email exists"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reset link sent"),
            @ApiResponse(responseCode = "400", description = "Invalid or missing email")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        String email = request.getEmail();



        userService.generateResetToken(email);

        return ResponseEntity.ok("Reset link sent if email exists");
    }



    /**
     * Resets the user's password using the provided reset token.
     *
     * @param request contains token and new password
     * @return success or failure message
     */
    @Operation(
            summary = "Reset password",
            description = "Resets user password if token is valid"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successful"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired token")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        String token       = request.getToken();
        String newPassword = request.getNewPassword();



        boolean ok = userService.resetPassword(token, newPassword);
        if (ok) {
            return ResponseEntity.ok("Password successfully reset");
        } else {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired token");
        }
    }
}




