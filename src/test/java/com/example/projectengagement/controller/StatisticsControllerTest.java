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
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatisticsController.class)
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
class StatisticsControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    StatisticsService statisticsService;

    StatisticsDto sampleDto() {
        return StatisticsDto.builder()
                .date(LocalDate.of(2025, 8, 10))
                .employeeCount(1)
                .activeProjectCount(1)
                .departmentCount(1)
                .activeProjects(List.of(new ProjectStatsDto("Project Alpha", 1L)))
                .build();
    }

    @Test
    void getStatistics_returnsExpectedJson() throws Exception {
        when(statisticsService.getStatistics(LocalDate.of(2025, 8, 10))).thenReturn(sampleDto());

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

    @Test
    void createStatistics_returnsCreated() throws Exception {
        StatisticsDto dto = sampleDto();
        when(statisticsService.create(any(StatisticsDto.class))).thenReturn(dto);

        String json = """
                {
                  "date": "2025-08-10",
                  "employeeCount": 1,
                  "activeProjectCount": 1,
                  "departmentCount": 1,
                  "activeProjects": [
                    {
                      "projectName": "Project Alpha",
                      "participantCount": 1
                    }
                  ]
                }
                """;

        mvc.perform(post("/api/statistics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/statistics/2025-08-10")))
                .andExpect(jsonPath("$.date").value("2025-08-10"));
    }

    @Test
    void getAllStatistics_returnsList() throws Exception {
        when(statisticsService.findAll()).thenReturn(List.of(sampleDto()));

        mvc.perform(get("/api/statistics/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date").value("2025-08-10"))
                .andExpect(jsonPath("$[0].employeeCount").value(1));
    }

    @Test
    void updateStatistics_returnsUpdated() throws Exception {
        StatisticsDto updated = sampleDto();
        updated.setEmployeeCount(5);
        when(statisticsService.update(eq(LocalDate.of(2025, 8, 10)), any(StatisticsDto.class))).thenReturn(updated);

        String json = """
                {
                  "date": "2025-08-10",
                  "employeeCount": 5,
                  "activeProjectCount": 1,
                  "departmentCount": 1,
                  "activeProjects": [
                    {
                      "projectName": "Project Alpha",
                      "participantCount": 1
                    }
                  ]
                }
                """;

        mvc.perform(put("/api/statistics/2025-08-10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeCount").value(5));
    }

    @Test
    void deleteStatistics_returnsNoContent() throws Exception {
        doNothing().when(statisticsService).delete(LocalDate.of(2025, 8, 10));

        mvc.perform(delete("/api/statistics/2025-08-10"))
                .andExpect(status().isNoContent());
    }
}
