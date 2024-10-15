package com.prudhviraj.security.security.utils;

import com.prudhviraj.security.security.entities.enums.Permissions;
import com.prudhviraj.security.security.entities.enums.Role;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.prudhviraj.security.security.entities.enums.Permissions.*;
import static com.prudhviraj.security.security.entities.enums.Role.*;

/**
 * A class that maps user roles to their corresponding permissions
 * and provides a method to retrieve authorities for a given role.
 */
public class PermissionMapping {

    // A map that associates each role with its set of permissions
    private static final Map<Role, Set<Permissions>> rolePermissionsMap = Map.of(
            USER, Set.of( // Permissions for USER role
                    POST_VIEW,
                    USER_VIEW
            ),
            CREATOR, Set.of( // Permissions for CREATOR role
                    POST_VIEW,
                    USER_VIEW,
                    POST_CREATE,
                    USER_CREATE
            ),
            GUEST_USER, Set.of( // Permissions for GUEST_USER role
                    POST_VIEW
            ),
            ADMIN, Set.of( // Permissions for ADMIN role
                    POST_VIEW,
                    POST_CREATE,
                    POST_UPDATE,
                    POST_DELETE,
                    USER_VIEW,
                    USER_CREATE,
                    USER_UPDATE,
                    USER_DELETE
            )
    );

    /**
     * Retrieves a set of granted authorities for a specified role.
     *
     * @param role The role for which to retrieve authorities
     * @return A set of SimpleGrantedAuthority corresponding to the permissions of the specified role
     */
    public static Set<SimpleGrantedAuthority> getAuthoritiesForRole(Role role) {
        // Stream through the permissions associated with the role
        return rolePermissionsMap.get(role).stream()
                // Map each permission to a SimpleGrantedAuthority
                .map(permissions -> new SimpleGrantedAuthority(permissions.name()))
                // Collect the authorities into a set and return
                .collect(Collectors.toSet());
    }
}

