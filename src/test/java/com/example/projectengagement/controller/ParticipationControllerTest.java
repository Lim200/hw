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
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ParticipationController.class)
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
class ParticipationControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    ParticipationService participationService;

    Participation sampleParticipation() {
        return Participation.builder()
                .id(100L)
                .user(User.builder().id(1L).build())
                .project(Project.builder().id(2L).build())
                .role("Developer")
                .participationPercentage(50.0)
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2025, 6, 1))
                .build();
    }

    @Test
    void createParticipation_returnsCreatedParticipation() throws Exception {
        when(participationService.create(any(CreateParticipationDto.class))).thenReturn(sampleParticipation());

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

    @Test
    void getAllParticipations_returnsList() throws Exception {
        when(participationService.findAll()).thenReturn(List.of(sampleParticipation()));

        mvc.perform(get("/api/participations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[0].role").value("Developer"));
    }

    @Test
    void getParticipationById_found() throws Exception {
        when(participationService.findById(100L)).thenReturn(Optional.of(sampleParticipation()));

        mvc.perform(get("/api/participations/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.role").value("Developer"));
    }

    @Test
    void getParticipationById_notFound() throws Exception {
        when(participationService.findById(999L)).thenReturn(Optional.empty());

        mvc.perform(get("/api/participations/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateParticipation_returnsUpdated() throws Exception {
        Participation updated = sampleParticipation();
        updated.setRole("Lead Developer");
        updated.setParticipationPercentage(75.0);

        when(participationService.update(eq(100L), any(Participation.class))).thenReturn(updated);

        String json = """
                {
                  "id": 100,
                  "user": { "id": 1 },
                  "project": { "id": 2 },
                  "role": "Lead Developer",
                  "participationPercentage": 75.0,
                  "startDate": "2025-01-01",
                  "endDate": "2025-06-01"
                }
                """;

        mvc.perform(put("/api/participations/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("Lead Developer"))
                .andExpect(jsonPath("$.participationPercentage").value(75.0));
    }

    @Test
    void deleteParticipation_returnsNoContent() throws Exception {
        doNothing().when(participationService).delete(100L);

        mvc.perform(delete("/api/participations/100"))
                .andExpect(status().isNoContent());
    }
}
