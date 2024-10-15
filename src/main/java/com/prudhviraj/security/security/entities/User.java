package com.prudhviraj.security.security.entities;

import com.prudhviraj.security.security.entities.enums.Permissions;
import com.prudhviraj.security.security.entities.enums.Role;
import com.prudhviraj.security.security.utils.PermissionMapping;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Email
    @Column(unique = true)
    private String email;
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    /**
     * Retrieves a collection of granted authorities for the user's roles,
     * including both role-based and permission-based authorities.
     *
     * @return A collection of SimpleGrantedAuthority objects representing
     *         the roles and permissions assigned to the user.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Create a set to hold all granted authorities
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        // Iterate through each role assigned to the user
        roles.forEach(role -> {
            // Retrieve the permissions associated with the current role
            Set<SimpleGrantedAuthority> permissions = PermissionMapping.getAuthoritiesForRole(role);

            // Add all permissions to the authorities set
            authorities.addAll(permissions);

            // Add the role as a granted authority
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
        });

        // Return the complete set of authorities
        return authorities;
    }


    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}
