package com.bestflix.movie.security.userService;

import com.bestflix.movie.exception.InvalidTokenException;
import com.bestflix.movie.exception.TokenExpiredException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.internal.Function;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Service class responsible for generating, validating, and parsing JWT tokens.
 * Encapsulates all cryptographic and claim-handling operations.
 */
@Service
public class JWTService {

    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * Generates a JWT token for the specified username with 1-hour expiration.
     *
     * @param userName the subject (usually the username) to embed in the token
     * @return a signed JWT token string
     */
    public String generateToken(String userName) {

        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();

    }

    /**
     * Retrieves the signing key from the configured secret.
     *
     * @return a SecretKey for signing/verifying JWTs
     */
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Extracts the username embedded within the token.
     *
     * @param token the JWT token to parse
     * @return the subject/username
     */
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    /**
     * Generic claim extraction logic.
     *
     * @param token         the JWT token to parse
     * @param claimResolver function to resolve a specific claim
     * @return the resolved claim value
     */
    private <T> T extractClaim(String token, Function<Claims,T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    /**
     * Parses token to retrieve all claims.
     *
     * @param token the JWT token
     * @return JWT Claims payload
     * @throws InvalidTokenException if parsing fails
     */
    private Claims extractAllClaims(String token) {

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e){
            throw new InvalidTokenException(token);
        }


    }

    /**
     * Validates token integrity and subject match against provided user details.
     *
     * @param token        the JWT token
     * @param userDetails  user details from security context
     * @return true if token is valid
     * @throws InvalidTokenException or TokenExpiredException as needed
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        if (!userName.equals(userDetails.getUsername())) {
            throw new InvalidTokenException("Username in token doesn't match");
        }
        if (isTokenExpired(token)) {
            throw new TokenExpiredException(token);
        }
        return true;
    }

    /**
     * Checks if token is expired.
     *
     * @param token the JWT token
     * @return true if expired
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts expiration timestamp from token.
     *
     * @param token the JWT token
     * @return Date representing expiration time
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}

