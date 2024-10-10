package com.prudhviraj.security.security.service.impl;

import com.prudhviraj.security.security.dto.LoginDto;
import com.prudhviraj.security.security.dto.LoginResponseDto;
import com.prudhviraj.security.security.dto.SignUpDto;
import com.prudhviraj.security.security.dto.UserDto;
import com.prudhviraj.security.security.entities.User;
import com.prudhviraj.security.security.exceptions.ResourceNotFoundException;
import com.prudhviraj.security.security.repository.UserRepository;
import com.prudhviraj.security.security.service.AuthService;
import com.prudhviraj.security.security.service.JwtService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private PasswordEncoder passwordEncoder;

    // Constructor injection
    public AuthServiceImpl(UserRepository userRepository, ModelMapper modelMapper,
                           AuthenticationManager authenticationManager, JwtService jwtService,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }
    /**
     * Handles user sign-up by saving user details into the database.
     *
     * @param signup - DTO containing user sign-up information.
     * @return UserDto - The saved user data.
     * @throws BadCredentialsException if the email is already registered.
     */
    @Override
    public UserDto signUp(SignUpDto signup) {
        log.debug("Attempting to sign up user with email: {}", signup.getEmail());

        // Check if a user with the same email already exists
        if (userRepository.findByEmail(signup.getEmail()).isPresent()) {
            log.warn("User with email {} already exists", signup.getEmail());
            throw new BadCredentialsException("User is already exist with given email");
        }

        // Map SignUpDto to User entity
        User user = modelMapper.map(signup, User.class);
        user.setPassword(passwordEncoder.encode(signup.getPassword()));

        // Save the user to the database
        User savedUser = userRepository.save(user);
        log.info("Successfully saved user with ID: {}", savedUser.getId());

        // Return mapped UserDto
        return modelMapper.map(savedUser, UserDto.class);
    }

    /**
     * Authenticates the user and generates a JWT token upon successful login.
     *
     * @param login - DTO containing login credentials.
     * @return String - The generated JWT token.
     */
    @Override
    public String login(LoginDto login) {
        log.debug("Attempting to log in user with email: {}", login.getEmail());

        // Authenticate the user using provided credentials
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword())
        );

        // Retrieve authenticated user details
        User user = (User) authentication.getPrincipal();
        log.info("User {} authenticated successfully", user.getEmail());

        // Generate and return JWT token
        String token = jwtService.generateToken(user);
        log.debug("Generated JWT token for user {}: {}", user.getEmail(), token);

        return token;
    }
    /**
     * Authenticates a user using their email and password, then generates both an access token and a refresh token.
     *
     * @param login the login request containing the user's email and password
     * @return LoginResponseDto containing the authenticated user's ID, access token, and refresh token
     *
     * @throws AuthenticationException if the authentication process fails
     *
     * <p>
     * This method uses the {@link AuthenticationManager} to authenticate the user and then generates
     * an access token and refresh token for future requests. It also logs the success or failure of
     * the authentication process.
     * </p>
     */
    @Override
    public LoginResponseDto loginWithAccessAndRefreshToken(LoginDto login) {
        log.info("Attempting to authenticate user with email: {}", login.getEmail());

        // Authenticate the user using the provided email and password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword())
        );

        // Retrieve the authenticated user from the authentication context
        User user = (User) authentication.getPrincipal();
        log.info("Authentication successful for user with email: {}", user.getEmail());

        // Generate access and refresh tokens for the authenticated user
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        log.debug("Generated access token for user: {}", user.getEmail());
        log.debug("Generated refresh token for user: {}", user.getEmail());

        // Return the LoginResponseDto containing the user ID, access token, and refresh token
        return new LoginResponseDto(user.getId(), accessToken, refreshToken);
    }

    /**
     * Refreshes the user's access token using a provided refresh token. The refresh token is used to
     * retrieve the user's ID and generate a new access token without re-authenticating the user.
     *
     * @param refreshToken the refresh token used to generate a new access token
     * @return LoginResponseDto containing the user's ID, new access token, and the same refresh token
     *
     * @throws JwtException if the refresh token is invalid or expired
     * @throws ResourceNotFoundException if the user associated with the refresh token cannot be found
     *
     * <p>
     * This method extracts the user's ID from the provided refresh token and generates a new access
     * token for the user. It logs key information about the refresh process, including token generation.
     * </p>
     */
    @Override
    public LoginResponseDto refreshToken(String refreshToken) {
        log.info("Refreshing access token using refresh token...");

        // Extract user ID from the provided refresh token
        Long userId = jwtService.getUserIdFromToken(refreshToken);
        log.debug("Extracted user ID from refresh token: {}", userId);

        // Fetch the user from the repository using the extracted user ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);
                    return new ResourceNotFoundException("User with given ID doesn't exist");
                });

        log.info("User found for ID: {}. Generating new access token.", userId);

        // Generate a new access token for the user
        String accessToken = jwtService.generateAccessToken(user);
        log.debug("Generated new access token for user ID: {}", userId);

        // Return the LoginResponseDto containing the user ID, new access token, and the same refresh token
        return new LoginResponseDto(userId, accessToken, refreshToken);
    }

}
