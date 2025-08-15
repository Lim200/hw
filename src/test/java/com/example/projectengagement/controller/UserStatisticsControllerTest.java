package com.example.projectengagement.controller;

import com.example.projectengagement.dto.UserProjectStatsDto;
import com.example.projectengagement.entity.Participation;
import com.example.projectengagement.entity.Project;
import com.example.projectengagement.entity.User;
import com.example.projectengagement.repository.ParticipationRepository;
import com.example.projectengagement.repository.ProjectRepository;
import com.example.projectengagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import com.example.projectengagement.service.UserStatisticsService;


import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class UserStatisticsControllerTest {

    @Mock
    private UserStatisticsService userStatisticsService;

    @InjectMocks
    private UserStatisticsController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserStats_returnsCorrectStatistics() {
        LocalDate targetDate = LocalDate.of(2025, 8, 10);
        String fullName = "Ivanov Ivan Ivanovich";

        UserProjectStatsDto dto = UserProjectStatsDto.builder()
                .fullName(fullName)
                .participationCount(2)
                .totalLoadPercentage(80.0)
                .managedProjectCount(1)
                .build();

        when(userStatisticsService.getUserStats(targetDate, fullName)).thenReturn(List.of(dto));

        List<UserProjectStatsDto> result = controller.getUserStats(targetDate, fullName);

        assertEquals(1, result.size());
        UserProjectStatsDto stats = result.get(0);

        assertEquals(fullName, stats.getFullName());
        assertEquals(2, stats.getParticipationCount());
        assertEquals(80.0, stats.getTotalLoadPercentage(), 0.001);
        assertEquals(1, stats.getManagedProjectCount());
    }

    @Test
    void getUserStats_emptyListForInvalidName() {
        LocalDate date = LocalDate.now();

        when(userStatisticsService.getUserStats(date, "Ivanov")).thenReturn(List.of());
        when(userStatisticsService.getUserStats(date, "")).thenReturn(List.of());

        assertTrue(controller.getUserStats(date, "Ivanov").isEmpty());
        assertTrue(controller.getUserStats(date, "").isEmpty());
    }
}
