package com.example.projectengagement.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProjectStatsDto {
    private String fullName;
    private long participationCount;
    private double totalLoadPercentage;
    private long managedProjectCount;
}
