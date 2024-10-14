package com.prudhviraj.security.security.service;

import com.prudhviraj.security.security.entities.User;

public interface SessionService {
    void generateNewSession(User user, String refreshToken);
    void validateRefreshToken(String refreshToken);
}
