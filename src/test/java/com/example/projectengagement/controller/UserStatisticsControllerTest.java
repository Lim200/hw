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

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserStatisticsControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ParticipationRepository participationRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private UserStatisticsController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserStats_returnsCorrectStatistics() {
        LocalDate targetDate = LocalDate.of(2025, 8, 10);

        // Пользователь
        User user = new User();
        user.setId(1L);
        user.setLastName("Ivanov");
        user.setFirstName("Ivan");
        user.setMiddleName("Ivanovich");

        // Другой пользователь (для проверки фильтрации)
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setLastName("Petrov");
        otherUser.setFirstName("Petr");
        otherUser.setMiddleName("Petrovich");

        // Создаем проекты с id для корректного distinct
        Project project1 = new Project();
        project1.setId(1L);
        Project project2 = new Project();
        project2.setId(2L);
        Project projectOther = new Project();
        projectOther.setId(3L);

        // Участия пользователя
        Participation participation1 = new Participation();
        participation1.setUser(user);
        participation1.setProject(project1);
        participation1.setParticipationPercentage(50.0);
        participation1.setStartDate(LocalDate.of(2025, 1, 1));
        participation1.setEndDate(LocalDate.of(2025, 12, 31));

        Participation participation2 = new Participation();
        participation2.setUser(user);
        participation2.setProject(project2);
        participation2.setParticipationPercentage(30.0);
        participation2.setStartDate(LocalDate.of(2025, 1, 1));
        participation2.setEndDate(LocalDate.of(2025, 12, 31));

        Participation participationOther = new Participation();
        participationOther.setUser(otherUser);
        participationOther.setProject(projectOther);
        participationOther.setParticipationPercentage(70.0);
        participationOther.setStartDate(LocalDate.of(2025, 1, 1));
        participationOther.setEndDate(LocalDate.of(2025, 12, 31));

        // Проекты управляемые пользователем
        Project managedProject1 = new Project();
        managedProject1.setId(10L);
        managedProject1.setManager(user);
        managedProject1.setStartDate(LocalDate.of(2025, 1, 1));
        managedProject1.setEndDate(LocalDate.of(2025, 12, 31));

        Project managedProject2 = new Project();
        managedProject2.setId(11L);
        managedProject2.setManager(user);
        managedProject2.setStartDate(LocalDate.of(2024, 1, 1));
        managedProject2.setEndDate(LocalDate.of(2024, 12, 31)); // неактивный на targetDate

        Project managedProjectOther = new Project();
        managedProjectOther.setId(12L);
        managedProjectOther.setManager(otherUser);
        managedProjectOther.setStartDate(LocalDate.of(2025, 1, 1));
        managedProjectOther.setEndDate(LocalDate.of(2025, 12, 31));

        when(userRepository.findAll()).thenReturn(List.of(user, otherUser));
        when(participationRepository.findAll()).thenReturn(List.of(participation1, participation2, participationOther));
        when(projectRepository.findAll()).thenReturn(List.of(managedProject1, managedProject2, managedProjectOther));

        List<UserProjectStatsDto> result = controller.getUserStats(targetDate, "Ivanov Ivan Ivanovich");

        assertEquals(1, result.size());
        UserProjectStatsDto stats = result.get(0);

        assertEquals("Ivanov Ivan Ivanovich", stats.getFullName());
        assertEquals(2, stats.getParticipationCount()); // 2 уникальных проекта участия
        assertEquals(80.0, stats.getTotalLoadPercentage(), 0.001); // 50 + 30
        assertEquals(1, stats.getManagedProjectCount()); // только один активный проект на targetDate

        // Проверка, что пользователи без совпадения по ФИО не попадают
        List<UserProjectStatsDto> noResult = controller.getUserStats(targetDate, "Petrov Petr Petrovich");
        assertEquals(1, noResult.size());
        assertEquals("Petrov Petr Petrovich", noResult.get(0).getFullName());
    }

    @Test
    void getUserStats_emptyListForInvalidName() {
        // Передаем неполное имя
        List<UserProjectStatsDto> result = controller.getUserStats(LocalDate.now(), "Ivanov");
        assertTrue(result.isEmpty());

        result = controller.getUserStats(LocalDate.now(), "");
        assertTrue(result.isEmpty());
    }
}
