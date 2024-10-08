package com.prudhviraj.security.security.service;

import com.prudhviraj.security.security.dto.UserDto;

public interface UserService {
    public UserDto getUserByUserId(Long userId);
}
