package com.example.projectengagement.controller;

import com.example.projectengagement.dto.UserProjectStatsDto;
import com.example.projectengagement.service.UserStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/user-statistics")
@RequiredArgsConstructor
@Tag(name = "User Statistics", description = "API for retrieving statistics about a specific user")
public class UserStatisticsController {

    private final UserStatisticsService userStatisticsService;

    @GetMapping
    @Operation(
            summary = "Get statistics for a user",
            description = "Returns the number of projects, workload percentage, and number of managed projects for the specified user"
    )
    public List<UserProjectStatsDto> getUserStats(
            @RequestParam("date")
            @NotNull(message = "Parameter 'date' is required")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "Date for which statistics should be retrieved", example = "2025-08-09")
            LocalDate date,

            @RequestParam("fullName")
            @NotBlank(message = "Parameter 'fullName' is required")
            @Parameter(description = "Full name of the user (e.g., Ivanov Ivan Ivanovich)", example = "Ivanov Ivan Ivanovich")
            String fullName
    ) {
        return userStatisticsService.getUserStats(date, fullName);
    }
}
