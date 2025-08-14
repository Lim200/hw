package com.example.projectengagement.controller;

import com.example.projectengagement.dto.UserProjectStatsDto;
import com.example.projectengagement.service.UserStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
@RequestMapping("/api/user-statistics")
@RequiredArgsConstructor
@Tag(name = "User Statistics", description = "API for retrieving and managing statistics about a specific user")
public class UserStatisticsController {

    private final UserStatisticsService userStatisticsService;

    // ✅ READ: получить статистику по пользователю и дате
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

    // ✅ CREATE: сохранить статистику
    @PostMapping
    @Operation(summary = "Create user statistics record")
    public ResponseEntity<UserProjectStatsDto> createUserStats(
            @Valid @RequestBody UserProjectStatsDto dto,
            UriComponentsBuilder uriBuilder
    ) {
        UserProjectStatsDto saved = userStatisticsService.create(dto);
        URI uri = uriBuilder.path("/api/user-statistics/{fullName}").buildAndExpand(saved.getFullName()).toUri();
        return ResponseEntity.created(uri).body(saved);
    }

    // ✅ READ: получить все сохранённые записи
    @GetMapping("/all")
    @Operation(summary = "Get all saved user statistics records")
    public List<UserProjectStatsDto> getAllUserStats() {
        return userStatisticsService.findAll();
    }

    // ✅ UPDATE: обновить статистику по имени
    @PutMapping("/{fullName}")
    @Operation(summary = "Update user statistics by full name")
    public ResponseEntity<UserProjectStatsDto> updateUserStats(
            @PathVariable String fullName,
            @Valid @RequestBody UserProjectStatsDto dto
    ) {
        UserProjectStatsDto updated = userStatisticsService.update(fullName, dto);
        return ResponseEntity.ok(updated);
    }

    // ✅ DELETE: удалить статистику по имени
    @DeleteMapping("/{fullName}")
    @Operation(summary = "Delete user statistics by full name")
    public ResponseEntity<Void> deleteUserStats(@PathVariable String fullName) {
        userStatisticsService.delete(fullName);
        return ResponseEntity.noContent().build();
    }
}
