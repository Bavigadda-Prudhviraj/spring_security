package com.prudhviraj.security.security.handlers;

import com.prudhviraj.security.security.dto.UserDto;
import com.prudhviraj.security.security.entities.User;
import com.prudhviraj.security.security.repository.UserRepository;
import com.prudhviraj.security.security.service.JwtService;
import com.prudhviraj.security.security.service.impl.UserServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class OauthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
     private final UserServiceImpl userService;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;
    public  OauthSuccessHandler( UserServiceImpl userService, JwtService jwtService,ModelMapper modelMapper){
        this.userService = userService;
        this.jwtService = jwtService;
        this.modelMapper = modelMapper;
    }
    @Value("${deployment.env}")
    public String deploymentEnv;
    Logger log = LoggerFactory.getLogger(OauthSuccessHandler.class);
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.debug("OAuth2 authentication success handler triggered.");

        // Cast authentication to OAuth2AuthenticationToken
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) token.getPrincipal();

        // Extract user email from OAuth2 user
        String userEmail = oAuth2User.getAttribute("email");
        log.debug("Extracted email from OAuth2 user: {}", userEmail);

        // Check if the user already exists in the database
        UserDto user = userService.findByEmail(userEmail);
        if (user == null) {
            log.info("User not found, creating a new user with email: {}", userEmail);

            // Create and save the new user
            User newUser = User.builder()
                    .name(oAuth2User.getAttribute("name"))
                    .email(userEmail)
                    .build();
            user = userService.save(newUser);

            log.info("New user created with ID: {}", user.getId());
        } else {
            log.info("User found: {}", user.getId());
        }

        // Generate access and refresh tokens for the user
        log.debug("Generating access token for user: {}", user.getId());
        String accessToken = jwtService.generateAccessToken(modelMapper.map(user, User.class));

        log.debug("Generating refresh token for user: {}", user.getId());
        String refreshToken = jwtService.generateRefreshToken(modelMapper.map(user, User.class));

        // Set the refresh token in an HTTP-only, secure cookie
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true); // Make the cookie HTTP-only
        cookie.setSecure("production".equals(deploymentEnv)); // Secure only in production
        cookie.setPath("/"); // Set the cookie's path to the root
        cookie.setMaxAge(60 * 60); // Set cookie expiration to 1 hour
        response.addCookie(cookie);
        log.debug("Refresh token cookie set for user: {}", user.getId());

        // Build the frontend URL with the access token as a query parameter
        String frontEndURL = "http://localhost:8080/home.html?token=" + accessToken;
        log.info("Redirecting to: {}", frontEndURL);

        // Redirect to the frontend URL with the access token
        //getRedirectStrategy().sendRedirect(request, response, frontEndURL);
        response.sendRedirect(frontEndURL);
    }
}
