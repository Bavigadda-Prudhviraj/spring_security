package com.prudhviraj.security.security.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class SignUpDto {

    private String name;
    @Email
    @Column(unique = true)
    private String email;
    private String password;
}
