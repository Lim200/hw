package com.example.projectengagement.controller;


import com.example.projectengagement.dto.EmployeeLoadDto;
import com.example.projectengagement.entity.Participation;
import com.example.projectengagement.entity.User;
import com.example.projectengagement.repository.ParticipationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employee-load")
@RequiredArgsConstructor
public class EmployeeLoadController {

    private final ParticipationRepository participationRepository;

    @GetMapping
    public List<EmployeeLoadDto> getEmployeeLoad(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        // Получаем все участия, активные на указанную дату
        List<Participation> activeParticipations = participationRepository.findAll().stream()
                .filter(p -> isActiveOnDate(p.getStartDate(), p.getEndDate(), date))
                .toList();

        // Группируем по пользователям
        Map<User, List<Participation>> grouped = activeParticipations.stream()
                .collect(Collectors.groupingBy(Participation::getUser));

        // Формируем DTO
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

