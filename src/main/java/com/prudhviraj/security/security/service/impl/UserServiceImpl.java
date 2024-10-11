package com.prudhviraj.security.security.service.impl;

import com.prudhviraj.security.security.dto.UserDto;
import com.prudhviraj.security.security.entities.User;
import com.prudhviraj.security.security.exceptions.ResourceNotFoundException;
import com.prudhviraj.security.security.repository.UserRepository;
import com.prudhviraj.security.security.service.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService , UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(()->new BadCredentialsException("No user account associated with the provided email address: "+username+". Please check your email and try again."));
    }
    @Override
    public UserDto getUserByUserId(Long userId) {
        log.info("Fetching user with ID: {}", userId);

        // Fetch the user from the repository or throw an exception if not found
        User fetchedUser = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);
                    return new ResourceNotFoundException("No user account associated with the provided User Id: "
                            + userId + ". Please check your UserId and try again.");
                });

        log.info("User with ID: {} successfully fetched", userId);

        // Map the fetched User entity to the UserDto and return
        UserDto userDto = modelMapper.map(fetchedUser, UserDto.class);
        log.debug("Mapped user entity to DTO: {}", userDto);

        return userDto;
    }

    @Override
    public UserDto findByEmail(String userEmail) {
        User fetchedUser = userRepository.findByEmail(userEmail)
                .orElse(null);

        UserDto userDto = modelMapper.map(fetchedUser, UserDto.class);
        log.debug("Mapped user entity to DTO: {}", userDto);

        return userDto;
    }

    @Override
    public UserDto save(User newUser) {
        return modelMapper.map(newUser, UserDto.class);
    }


}
