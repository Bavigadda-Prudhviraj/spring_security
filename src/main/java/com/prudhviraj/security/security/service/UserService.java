package com.prudhviraj.security.security.service;

import com.prudhviraj.security.security.dto.UserDto;
import com.prudhviraj.security.security.entities.User;

public interface UserService {
    public UserDto getUserByUserId(Long userId);
    public UserDto findByEmail(String userEmail);

    public UserDto save(User newUser);
}
