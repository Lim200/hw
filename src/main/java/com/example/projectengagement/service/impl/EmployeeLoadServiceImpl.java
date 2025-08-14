package com.example.projectengagement.service.impl;

import com.example.projectengagement.dto.EmployeeLoadDto;
import com.example.projectengagement.entity.Participation;
import com.example.projectengagement.entity.User;
import com.example.projectengagement.repository.ParticipationRepository;
import com.example.projectengagement.service.EmployeeLoadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeLoadServiceImpl implements EmployeeLoadService {

    private final ParticipationRepository participationRepository;

    @Override
    public List<EmployeeLoadDto> getEmployeeLoad(LocalDate date) {
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
