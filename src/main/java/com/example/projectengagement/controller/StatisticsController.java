package com.example.projectengagement.controller;

import com.example.projectengagement.dto.StatisticsDto;
import com.example.projectengagement.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Tag(name = "Statistics", description = "API for retrieving and managing statistics about projects and participants")
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

    @PostMapping
    @Operation(summary = "Create a new statistics record")
    public ResponseEntity<StatisticsDto> createStatistics(
            @Valid @RequestBody StatisticsDto dto,
            UriComponentsBuilder uriBuilder
    ) {
        StatisticsDto saved = statisticsService.create(dto);
        URI uri = uriBuilder.path("/api/statistics/{date}").buildAndExpand(saved.getDate()).toUri();
        return ResponseEntity.created(uri).body(saved);
    }

    @GetMapping("/all")
    @Operation(summary = "Get all saved statistics records")
    public List<StatisticsDto> getAllStatistics() {
        return statisticsService.findAll();
    }

    @PutMapping("/{date}")
    @Operation(summary = "Update statistics for a specific date")
    public ResponseEntity<StatisticsDto> updateStatistics(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Valid @RequestBody StatisticsDto dto
    ) {
        StatisticsDto updated = statisticsService.update(date, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{date}")
    @Operation(summary = "Delete statistics for a specific date")
    public ResponseEntity<Void> deleteStatistics(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        statisticsService.delete(date);
        return ResponseEntity.noContent().build();
    }
}
