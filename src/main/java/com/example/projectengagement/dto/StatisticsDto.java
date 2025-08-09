package com.example.projectengagement.dto;

import jakarta.validation.Valid;
import lombok.*;
import jakarta.validation.constraints.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsDto {

    @Min(value = 0, message = "Employee count must be zero or positive")
    private long employeeCount;

    @Min(value = 0, message = "Active project count must be zero or positive")
    private long activeProjectCount;

    @Min(value = 0, message = "Department count must be zero or positive")
    private long departmentCount;

    @NotNull(message = "Active projects list must not be null")
    @Size(min = 1, message = "Active projects list must contain at least one project")
    private List<@Valid ProjectStatsDto> activeProjects;
}

