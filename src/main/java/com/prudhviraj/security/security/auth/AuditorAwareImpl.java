package com.prudhviraj.security.security.auth;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {
    /**
     * Returns the current auditor of the application.
     *
     * @return the current auditor.
     */
    @Override
    public Optional getCurrentAuditor() {
        // get the security context
        // get authentication
        // get the principle
        //get the username
        return Optional.of("Bavigadda Prudhviraj");
    }
}
