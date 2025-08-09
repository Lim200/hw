package com.example.projectengagement.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeLoadDto {
    private String fullName;
    private String departmentName;
    private double totalLoadPercentage;
}
