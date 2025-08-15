package com.example.projectengagement.controller;

import com.example.projectengagement.config.TestSecurityConfig;
import com.example.projectengagement.dto.CreateParticipationDto;
import com.example.projectengagement.entity.Participation;
import com.example.projectengagement.entity.Project;
import com.example.projectengagement.entity.User;
import com.example.projectengagement.exception.GlobalExceptionHandler;
import com.example.projectengagement.service.ParticipationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ParticipationController.class)
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
class ParticipationControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    ParticipationService participationService;

    @Test
    void createParticipation_returnsCreatedParticipation() throws Exception {
        CreateParticipationDto dto = new CreateParticipationDto(
                1L, 2L, "Developer", 50.0,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 6, 1)
        );

        User user = User.builder().id(1L).build();
        Project project = Project.builder().id(2L).build();

        Participation saved = Participation.builder()
                .id(100L)
                .user(user)
                .project(project)
                .role("Developer")
                .participationPercentage(50.0)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .build();

        when(participationService.create(any(CreateParticipationDto.class))).thenReturn(saved);

        String json = """
                {
                  "userId": 1,
                  "projectId": 2,
                  "role": "Developer",
                  "participationPercentage": 50.0,
                  "startDate": "2025-01-01",
                  "endDate": "2025-06-01"
                }
                """;

        mvc.perform(post("/api/participations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/participations/100")))
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.role").value("Developer"))
                .andExpect(jsonPath("$.participationPercentage").value(50.0));
    }

    @Test
    void createParticipation_invalidPayload_returnsBadRequest() throws Exception {
        String invalidJson = """
                {
                  "userId": null,
                  "projectId": null,
                  "role": "",
                  "participationPercentage": -10
                }
                """;

        mvc.perform(post("/api/participations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}

