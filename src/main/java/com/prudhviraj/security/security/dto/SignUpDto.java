package com.prudhviraj.security.security.dto;

import com.prudhviraj.security.security.entities.enums.Permissions;
import com.prudhviraj.security.security.entities.enums.Role;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.util.Set;

@Data
public class SignUpDto {

    private String name;
    @Email
    @Column(unique = true)
    private String email;
    private String password;
    private Set<Role> roles;
    private Set<Permissions> permission;
}
