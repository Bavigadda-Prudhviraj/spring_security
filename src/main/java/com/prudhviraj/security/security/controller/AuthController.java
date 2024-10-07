package com.prudhviraj.security.security.controller;

import com.prudhviraj.security.security.advices.ApiResponse;
import com.prudhviraj.security.security.dto.LoginDto;
import com.prudhviraj.security.security.dto.SignUpDto;
import com.prudhviraj.security.security.dto.UserDto;
import com.prudhviraj.security.security.service.impl.AuthServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        cookie.setPath("/"); // Set the path for the cookie
        cookie.setMaxAge(60 * 60); // Set cookie expiration to 1 hour
        response.addCookie(cookie);

        log.info("Login successful, token generated for email: {}", login.getEmail());

        ApiResponse<String> tokenToSend = new ApiResponse<>(token);
        return ResponseEntity.ok(tokenToSend);
    }
}
