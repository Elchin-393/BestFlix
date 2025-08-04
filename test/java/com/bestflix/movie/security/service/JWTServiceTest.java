package com.bestflix.movie.security.service;

import com.bestflix.movie.exception.InvalidTokenException;
import com.bestflix.movie.security.userService.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JWTServiceTest {

    private JWTService jwtService;

    private final String secret = "your-256-bit-secret-your-256-bit-secret";

    @BeforeEach
    void setUp() {
        jwtService = new JWTService();
        ReflectionTestUtils.setField(jwtService, "secretKey", secret);
    }

    @Test
    void generateToken_shouldIncludeCorrectSubject() {
        String token = jwtService.generateToken("elcin");

        String extractedUsername = jwtService.extractUserName(token);

        assertEquals("elcin", extractedUsername);
    }

    @Test
    void validateToken_shouldReturnTrueForValidToken() {
        String token = jwtService.generateToken("elcin");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("elcin");

        assertTrue(jwtService.validateToken(token, userDetails));
    }

    @Test
    void validateToken_shouldThrowInvalidTokenException_whenUsernameMismatch() {
        String token = jwtService.generateToken("elcin");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("unknownUser");

        assertThrows(InvalidTokenException.class, () -> jwtService.validateToken(token, userDetails));
    }
}
