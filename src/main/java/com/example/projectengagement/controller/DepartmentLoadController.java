package com.example.projectengagement.controller;

import com.example.projectengagement.dto.EmployeeProjectLoadDto;
import com.example.projectengagement.repository.ParticipationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/department-load")
@RequiredArgsConstructor
@Validated
@Tag(name = "Department Load", description = "API for retrieving department load statistics")
public class DepartmentLoadController {

    private final ParticipationRepository participationRepository;

    @GetMapping
    @Operation(
            summary = "Get department load",
            description = "Returns a list of employees with their project load for a given department and date"
    )
    public List<EmployeeProjectLoadDto> getDepartmentLoad(
            @RequestParam("date")
            @NotNull(message = "Date must not be null")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "Target date for filtering active participations", required = true)
            LocalDate date,

            @RequestParam("department")
            @NotBlank(message = "Department name must not be blank")
            @Parameter(description = "Name of the department", required = true)
            String departmentName
    ) {
        return participationRepository.findAll().stream()
                .filter(p -> {
                    var user = p.getUser();
                    var dept = user.getDepartment();
                    return dept != null &&
                            dept.getName().equalsIgnoreCase(departmentName) &&
                            isActiveOnDate(p.getStartDate(), p.getEndDate(), date);
                })
                .map(p -> {
                    var user = p.getUser();
                    String fullName = user.getLastName() + " " + user.getFirstName() +
                            (user.getMiddleName() != null ? " " + user.getMiddleName() : "");

                    String projectName = p.getProject() != null ? p.getProject().getName() : "—";
                    double load = Optional.ofNullable(p.getParticipationPercentage()).orElse(0.0);

                    return new EmployeeProjectLoadDto(departmentName, fullName, projectName, load);
                })
                .toList();
    }

    private boolean isActiveOnDate(LocalDate start, LocalDate end, LocalDate target) {
        return (start == null || !start.isAfter(target)) &&
                (end == null || !end.isBefore(target));
    }
}
