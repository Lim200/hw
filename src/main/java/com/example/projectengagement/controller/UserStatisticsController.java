package com.example.projectengagement.controller;

import com.example.projectengagement.dto.UserProjectStatsDto;
import com.example.projectengagement.entity.Participation;
import com.example.projectengagement.entity.Project;
import com.example.projectengagement.entity.User;
import com.example.projectengagement.repository.ParticipationRepository;
import com.example.projectengagement.repository.ProjectRepository;
import com.example.projectengagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user-statistics")
@RequiredArgsConstructor
public class UserStatisticsController {

    private final UserRepository userRepository;
    private final ParticipationRepository participationRepository;
    private final ProjectRepository projectRepository;

    @GetMapping("/test")
    public String test() {
        return "User statistics controller is working!";
    }

    @GetMapping
    public List<UserProjectStatsDto> getUserStats(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("fullName") String fullName
    ) {
        // Разбиваем ФИО
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length < 2) return Collections.emptyList();

        String lastName = parts[0];
        String firstName = parts[1];
        String middleName = parts.length > 2 ? parts[2] : "";

        // Ищем пользователей по ФИО
        List<User> matchedUsers = userRepository.findAll().stream()
                .filter(u -> u.getLastName().equalsIgnoreCase(lastName))
                .filter(u -> u.getFirstName().equalsIgnoreCase(firstName))
                .filter(u -> middleName.isEmpty() || u.getMiddleName().equalsIgnoreCase(middleName))
                .toList();

        return matchedUsers.stream().map(user -> {
            // Участие в проектах
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

            // Руководство проектами
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

