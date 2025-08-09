package com.example.projectengagement.controller;

import com.example.projectengagement.dto.ProjectStatsDto;
import com.example.projectengagement.dto.StatisticsDto;
import com.example.projectengagement.entity.Project;
import com.example.projectengagement.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Tag(name = "Statistics", description = "API for retrieving statistics about projects and participants")
public class StatisticsController {

    private final ParticipationRepository participationRepository;
    private final ProjectRepository projectRepository;
    private final ProjectContractorRepository projectContractorRepository;

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
        long employeeCount = participationRepository.findAll().stream()
                .filter(p -> isActiveOnDate(p.getStartDate(), p.getEndDate(), date))
                .map(p -> p.getUser().getId())
                .distinct()
                .count();

        List<Project> activeProjects = projectRepository.findAll().stream()
                .filter(p -> p.isActive() && isActiveOnDate(p.getStartDate(), p.getEndDate(), date))
                .toList();

        long activeProjectCount = activeProjects.size();

        long departmentCount = projectContractorRepository.findAll().stream()
                .filter(pc -> activeProjects.contains(pc.getProject()))
                .map(pc -> pc.getDepartment().getId())
                .distinct()
                .count();

        Map<String, Long> projectParticipationMap = activeProjects.stream()
                .collect(Collectors.toMap(
                        Project::getName,
                        project -> participationRepository.findAll().stream()
                                .filter(p -> p.getProject().equals(project))
                                .filter(p -> isActiveOnDate(p.getStartDate(), p.getEndDate(), date))
                                .map(p -> p.getUser().getId())
                                .distinct()
                                .count()
                ));

        List<ProjectStatsDto> projectStats = projectParticipationMap.entrySet().stream()
                .map(entry -> new ProjectStatsDto(entry.getKey(), entry.getValue()))
                .toList();

        return new StatisticsDto(employeeCount, activeProjectCount, departmentCount, projectStats);
    }

    private boolean isActiveOnDate(LocalDate start, LocalDate end, LocalDate target) {
        return (start == null || !start.isAfter(target)) &&
                (end == null || !end.isBefore(target));
    }
}
