package com.example.projectengagement.controller;

import com.example.projectengagement.dto.UserProjectStatsDto;
import com.example.projectengagement.entity.Participation;
import com.example.projectengagement.entity.Project;
import com.example.projectengagement.entity.User;
import com.example.projectengagement.repository.ParticipationRepository;
import com.example.projectengagement.repository.ProjectRepository;
import com.example.projectengagement.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user-statistics")
@RequiredArgsConstructor
@Tag(name = "User Statistics", description = "API for retrieving statistics about a specific user")
public class UserStatisticsController {

    private final UserRepository userRepository;
    private final ParticipationRepository participationRepository;
    private final ProjectRepository projectRepository;

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
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length < 2) return Collections.emptyList();

        String lastName = parts[0];
        String firstName = parts[1];
        String middleName = parts.length > 2 ? parts[2] : "";

        List<User> matchedUsers = userRepository.findAll().stream()
                .filter(u -> u.getLastName().equalsIgnoreCase(lastName))
                .filter(u -> u.getFirstName().equalsIgnoreCase(firstName))
                .filter(u -> middleName.isEmpty() || u.getMiddleName().equalsIgnoreCase(middleName))
                .toList();

        return matchedUsers.stream().map(user -> {
            List<Participation> participations = participationRepository.findAll().stream()
                    .filter(p -> p.getUser().equals(user))
                    .filter(p -> isActiveOnDate(p.getStartDate(), p.getEndDate(), date))
                    .toList();

            long participationCount = participations.stream()
                    .map(Participation::getProject)
                    .distinct()
                    .count();

            double totalLoad = participations.stream()
                    .map(p -> Optional.ofNullable(p.getParticipationPercentage()).orElse(0.0))
                    .mapToDouble(Double::doubleValue)
                    .sum();

            long managedCount = projectRepository.findAll().stream()
                    .filter(p -> p.getManager() != null && p.getManager().equals(user))
                    .filter(p -> isActiveOnDate(p.getStartDate(), p.getEndDate(), date))
                    .count();

            return new UserProjectStatsDto(
                    user.getLastName() + " " + user.getFirstName() + " " + Optional.ofNullable(user.getMiddleName()).orElse(""),
                    participationCount,
                    totalLoad,
                    managedCount
            );
        }).toList();
    }

    private boolean isActiveOnDate(LocalDate start, LocalDate end, LocalDate target) {
        return (start == null || !start.isAfter(target)) &&
                (end == null || !end.isBefore(target));
    }
}
