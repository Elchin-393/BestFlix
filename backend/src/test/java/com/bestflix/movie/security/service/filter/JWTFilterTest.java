package com.bestflix.movie.security.service.filter;

import com.bestflix.movie.exception.InvalidTokenException;
import com.bestflix.movie.security.userDetails.MyUserDetailsService;
import com.bestflix.movie.security.userService.JWTService;
import com.bestflix.movie.security.userService.filter.JwtFilter;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JWTFilterTest {

    @Mock
    private JWTService jwtService;

    @Mock
    private MyUserDetailsService userDetailsService;

    @Mock
    private UserDetails userDetails;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtFilter jwtFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void shouldAuthenticateWhenTokenIsValid() throws Exception {
        String token = "valid.jwt.token";
        String username = "testUser";

        request.addHeader("Authorization", "Bearer " + token);

        when(jwtService.extractUserName(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.validateToken(token, userDetails)).thenReturn(true);
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());

        jwtFilter.doFilterInternal(request, response, filterChain);

        Assertions.assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldSkipAuthenticationIfNoHeader() throws Exception {
        jwtFilter.doFilterInternal(request, response, filterChain);

        Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldThrowExceptionForInvalidToken() throws Exception {
        String token = "bad.token";
        String username = "testUser";

        request.addHeader("Authorization", "Bearer " + token);

        when(jwtService.extractUserName(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.validateToken(token, userDetails)).thenReturn(false);

        Assertions.assertThrows(InvalidTokenException.class, () ->
                jwtFilter.doFilterInternal(request, response, filterChain)
        );
    }
}

