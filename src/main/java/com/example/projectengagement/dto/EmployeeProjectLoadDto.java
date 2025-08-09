package com.example.projectengagement.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeProjectLoadDto {
    private String departmentName;
    private String employeeFullName;
    private String projectName;
    private double loadPercentage;
}
