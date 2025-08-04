package com.bestflix.movie.security.userService.filter;

import com.bestflix.movie.exception.InvalidTokenException;
import com.bestflix.movie.security.userDetails.MyUserDetailsService;
import com.bestflix.movie.security.userService.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT authentication filter that runs once per request.
 * Intercepts requests to extract and validate JWT tokens from the Authorization header.
 * If valid, sets the security context with authenticated user details.
 */
@Component
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private JWTService jwtService;


    private final MyUserDetailsService myUserDetailsService;


    /**
     * Extracts JWT token from the request header and validates it.
     * If valid, sets the user authentication in the security context.
     *
     * @param request       the HTTP request
     * @param response      the HTTP response
     * @param filterChain   the chain of filters to proceed with
     * @throws ServletException
     * @throws IOException
     */
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {



        String authHeader = request.getHeader("Authorization");
        String token = null;
        String userName = null;

        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            userName = jwtService.extractUserName(token);
        }

        if(userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = myUserDetailsService.loadUserByUsername(userName);

            if(jwtService.validateToken(token,userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                throw new InvalidTokenException(token);
            }

        }

        filterChain.doFilter(request, response);

    }

}
