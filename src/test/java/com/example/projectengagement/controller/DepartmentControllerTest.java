package com.example.projectengagement.controller;

import com.example.projectengagement.config.TestSecurityConfig;
import com.example.projectengagement.dto.CreateDepartmentDto;
import com.example.projectengagement.entity.Department;
import com.example.projectengagement.exception.GlobalExceptionHandler;
//import com.example.projectengagement.security.TestSecurityConfig;
import com.example.projectengagement.service.DepartmentService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DepartmentController.class)
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
class DepartmentControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    DepartmentService departmentService;

    @Test
    void createDepartment() throws Exception {
        CreateDepartmentDto dto = new CreateDepartmentDto("IT", false);
        Department saved = Department.builder().id(10L).name("IT").isContractor(false).build();
        when(departmentService.create(any(CreateDepartmentDto.class))).thenReturn(saved);


        mvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"IT\",\"contractor\":false}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/departments/10")))
                .andExpect(jsonPath("$.id").value(10));
    }
}


