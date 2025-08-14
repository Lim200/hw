package com.example.projectengagement.service;

import com.example.projectengagement.dto.EmployeeLoadDto;

import java.time.LocalDate;
import java.util.List;

public interface EmployeeLoadService {
    List<EmployeeLoadDto> getEmployeeLoad(LocalDate date);
}
