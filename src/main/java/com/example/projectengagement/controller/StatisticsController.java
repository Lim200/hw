package com.example.projectengagement.controller;

import com.example.projectengagement.dto.StatisticsDto;
import com.example.projectengagement.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Tag(name = "Statistics", description = "API for retrieving statistics about projects and participants")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping
    @Operation(
            summary = "Get statistics for a specific date",
            description = "Returns the number of employees, active projects, departments, and a list of projects with participant counts"
    )
    public StatisticsDto getStatistics(
            @RequestParam("date")
            @NotNull(message = "Parameter 'date' is required")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "Date for which statistics should be retrieved", example = "2025-08-09")
            LocalDate date
    ) {
        return statisticsService.getStatistics(date);
    }
}
