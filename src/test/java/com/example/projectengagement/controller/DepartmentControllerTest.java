package com.example.projectengagement.controller;

import com.example.projectengagement.config.TestSecurityConfig;
import com.example.projectengagement.dto.CreateDepartmentDto;
import com.example.projectengagement.entity.Department;
import com.example.projectengagement.exception.GlobalExceptionHandler;
import com.example.projectengagement.service.DepartmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DepartmentController.class)
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
class DepartmentControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    DepartmentService departmentService;

    Department sampleDepartment() {
        return Department.builder().id(10L).name("IT").isContractor(false).build();
    }

    @Test
    void createDepartmentFromDto() throws Exception {
        CreateDepartmentDto dto = new CreateDepartmentDto("IT", false);
        Department saved = sampleDepartment();

        when(departmentService.create(any(CreateDepartmentDto.class))).thenReturn(saved);

        mvc.perform(post("/api/departments/dto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"IT\",\"contractor\":false}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/departments/10")))
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void createDepartmentRawEntity() throws Exception {
        Department department = sampleDepartment();

        when(departmentService.create(any(Department.class))).thenReturn(department);

        mvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":10,\"name\":\"IT\",\"contractor\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void getAllDepartments() throws Exception {
        when(departmentService.findAll()).thenReturn(List.of(sampleDepartment()));

        mvc.perform(get("/api/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10));
    }

    @Test
    void getDepartmentById_found() throws Exception {
        when(departmentService.findById(10L)).thenReturn(Optional.of(sampleDepartment()));

        mvc.perform(get("/api/departments/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void getDepartmentById_notFound() throws Exception {
        when(departmentService.findById(99L)).thenReturn(Optional.empty());

        mvc.perform(get("/api/departments/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateDepartment() throws Exception {
        Department updated = Department.builder().id(10L).name("Updated").isContractor(true).build();

        when(departmentService.update(eq(10L), any(Department.class))).thenReturn(updated);

        mvc.perform(put("/api/departments/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":10,\"name\":\"Updated\",\"contractor\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"))
                .andExpect(jsonPath("$.contractor").value(true));
    }

    @Test
    void deleteDepartment() throws Exception {
        doNothing().when(departmentService).delete(10L);

        mvc.perform(delete("/api/departments/10"))
                .andExpect(status().isNoContent());
    }
}
