package com.example.projectengagement.repository;

import com.example.projectengagement.entity.DepartmentManager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentManagerRepository extends JpaRepository<DepartmentManager, Long> {
    List<DepartmentManager> findByDepartmentId(Long departmentId);
}

