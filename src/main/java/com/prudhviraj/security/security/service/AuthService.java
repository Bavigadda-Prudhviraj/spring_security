package com.prudhviraj.security.security.service;

import com.prudhviraj.security.security.dto.LoginDto;
import com.prudhviraj.security.security.dto.LoginResponseDto;
import com.prudhviraj.security.security.dto.SignUpDto;
import com.prudhviraj.security.security.dto.UserDto;

public interface AuthService {

    UserDto signUp(SignUpDto signup);
    String login(LoginDto login);

    LoginResponseDto loginWithAccessAndRefreshToken(LoginDto login);
    LoginResponseDto refreshToken(String refreshToken);
}
