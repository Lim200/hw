package com.example.projectengagement.service;

import com.example.projectengagement.dto.CreateDepartmentDto;
import com.example.projectengagement.entity.Department;

import java.util.List;
import java.util.Optional;

public interface DepartmentService {
    Department create(CreateDepartmentDto dto);
    Department create(Department department);
    List<Department> findAll();
    Optional<Department> findById(Long id);
    Department update(Long id, Department department);
    void delete(Long id);
}

