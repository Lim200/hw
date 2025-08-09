package com.example.projectengagement.dto;

import lombok.*;

import jakarta.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeProjectLoadDto {

    @NotBlank(message = "Название подразделения не может быть пустым")
    private String departmentName;

    @NotBlank(message = "ФИО сотрудника не может быть пустым")
    private String employeeFullName;

    @NotBlank(message = "Название проекта не может быть пустым")
    private String projectName;

    @DecimalMin(value = "0.0", inclusive = true, message = "Загрузка не может быть отрицательной")
    @DecimalMax(value = "100.0", inclusive = true, message = "Загрузка не может превышать 100%")
    private double loadPercentage;
}