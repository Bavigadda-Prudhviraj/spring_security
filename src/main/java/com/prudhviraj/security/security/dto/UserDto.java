package com.prudhviraj.security.security.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    @Email
    private String email;
    private String name;
}
