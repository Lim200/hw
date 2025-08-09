package com.example.projectengagement.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectStatsDto {

    @NotBlank(message = "Project name must not be blank")
    @Size(max = 100, message = "Project name must be at most 100 characters")
    private String projectName;

    @Min(value = 0, message = "Participant count must be zero or positive")
    private long participantCount;
}
