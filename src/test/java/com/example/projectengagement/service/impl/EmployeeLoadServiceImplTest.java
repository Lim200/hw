package com.example.projectengagement.controller;

import com.example.projectengagement.config.TestSecurityConfig;
import com.example.projectengagement.dto.EmployeeLoadDto;
import com.example.projectengagement.exception.GlobalExceptionHandler;
import com.example.projectengagement.service.EmployeeLoadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeLoadController.class)
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
class EmployeeLoadControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    EmployeeLoadService employeeLoadService;

    @Test
    void getEmployeeLoad_returnsExpectedJson() throws Exception {
        LocalDate date = LocalDate.of(2025, 8, 10);

        EmployeeLoadDto dto = new EmployeeLoadDto("Ivanov Ivan", "Sales", 75.0);
        when(employeeLoadService.getEmployeeLoad(date)).thenReturn(List.of(dto));

        mvc.perform(get("/api/employee-load")
                        .param("date", "2025-08-10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].fullName").value("Ivanov Ivan"))
                .andExpect(jsonPath("$[0].departmentName").value("Sales"))
                .andExpect(jsonPath("$[0].totalLoadPercentage").value(75.0));
    }

    @Test
    void getEmployeeLoad_missingDateParam_returnsBadRequest() throws Exception {
        mvc.perform(get("/api/employee-load"))
                .andExpect(status().isBadRequest());
    }
}
