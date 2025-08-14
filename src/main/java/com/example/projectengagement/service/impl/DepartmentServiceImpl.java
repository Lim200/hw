package com.example.projectengagement.service.impl;

import com.example.projectengagement.dto.CreateDepartmentDto;
import com.example.projectengagement.entity.Department;
import com.example.projectengagement.repository.DepartmentRepository;
import com.example.projectengagement.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Transactional
    @Override
    public Department create(CreateDepartmentDto dto) {
        Department d = Department.builder()
                .name(dto.getName())
                .isContractor(dto.isContractor())
                .build();
        return departmentRepository.save(d);
    }

    @Transactional
    @Override
    public Department create(Department department) {
        return departmentRepository.save(department);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Department> findById(Long id) {
        return departmentRepository.findById(id);
    }

    @Transactional
    @Override
    public Department update(Long id, Department department) {
        Department existing = departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Department not found"));
        existing.setName(department.getName());
        existing.setContractor(department.isContractor()); // ✅ правильный сеттер
        return departmentRepository.save(existing);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        departmentRepository.deleteById(id);
    }
}
