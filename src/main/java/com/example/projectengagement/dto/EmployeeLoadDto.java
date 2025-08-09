package com.example.projectengagement.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeLoadDto {

    @NotBlank(message = "Full name must not be blank")
    @Size(max = 100, message = "Full name must be at most 100 characters")
    private String fullName;

    @NotBlank(message = "Department name must not be blank")
    @Size(max = 100, message = "Department name must be at most 100 characters")
    private String departmentName;

    @DecimalMin(value = "0.0", inclusive = true, message = "Load percentage must be at least 0")
    @DecimalMax(value = "100.0", inclusive = true, message = "Load percentage must be at most 100")
    private double totalLoadPercentage;
}
