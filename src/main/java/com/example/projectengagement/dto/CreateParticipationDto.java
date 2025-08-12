package com.example.projectengagement.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateParticipationDto {
    @NotNull
    private Long userId;

    @NotNull
    private Long projectId;

    @Size(max = 100)
    private String role;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    private Double participationPercentage;

    private LocalDate startDate;
    private LocalDate endDate;
}

