package com.prudhviraj.security.security.service.impl;

import com.prudhviraj.security.security.entities.User;
import com.prudhviraj.security.security.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Set;

@Service
public class JwtServiceImpl  implements JwtService{
    Logger log = LoggerFactory.getLogger(JwtServiceImpl.class);

    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    private SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }
    @Override
    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("role", Set.of("ADMIN", "USER"))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60))
                .signWith(getSecretKey())
                .compact();

    }

    /**
     * Generates an access token for the provided user. The access token is valid for 10 minutes.
     *
     * @param user the user for whom the access token is being generated
     * @return the generated access token as a String
     *
     * <p>
     * This method generates a JWT access token by embedding the user's ID in the token's subject field,
     * along with their email and roles in the token claims. The access token has a validity period of 10 minutes.
     * </p>
     */
    @Override
    public String generateAccessToken(User user) {
        // Log the start of access token generation
        log.info("Generating access token for user ID: {}", user.getId());

        // Generate the access token using JWT builder
        String accessToken = Jwts.builder()
                // Set the user's ID as the subject of the token
                .subject(user.getId().toString())
                // Include the user's email and role as claims
                .claim("email", user.getEmail())
                .claim("roles", user.getRoles().toString())
                // Set the token's issued date to the current time
                .issuedAt(new Date())
                // Set the token's expiration time to 10 minutes from the current time
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10)) // 10 minutes validity
                // Sign the token with a secret key
                .signWith(getSecretKey())
                // Build the token as a compact JWT string
                .compact();

        // Log the successful generation of the access token
        log.debug("Generated access token for user ID: {}", user.getId());

        // Return the generated token
        return accessToken;
    }

    /**
     * Generates a refresh token for the provided user. The refresh token is valid for 6 months.
     *
     * @param user the user for whom the refresh token is being generated
     * @return the generated refresh token as a String
     *
     * <p>
     * This method generates a JWT refresh token by embedding the user's ID in the token's subject field.
     * The refresh token has a validity period of 6 months from the time of issue.
     * </p>
     */
    @Override
    public String generateRefreshToken(User user) {
        // Log the start of refresh token generation
        log.info("Generating refresh token for user ID: {}", user.getId());

        // Generate the refresh token using JWT builder
        String refreshToken = Jwts.builder()
                // Set the user's ID as the subject of the token
                .subject(user.getId().toString())
                // Set the token's issued date to the current time
                .issuedAt(new Date())
                // Set the token's expiration time to 6 months from the current time
                .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 30 * 6)) // 6 months validity
                // Sign the token with a secret key
                .signWith(getSecretKey())
                // Build the token as a compact JWT string
                .compact();

        // Log the successful generation of the refresh token
        log.debug("Generated refresh token for user ID: {}", user.getId());

        // Return the generated token
        return refreshToken;
    }


    @Override
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return Long.valueOf(claims.getSubject());
    }

}
