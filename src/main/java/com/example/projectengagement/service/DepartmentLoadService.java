package com.example.projectengagement.service;

import com.example.projectengagement.dto.EmployeeProjectLoadDto;
import com.example.projectengagement.entity.Department;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DepartmentLoadService {

    List<EmployeeProjectLoadDto> getDepartmentLoad(LocalDate date, String departmentName);

    Department create(Department departmentLoad);

    Optional<Department> findById(Long id);

    Department update(Long id, Department departmentLoad);

    void delete(Long id);
}

