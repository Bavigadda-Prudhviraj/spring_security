package com.prudhviraj.security.security.dto;



import com.fasterxml.jackson.annotation.JsonProperty;
import com.prudhviraj.security.security.annotations.EmployeeRoleValidation;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmployeeDto {
    private Long id;
    @NotNull(message = "Required Field in Employee : name ")
    @Size(min = 3,max = 30, message = "No of characters in the name should be <=30 && >=3")
    private String name;

    @Email(message = "Invalid Email")
    private String email;

    @Max(value = 80, message = "Age of employee must be less than 80")
    @Min(value = 18, message = "Age of employee must be greater than 18")
    private Integer age;

    @PastOrPresent(message = "date of joining should now be in future")
    private LocalDate DOJ;

    //@Pattern( regexp = "^(ADMIN|USER)$")
    @EmployeeRoleValidation
    private String role;

    @NotNull(message = "salary cannot be null")
    @Positive(message = "salary should be positive value")
    @Digits(integer = 6,fraction = 2, message = "Issue with digts")
    @DecimalMin(value = "100.50", message = "issue with DemicalMIN")
    @DecimalMax(value = "100000.99", message = "issue with DemicalMAX")
    private Double salary;

    @JsonProperty("isActive")
    private boolean isActive;

}
