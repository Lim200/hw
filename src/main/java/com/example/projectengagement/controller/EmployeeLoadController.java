package com.example.projectengagement.controller;

import com.example.projectengagement.dto.EmployeeLoadDto;
import com.example.projectengagement.service.EmployeeLoadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/employee-load")
@RequiredArgsConstructor
@Tag(name = "Employee Load", description = "API for retrieving employee workload statistics")
public class EmployeeLoadController {

    private final EmployeeLoadService employeeLoadService;

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
        return employeeLoadService.getEmployeeLoad(date);
    }
}
