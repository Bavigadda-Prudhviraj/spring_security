package com.prudhviraj.security.security.controller;

import com.prudhviraj.security.security.advices.ApiResponse;
import com.prudhviraj.security.security.dto.LoginDto;
import com.prudhviraj.security.security.dto.LoginResponseDto;
import com.prudhviraj.security.security.dto.SignUpDto;
import com.prudhviraj.security.security.dto.UserDto;
import com.prudhviraj.security.security.service.impl.AuthServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthServiceImpl authService;

    // Constructor injection for better testability
    @Autowired
    public AuthController(AuthServiceImpl authService) {
        this.authService = authService;
    }

    @Value("${deployment.env}")
    public String deploymentEnv;

    /**
     * Handles user sign-up.
     *
     * @param signup - DTO containing user sign-up information.
     * @return ResponseEntity containing the created UserDto.
     */
    @PostMapping("/signUp")
    public ResponseEntity<UserDto> signup(@RequestBody SignUpDto signup) {
        log.info("Sign-up request received with email: {}", signup.getEmail());
        UserDto user = authService.signUp(signup);
        log.info("User created successfully with ID: {}", user.getId());
        return ResponseEntity.ok(user);
    }

    /**
     * Handles user login and sets an HTTP-only cookie with the JWT token.
     *
     * @param login - DTO containing login credentials.
     * @param response - HttpServletResponse to add the cookie.
     * @return ResponseEntity containing the token in the response body.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody LoginDto login, HttpServletRequest request, HttpServletResponse response) {
        log.info("Login request received for email: {}", login.getEmail());

        // Attempt to authenticate and get the token
        String token = authService.login(login);

        // Create a secure HTTP-only cookie to store the token
        Cookie cookie = new Cookie("tokenCookie", token);
        cookie.setHttpOnly(true);
        cookie.setSecure("production".equals(deploymentEnv));// Set the Secure flag for the cookie only if the environment is production, ensuring it's sent only over HTTPS
        cookie.setPath("/"); // Set the path for the cookie
        cookie.setMaxAge(60 * 60); // Set cookie expiration to 1 hour
        response.addCookie(cookie);

        log.info("Login successful, token generated for email: {}", login.getEmail());

        ApiResponse<String> tokenToSend = new ApiResponse<>(token);
        return ResponseEntity.ok(tokenToSend);
    }

    @PostMapping("/loginWithAccessAndRefreshToken")
    public ResponseEntity<ApiResponse<LoginResponseDto>> loginWithAccessAndRefreshToken(@RequestBody LoginDto login, HttpServletRequest request, HttpServletResponse response) {
        log.info("Login request received for email: {}", login.getEmail());

        // Attempt to authenticate and get the access and refresh tokens
        LoginResponseDto loginResponseDto = authService.loginWithAccessAndRefreshToken(login);

        // Create a secure HTTP-only cookie to store the refresh token
        Cookie cookie = new Cookie("refreshToken", loginResponseDto.getRefreshToken());
        cookie.setHttpOnly(true);// Make the cookie HTTP-only
        cookie.setSecure("production".equals(deploymentEnv));// Set the Secure flag for the cookie only if the environment is production, ensuring it's sent only over HTTPS
        cookie.setPath("/"); // Set the cookie's path to the root
        cookie.setMaxAge(60 * 60); // Set cookie expiration to 1 hour
        response.addCookie(cookie);

        log.info("Login successful, access and refresh tokens generated for email: {}", login.getEmail());

        // Wrap the token in an ApiResponse object and return it
        ApiResponse<LoginResponseDto> tokenToSend = new ApiResponse<>(loginResponseDto);
        return ResponseEntity.ok(tokenToSend);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<ApiResponse<LoginResponseDto>> refreshToken(HttpServletRequest request) {
        log.info("Processing refresh token request...");

        // Extract the refresh token from the request cookies
        String refreshToken = Arrays.stream(request.getCookies())
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> {
                    log.error("Refresh token not found in the request");
                    return new AuthenticationServiceException("Refresh token not found in the request");
                });

        log.info("Extracted refresh token: {}", refreshToken);

        // Attempt to refresh the access token using the refresh token
        LoginResponseDto loginResponseDto = authService.refreshToken(refreshToken);

        log.info("Access token successfully refreshed for refresh token: {}", refreshToken);

        // Wrap the refreshed token in an ApiResponse object and return it
        ApiResponse<LoginResponseDto> tokenToSend = new ApiResponse<>(loginResponseDto);
        return ResponseEntity.ok(tokenToSend);
    }


}
