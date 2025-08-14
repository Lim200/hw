package com.example.projectengagement.service.impl;

import com.example.projectengagement.dto.ProjectStatsDto;
import com.example.projectengagement.dto.StatisticsDto;
import com.example.projectengagement.entity.Project;
import com.example.projectengagement.repository.ParticipationRepository;
import com.example.projectengagement.repository.ProjectContractorRepository;
import com.example.projectengagement.repository.ProjectRepository;
import com.example.projectengagement.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final ParticipationRepository participationRepository;
    private final ProjectRepository projectRepository;
    private final ProjectContractorRepository projectContractorRepository;

    @Override
    public StatisticsDto getStatistics(LocalDate date) {
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
