package com.prudhviraj.security.security.service.impl;

import com.prudhviraj.security.security.dto.LoginDto;
import com.prudhviraj.security.security.dto.SignUpDto;
import com.prudhviraj.security.security.dto.UserDto;
import com.prudhviraj.security.security.entities.User;
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
}
