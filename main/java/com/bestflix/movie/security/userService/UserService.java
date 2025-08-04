package com.bestflix.movie.security.userService;

import com.bestflix.movie.exception.InvalidCredentialsException;
import com.bestflix.movie.exception.InvalidTokenException;
import com.bestflix.movie.exception.TokenExpiredException;
import com.bestflix.movie.exception.UserNotFoundException;
import com.bestflix.movie.security.entity.PasswordResetToken;
import com.bestflix.movie.security.entity.Users;
import com.bestflix.movie.security.dto.UsersRequest;
import com.bestflix.movie.security.repository.PasswordResetTokenRepository;
import com.bestflix.movie.security.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


/**
 * Service class for user-related operations such as registration,
 * authentication, password reset and token lifecycle management.
 */
@Service
@Transactional
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;
    private PasswordResetTokenRepository resetTokenRepository;
    private AuthenticationManager authManager;
    private JWTService jwtService;
    private PasswordEncoder passwordEncoder;
    private EmailService emailService;


    /**
     * Registers a new user by encoding their password and persisting to database.
     *
     * @param user a {@link Users} entity with plain-text password
     * @return the saved {@link Users} entity
     */
    public Users register(Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }


    /**
     * Authenticates a user and issues a JWT token upon success.
     *
     * @param user the login credentials
     * @return JWT token if authentication succeeds
     * @throws InvalidCredentialsException on failed authentication
     */
    public String verify(UsersRequest user) {
        try {
            Authentication authentication =
                    authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

            if (authentication.isAuthenticated()) {
                return jwtService.generateToken(user.getUsername());
            } else {
                throw new InvalidCredentialsException(user.getUsername());
            }

        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException(user.getUsername());
        }

    }


    /**
     * Generates and stores a password reset token for the given user email.
     * Sends token via email with reset link.
     *
     * @param email the user's registered email
     * @return generated token string
     * @throws UserNotFoundException if email is not registered
     */
    public String generateResetToken(String email) {
        Optional<Users> user = userRepository.findByEmail(email);
        if (user.isEmpty() || user == null)
            throw new UserNotFoundException("User with email" + email);

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();

        resetToken.setToken(token);
        resetToken.setUser(user.get());
        resetToken.setExpiry(LocalDateTime.now().plusMinutes(30));

        resetTokenRepository.save(resetToken);

        emailService.sendPasswordResetEmail(user.get().getEmail(), token);

        return token;
    }



    /**
     * Resets the user's password using a valid token.
     * Deletes used token after successful operation.
     *
     * @param token       the password reset token
     * @param newPassword the new password to set
     * @return true if reset succeeds
     * @throws InvalidTokenException or TokenExpiredException if token is invalid/expired
     */
    public boolean resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> maybeToken = resetTokenRepository.findByToken(token);
        if (maybeToken.isEmpty()) {
            throw new InvalidTokenException(token);
        }

        PasswordResetToken resetToken = maybeToken.get();

        if (resetToken.getExpiry().isBefore(LocalDateTime.now())) {
            resetTokenRepository.delete(resetToken);
            throw new TokenExpiredException("Reset token has expired");
        }

        Users user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetTokenRepository.delete(resetToken);
        return true;
    }

}



