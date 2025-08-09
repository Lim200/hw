package com.example.projectengagement.controller;

import com.example.projectengagement.dto.ProjectStatsDto;
import com.example.projectengagement.dto.StatisticsDto;
import com.example.projectengagement.entity.Project;
import com.example.projectengagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final ParticipationRepository participationRepository;
    private final ProjectRepository projectRepository;
    private final ProjectContractorRepository projectContractorRepository;

    @GetMapping
    public StatisticsDto getStatistics(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        // 1. Количество работников, участвующих в проектах на дату
        long employeeCount = participationRepository.findAll().stream()
                .filter(p -> isActiveOnDate(p.getStartDate(), p.getEndDate(), date))
                .map(p -> p.getUser().getId())
                .distinct()
                .count();

        // 2. Количество активных проектов на дату
        List<Project> activeProjects = projectRepository.findAll().stream()
                .filter(p -> p.isActive() && isActiveOnDate(p.getStartDate(), p.getEndDate(), date))
                .toList();

        long activeProjectCount = activeProjects.size();

        // 3. Количество уникальных подразделений, задействованных в проектах
        long departmentCount = projectContractorRepository.findAll().stream()
                .filter(pc -> activeProjects.contains(pc.getProject()))
                .map(pc -> pc.getDepartment().getId())
                .distinct()
                .count();

        // 4. Список проектов с количеством участников
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

