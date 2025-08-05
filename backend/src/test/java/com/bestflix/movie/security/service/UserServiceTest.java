package com.bestflix.movie.security.service;

import com.bestflix.movie.exception.InvalidCredentialsException;
import com.bestflix.movie.exception.InvalidTokenException;
import com.bestflix.movie.exception.TokenExpiredException;
import com.bestflix.movie.exception.UserNotFoundException;
import com.bestflix.movie.security.dto.UsersRequest;
import com.bestflix.movie.security.entity.PasswordResetToken;
import com.bestflix.movie.security.entity.Users;
import com.bestflix.movie.security.repository.PasswordResetTokenRepository;
import com.bestflix.movie.security.repository.UserRepository;
import com.bestflix.movie.security.userService.EmailService;
import com.bestflix.movie.security.userService.JWTService;
import com.bestflix.movie.security.userService.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordResetTokenRepository resetTokenRepository;
    @Mock private AuthenticationManager authManager;
    @Mock private JWTService jwtService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private EmailService emailService;

    @InjectMocks private UserService userService;


    @Test
    void testRegister_ShouldEncodePasswordAndSaveUser() {
        Users user = new Users();
        user.setPassword("rawPassword");

        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(Users.class))).thenAnswer(i -> i.getArguments()[0]);

        Users result = userService.register(user);

        assertEquals("encodedPassword", result.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void testVerify_ShouldReturnTokenIfAuthenticated() {
        UsersRequest req = new UsersRequest("user", "pass");
        Authentication auth = mock(Authentication.class);

        when(authManager.authenticate(any())).thenReturn(auth);
        when(auth.isAuthenticated()).thenReturn(true);
        when(jwtService.generateToken("user")).thenReturn("jwt-token");

        String token = userService.verify(req);
        assertEquals("jwt-token", token);
    }

    @Test
    void testVerify_ShouldThrowExceptionIfInvalid() {
        UsersRequest req = new UsersRequest("user", "wrong");

        when(authManager.authenticate(any())).thenThrow(new BadCredentialsException("bad creds"));

        assertThrows(InvalidCredentialsException.class, () -> userService.verify(req));
    }

    @Test
    void testGenerateResetToken_ShouldCreateAndSendEmail() {
        Users user = new Users();
        user.setEmail("test@gmail.com");
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));

        String token = userService.generateResetToken("test@gmail.com");

        assertNotNull(token);
        verify(resetTokenRepository).save(any());
        verify(emailService).sendPasswordResetEmail(eq("test@gmail.com"), eq(token));
    }

    @Test
    void testGenerateResetToken_ShouldThrowIfUserNotFound() {
        when(userRepository.findByEmail("none@gmail.com")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.generateResetToken("none@gmail.com"));
    }

    @Test
    void testResetPassword_ShouldResetAndDeleteToken() {
        Users user = new Users();
        PasswordResetToken token = new PasswordResetToken();
        token.setToken("abc");
        token.setUser(user);
        token.setExpiry(LocalDateTime.now().plusMinutes(10));

        when(resetTokenRepository.findByToken("abc")).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("newpass")).thenReturn("hashed");

        boolean result = userService.resetPassword("abc", "newpass");

        assertTrue(result);
        verify(userRepository).save(user);
        verify(resetTokenRepository).delete(token);
    }

    @Test
    void testResetPassword_ShouldThrowIfTokenNotFound() {
        when(resetTokenRepository.findByToken("notFound")).thenReturn(Optional.empty());
        assertThrows(InvalidTokenException.class, () -> userService.resetPassword("notFound", "pass"));
    }

    @Test
    void testResetPassword_ShouldThrowIfTokenExpired() {
        PasswordResetToken token = new PasswordResetToken();
        token.setExpiry(LocalDateTime.now().minusMinutes(1));
        when(resetTokenRepository.findByToken("expired")).thenReturn(Optional.of(token));

        assertThrows(TokenExpiredException.class, () -> userService.resetPassword("expired", "pass"));
        verify(resetTokenRepository).delete(token);
    }
}
