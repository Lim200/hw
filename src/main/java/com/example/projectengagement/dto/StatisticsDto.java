package com.example.projectengagement.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsDto {
    private long employeeCount;
    private long activeProjectCount;
    private long departmentCount;
    private List<ProjectStatsDto> activeProjects;
}
