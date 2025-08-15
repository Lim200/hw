package com.example.projectengagement.controller;

import com.example.projectengagement.config.TestSecurityConfig;
import com.example.projectengagement.dto.ProjectStatsDto;
import com.example.projectengagement.dto.StatisticsDto;
import com.example.projectengagement.exception.GlobalExceptionHandler;
import com.example.projectengagement.service.StatisticsService;
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

@WebMvcTest(StatisticsController.class)
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
class StatisticsControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    StatisticsService statisticsService;

    @Test
    void getStatistics_returnsExpectedJson() throws Exception {
        LocalDate date = LocalDate.of(2025, 8, 10);

        StatisticsDto dto = StatisticsDto.builder()
                .date(date)
                .employeeCount(1)
                .activeProjectCount(1)
                .departmentCount(1)
                .activeProjects(List.of(new ProjectStatsDto("Project Alpha", 1L)))
                .build();

        when(statisticsService.getStatistics(date)).thenReturn(dto);

        mvc.perform(get("/api/statistics")
                        .param("date", "2025-08-10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeCount").value(1))
                .andExpect(jsonPath("$.activeProjectCount").value(1))
                .andExpect(jsonPath("$.departmentCount").value(1))
                .andExpect(jsonPath("$.activeProjects[0].projectName").value("Project Alpha"))
                .andExpect(jsonPath("$.activeProjects[0].participantCount").value(1));
    }

    @Test
    void getStatistics_missingDate_returnsBadRequest() throws Exception {
        mvc.perform(get("/api/statistics"))
                .andExpect(status().isBadRequest());
    }
}
