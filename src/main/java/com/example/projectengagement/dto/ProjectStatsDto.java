package com.example.projectengagement.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectStatsDto {
    private String projectName;
    private long participantCount;
}
