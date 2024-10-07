package com.prudhviraj.security.security.service;

import com.prudhviraj.security.security.entities.User;


public interface JwtService {
    public String generateToken(User user);
    public Long getUserIdFromToken(String token);
}
