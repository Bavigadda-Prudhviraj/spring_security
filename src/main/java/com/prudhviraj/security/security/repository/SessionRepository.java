package com.prudhviraj.security.security.repository;

import com.prudhviraj.security.security.entities.Sessions;
import com.prudhviraj.security.security.entities.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Sessions,Long> {

     List<Sessions> findByUser(User user);
     Optional<Sessions> findByRefreshToken(String refreshToken);
}
