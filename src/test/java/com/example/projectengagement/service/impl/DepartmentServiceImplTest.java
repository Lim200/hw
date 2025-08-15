package com.example.projectengagement.service.impl;

import com.example.projectengagement.dto.CreateDepartmentDto;
import com.example.projectengagement.entity.Department;
import com.example.projectengagement.repository.DepartmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceImplTest {

    @Mock
    DepartmentRepository departmentRepository;

    @InjectMocks
    DepartmentServiceImpl departmentService;

    @Test
    void createFromDto_shouldSaveAndReturnDepartment() {
        CreateDepartmentDto dto = new CreateDepartmentDto("Finance", true);
        Department expected = Department.builder().id(1L).name("Finance").isContractor(true).build();

        when(departmentRepository.save(any(Department.class))).thenReturn(expected);

        Department result = departmentService.create(dto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Finance");
        assertThat(result.isContractor()).isTrue();
    }

    @Test
    void createRaw_shouldSaveAndReturnDepartment() {
        Department department = Department.builder().name("Legal").isContractor(false).build();
        Department saved = Department.builder().id(2L).name("Legal").isContractor(false).build();

        when(departmentRepository.save(department)).thenReturn(saved);

        Department result = departmentService.create(department);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("Legal");
    }

    @Test
    void findAll_shouldReturnListOfDepartments() {
        List<Department> departments = List.of(
                Department.builder().id(1L).name("IT").isContractor(false).build(),
                Department.builder().id(2L).name("HR").isContractor(true).build()
        );

        when(departmentRepository.findAll()).thenReturn(departments);

        List<Department> result = departmentService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("IT");
    }

    @Test
    void findById_shouldReturnDepartmentIfExists() {
        Department department = Department.builder().id(3L).name("Marketing").isContractor(false).build();

        when(departmentRepository.findById(3L)).thenReturn(Optional.of(department));

        Optional<Department> result = departmentService.findById(3L);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Marketing");
    }

    @Test
    void update_shouldModifyAndReturnDepartment() {
        Department existing = Department.builder().id(4L).name("OldName").isContractor(false).build();
        Department updateData = Department.builder().name("NewName").isContractor(true).build();

        when(departmentRepository.findById(4L)).thenReturn(Optional.of(existing));
        when(departmentRepository.save(any(Department.class))).thenAnswer(inv -> inv.getArgument(0));

        Department result = departmentService.update(4L, updateData);

        assertThat(result.getName()).isEqualTo("NewName");
        assertThat(result.isContractor()).isTrue();
    }

    @Test
    void delete_shouldCallRepositoryDeleteById() {
        departmentService.delete(5L);
        verify(departmentRepository, times(1)).deleteById(5L);
    }
}

