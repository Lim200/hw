package com.example.projectengagement.service;

import com.example.projectengagement.dto.CreateDepartmentDto;
import com.example.projectengagement.entity.Department;
import com.example.projectengagement.repository.DepartmentRepository;
import com.example.projectengagement.service.impl.DepartmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DepartmentServiceTest {

    private DepartmentRepository departmentRepository;
    private DepartmentService departmentService;

    @BeforeEach
    void setUp() {
        departmentRepository = mock(DepartmentRepository.class);
        departmentService = new DepartmentServiceImpl(departmentRepository);
    }

    @Test
    void create_savesDepartmentAndReturnsIt() {
        // given
        CreateDepartmentDto dto = new CreateDepartmentDto("IT", false);
        Department saved = Department.builder()
                .id(1L)
                .name("IT")
                .isContractor(false)
                .build();

        when(departmentRepository.save(any(Department.class))).thenReturn(saved);

        // when
        Department result = departmentService.create(dto);

        // then
        ArgumentCaptor<Department> captor = ArgumentCaptor.forClass(Department.class);
        verify(departmentRepository).save(captor.capture());

        Department captured = captor.getValue();
        assertThat(captured.getName()).isEqualTo("IT");
        assertThat(captured.isContractor()).isFalse();

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("IT");
        assertThat(result.isContractor()).isFalse();
    }
}
