package com.example.projectengagement.controller;

import com.example.projectengagement.dto.EmployeeLoadDto;
import com.example.projectengagement.entity.Participation;
import com.example.projectengagement.entity.User;
import com.example.projectengagement.repository.ParticipationRepository;
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
@RequestMapping("/api/employee-load")
@RequiredArgsConstructor
@Tag(name = "Employee Load", description = "API for retrieving employee workload statistics")
public class EmployeeLoadController {

    private final ParticipationRepository participationRepository;

    @GetMapping
    @Operation(
            summary = "Get employee workload",
            description = "Returns a list of employees with their total workload percentage on a given date"
    )
    public List<EmployeeLoadDto> getEmployeeLoad(
            @RequestParam("date")
            @NotNull(message = "Parameter 'date' is required")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "Target date for workload calculation", example = "2025-08-09")
            LocalDate date
    ) {
        List<Participation> activeParticipations = participationRepository.findAll().stream()
                .filter(p -> isActiveOnDate(p.getStartDate(), p.getEndDate(), date))
                .toList();

        Map<User, List<Participation>> grouped = activeParticipations.stream()
                .collect(Collectors.groupingBy(Participation::getUser));

        return grouped.entrySet().stream()
                .map(entry -> {
                    User user = entry.getKey();
                    List<Participation> participations = entry.getValue();

                    double totalLoad = participations.stream()
                            .map(p -> Optional.ofNullable(p.getParticipationPercentage()).orElse(0.0))
                            .mapToDouble(Double::doubleValue)
                            .sum();

                    String fullName = user.getLastName() + " " + user.getFirstName() +
                            (user.getMiddleName() != null ? " " + user.getMiddleName() : "");

                    String departmentName = user.getDepartment() != null ? user.getDepartment().getName() : "—";

                    return new EmployeeLoadDto(fullName, departmentName, totalLoad);
                })
                .toList();
    }

    private boolean isActiveOnDate(LocalDate start, LocalDate end, LocalDate target) {
        return (start == null || !start.isAfter(target)) &&
                (end == null || !end.isBefore(target));
    }
}
