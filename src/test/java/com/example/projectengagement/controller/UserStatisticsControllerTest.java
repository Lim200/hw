package com.example.projectengagement.controller;

import com.example.projectengagement.config.TestSecurityConfig;
import com.example.projectengagement.dto.UserProjectStatsDto;
import com.example.projectengagement.exception.GlobalExceptionHandler;
import com.example.projectengagement.service.UserStatisticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserStatisticsController.class)
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
class UserStatisticsControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    UserStatisticsService userStatisticsService;

    UserProjectStatsDto sampleDto() {
        return UserProjectStatsDto.builder()
                .fullName("Ivanov Ivan Ivanovich")
                .participationCount(2)
                .totalLoadPercentage(80.0)
                .managedProjectCount(1)
                .build();
    }

    @Test
    void getUserStats_returnsExpectedJson() throws Exception {
        LocalDate date = LocalDate.of(2025, 8, 10);
        when(userStatisticsService.getUserStats(date, "Ivanov Ivan Ivanovich"))
                .thenReturn(List.of(sampleDto()));

        mvc.perform(get("/api/user-statistics")
                        .param("date", "2025-08-10")
                        .param("fullName", "Ivanov Ivan Ivanovich"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value("Ivanov Ivan Ivanovich"))
                .andExpect(jsonPath("$[0].participationCount").value(2))
                .andExpect(jsonPath("$[0].totalLoadPercentage").value(80.0))
                .andExpect(jsonPath("$[0].managedProjectCount").value(1));
    }

    @Test
    void createUserStats_returnsCreated() throws Exception {
        when(userStatisticsService.create(any(UserProjectStatsDto.class))).thenReturn(sampleDto());

        String json = """
            {
              "fullName": "Ivanov Ivan Ivanovich",
              "participationCount": 2,
              "totalLoadPercentage": 80.0,
              "managedProjectCount": 1
            }
            """;

        // ✅ ожидаем %20 вместо +
        String expectedPath = "/api/user-statistics/Ivanov%20Ivan%20Ivanovich";

        mvc.perform(post("/api/user-statistics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString(expectedPath)))
                .andExpect(jsonPath("$.fullName").value("Ivanov Ivan Ivanovich"));
    }


    @Test
    void getAllUserStats_returnsList() throws Exception {
        when(userStatisticsService.findAll()).thenReturn(List.of(sampleDto()));

        mvc.perform(get("/api/user-statistics/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value("Ivanov Ivan Ivanovich"));
    }

    @Test
    void updateUserStats_returnsUpdated() throws Exception {
        UserProjectStatsDto updated = sampleDto();
        updated.setTotalLoadPercentage(90.0);

        when(userStatisticsService.update(eq("Ivanov Ivan Ivanovich"), any(UserProjectStatsDto.class)))
                .thenReturn(updated);

        String json = """
                {
                  "fullName": "Ivanov Ivan Ivanovich",
                  "participationCount": 2,
                  "totalLoadPercentage": 90.0,
                  "managedProjectCount": 1
                }
                """;

        mvc.perform(put("/api/user-statistics/Ivanov Ivan Ivanovich")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalLoadPercentage").value(90.0));
    }

    @Test
    void deleteUserStats_returnsNoContent() throws Exception {
        doNothing().when(userStatisticsService).delete("Ivanov Ivan Ivanovich");

        mvc.perform(delete("/api/user-statistics/Ivanov Ivan Ivanovich"))
                .andExpect(status().isNoContent());
    }
}
