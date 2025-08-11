package com.example.projectengagement.controller;

import com.example.projectengagement.config.TestSecurityConfig;
import com.example.projectengagement.entity.Department;
import com.example.projectengagement.entity.Participation;
import com.example.projectengagement.entity.Project;
import com.example.projectengagement.entity.User;
import com.example.projectengagement.exception.GlobalExceptionHandler;
import com.example.projectengagement.repository.ParticipationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.closeTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DepartmentLoadController.class)
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
class DepartmentLoadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParticipationRepository participationRepository;

    @Test
    @DisplayName("Возвращает список загрузок сотрудников для заданного отдела и даты")
    void shouldReturnEmployeeProjectLoads() throws Exception {
        Department dept = Department.builder().id(1L).name("Отдел разработки").build();
        User user = User.builder()
                .id(1L)
                .lastName("Иванов")
                .firstName("Алексей")
                .middleName("Сергеевич")
                .position("Разработчик")
                .department(dept)
                .build();
        Project project = Project.builder()
                .id(1L)
                .name("Проект Alpha")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .build();

        Participation p = Participation.builder()
                .id(1L)
                .user(user)
                .project(project)
                .participationPercentage(50.0)
                .startDate(LocalDate.of(2024, 2, 1))
                .endDate(LocalDate.of(2024, 6, 30))
                .build();

        when(participationRepository.findAll()).thenReturn(List.of(p));

        mockMvc.perform(get("/api/department-load")
                        .param("date", "2024-06-15")
                        .param("department", "Отдел разработки"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].departmentName", is("Отдел разработки")))
                .andExpect(jsonPath("$[0].employeeFullName", is("Иванов Алексей Сергеевич")))
                .andExpect(jsonPath("$[0].projectName", is("Проект Alpha")))
                .andExpect(jsonPath("$[0].loadPercentage", closeTo(50.0, 0.001)));
    }

    @Test
    @DisplayName("Фильтрует неактивные участия (start > date или end < date)")
    void shouldFilterOutInactiveParticipations() throws Exception {
        Department d = Department.builder().id(2L).name("IT").build();
        User u1 = User.builder().id(2L).lastName("Петров").firstName("Иван").position("Dev").department(d).build();
        User u2 = User.builder().id(3L).lastName("Сидоров").firstName("Олег").position("Dev").department(d).build();

        // активное участие
        Participation active = Participation.builder()
                .id(2L)
                .user(u1)
                .project(Project.builder().id(2L).name("P").startDate(LocalDate.of(2024,1,1)).endDate(LocalDate.of(2024,12,31)).build())
                .participationPercentage(30.0)
                .startDate(LocalDate.of(2024,1,1))
                .endDate(LocalDate.of(2024,12,31))
                .build();

        // неактивное: начало позже target
        Participation inactive = Participation.builder()
                .id(3L)
                .user(u2)
                .project(Project.builder().id(3L).name("Q").startDate(LocalDate.of(2025,1,1)).endDate(LocalDate.of(2025,12,31)).build())
                .participationPercentage(40.0)
                .startDate(LocalDate.of(2025,1,1))
                .endDate(null)
                .build();

        when(participationRepository.findAll()).thenReturn(List.of(active, inactive));

        mockMvc.perform(get("/api/department-load")
                        .param("date", "2024-06-01")
                        .param("department", "it")) // small-case, проверка регистра — контроллер сравнивает ignoreCase
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].employeeFullName", is("Петров Иван")));
    }

    @Test
    @DisplayName("Обрабатывает null project и null participationPercentage (должен вернуть — и 0.0)")
    void shouldHandleNullProjectAndParticipationPercentage() throws Exception {
        Department d = Department.builder().id(5L).name("HR").build();
        User u = User.builder().id(5L).lastName("Кузнецов").firstName("Антон").position("HR").department(d).build();

        Participation p = Participation.builder()
                .id(10L)
                .user(u)
                .project(null)
                .participationPercentage(null)
                .startDate(LocalDate.of(2023, 1, 1))
                .endDate(null)
                .build();

        when(participationRepository.findAll()).thenReturn(List.of(p));

        mockMvc.perform(get("/api/department-load")
                        .param("date", "2024-06-01")
                        .param("department", "HR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].projectName", is("—")))
                .andExpect(jsonPath("$[0].loadPercentage", closeTo(0.0, 0.001)));
    }

    @Test
    @DisplayName("Валидация: пустой department -> 400 Bad Request")
    void shouldReturnBadRequestWhenDepartmentBlank() throws Exception {
        mockMvc.perform(get("/api/department-load")
                        .param("date", "2024-06-01")
                        .param("department", "")) // пустая строка
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Валидация: отсутствие параметра date -> 400 Bad Request")
    void shouldReturnBadRequestWhenDateMissing() throws Exception {
        mockMvc.perform(get("/api/department-load")
                        .param("department", "Отдел разработки")) // нет date
                .andExpect(status().isBadRequest());
    }
}
