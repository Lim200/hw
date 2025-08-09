package com.example.projectengagement.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProjectStatsDto {

    @NotBlank(message = "Full name must not be blank")
    @Size(max = 100, message = "Full name must be at most 100 characters")
    private String fullName;

    @Min(value = 0, message = "Participation count must be zero or positive")
    private long participationCount;

    @DecimalMin(value = "0.0", inclusive = true, message = "Total load percentage must be at least 0")
    @DecimalMax(value = "100.0", inclusive = true, message = "Total load percentage must be at most 100")
    private double totalLoadPercentage;

    @Min(value = 0, message = "Managed project count must be zero or positive")
    private long managedProjectCount;
}
