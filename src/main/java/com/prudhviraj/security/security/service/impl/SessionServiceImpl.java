package com.prudhviraj.security.security.service.impl;

import com.prudhviraj.security.security.entities.Sessions;
import com.prudhviraj.security.security.entities.User;
import com.prudhviraj.security.security.repository.SessionRepository;
import com.prudhviraj.security.security.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private static final Logger log = LoggerFactory.getLogger(SessionServiceImpl.class);

    private final SessionRepository sessionRepository; // Repository for session data
    private final int SESSION_LIMIT = 2; // Maximum number of sessions allowed per user

    /**
     * Generates a new session for the specified user.
     * If the user already has the maximum number of sessions,
     * the least recently used session will be deleted.
     *
     * @param user        The user for whom the session is being generated
     * @param refreshToken The refresh token for the new session
     */
    @Override
    public void generateNewSession(User user, String refreshToken) {
        // Retrieve the current sessions for the user
        List<Sessions> userSessions = sessionRepository.findByUser(user);

        // Check if the user has reached the session limit
        if (userSessions.size() == SESSION_LIMIT) {
            // Sort sessions by last used time (ascending)
            userSessions.sort(Comparator.comparing(Sessions::getLastUsedAt));

            // Get the least recently used session
            Sessions leastRecentlyUsedSession = userSessions.get(0);
            log.info("Deleting least recently used session: {}", leastRecentlyUsedSession.getId());
            sessionRepository.delete(leastRecentlyUsedSession); // Delete the oldest session
        }

        // Create a new session with the given user and refresh token
        Sessions newSession = Sessions.builder()
                .user(user)
                .refreshToken(refreshToken)
                .lastUsedAt(LocalDateTime.now()) // Set the current time as last used
                .build();

        log.info("Saving new session for user: {}", user.getId());
        sessionRepository.save(newSession); // Save the new session
    }

    /**
     * Validates the provided refresh token and updates the last used time.
     *
     * @param refreshToken The refresh token to validate
     * @throws SessionAuthenticationException if the session is not found
     */
    @Override
    public void validateRefreshToken(String refreshToken) {
        // Find the session by refresh token
        Sessions session = sessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new SessionAuthenticationException("Session not found for refresh token: " + refreshToken));

        // Update the last used time for the session
        session.setLastUsedAt(LocalDateTime.now());
        log.info("Updating last used time for session: {}", session.getId());
        sessionRepository.save(session); // Save the updated session
    }
}