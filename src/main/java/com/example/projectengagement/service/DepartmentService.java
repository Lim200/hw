package com.example.projectengagement.service;

import com.example.projectengagement.dto.CreateDepartmentDto;
import com.example.projectengagement.entity.Department;
import com.example.projectengagement.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    @Transactional
    public Department create(CreateDepartmentDto dto) {
        Department d = Department.builder()
                .name(dto.getName())
                .isContractor(dto.isContractor())
                .build();
        return departmentRepository.save(d);
    }
}

