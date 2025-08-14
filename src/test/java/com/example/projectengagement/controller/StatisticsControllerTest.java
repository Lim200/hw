//package com.example.projectengagement.controller;
//
//import com.example.projectengagement.dto.ProjectStatsDto;
//import com.example.projectengagement.dto.StatisticsDto;
//import com.example.projectengagement.entity.Department;
//import com.example.projectengagement.entity.Participation;
//import com.example.projectengagement.entity.Project;
//import com.example.projectengagement.entity.ProjectContractor;
//import com.example.projectengagement.entity.User;
//import com.example.projectengagement.repository.ParticipationRepository;
//import com.example.projectengagement.repository.ProjectContractorRepository;
//import com.example.projectengagement.repository.ProjectRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class StatisticsControllerTest {
//
//    @Mock
//    private ParticipationRepository participationRepository;
//
//    @Mock
//    private ProjectRepository projectRepository;
//
//    @Mock
//    private ProjectContractorRepository projectContractorRepository;
//
//    @InjectMocks
//    private StatisticsController statisticsController;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void getStatistics_returnsCorrectStatistics() {
//        LocalDate targetDate = LocalDate.of(2025, 8, 10);
//
//        // Пользователи
//        User user1 = new User();
//        user1.setId(1L);
//        User user2 = new User();
//        user2.setId(2L);
//
//        // Проекты
//        Project project1 = new Project();
//        project1.setId(1L);
//        project1.setName("Project Alpha");
//        project1.setStartDate(LocalDate.of(2025, 1, 1));
//        project1.setEndDate(LocalDate.of(2025, 12, 31));
//        project1.setActive(true);
//
//        Project project2 = new Project();
//        project2.setId(2L);
//        project2.setName("Project Beta");
//        project2.setStartDate(LocalDate.of(2024, 1, 1));
//        project2.setEndDate(LocalDate.of(2024, 12, 31)); // Неактивен на дату
//        project2.setActive(true);
//
//        // Участия
//        Participation participation1 = new Participation();
//        participation1.setUser(user1);
//        participation1.setProject(project1);
//        participation1.setStartDate(LocalDate.of(2025, 1, 1));
//        participation1.setEndDate(LocalDate.of(2025, 12, 31));
//
//        Participation participation2 = new Participation();
//        participation2.setUser(user2);
//        participation2.setProject(project1);
//        participation2.setStartDate(LocalDate.of(2025, 5, 1));
//        participation2.setEndDate(LocalDate.of(2025, 7, 31)); // Неактивен на дату
//
//        Participation participation3 = new Participation();
//        participation3.setUser(user2);
//        participation3.setProject(project2);
//        participation3.setStartDate(LocalDate.of(2024, 1, 1));
//        participation3.setEndDate(LocalDate.of(2024, 12, 31));
//
//        // Департаменты
//        Department department1 = new Department();
//        department1.setId(10L);
//
//        Department department2 = new Department();
//        department2.setId(20L);
//
//        // ProjectContractors
//        ProjectContractor contractor1 = new ProjectContractor();
//        contractor1.setProject(project1);
//        contractor1.setDepartment(department1);
//
//        ProjectContractor contractor2 = new ProjectContractor();
//        contractor2.setProject(project2);
//        contractor2.setDepartment(department2);
//
//        // Моки
//        when(participationRepository.findAll()).thenReturn(List.of(participation1, participation2, participation3));
//        when(projectRepository.findAll()).thenReturn(List.of(project1, project2));
//        when(projectContractorRepository.findAll()).thenReturn(List.of(contractor1, contractor2));
//
//        // Вызов тестируемого метода
//        StatisticsDto result = statisticsController.getStatistics(targetDate);
//
//        // Проверки
//        assertEquals(1, result.getEmployeeCount()); // Только user1 активен на targetDate
//        assertEquals(1, result.getActiveProjectCount()); // Только project1 активен
//        assertEquals(1, result.getDepartmentCount()); // Только department1 связан с активным проектом
//
//        assertEquals(1, result.getActiveProjects().size());
//        ProjectStatsDto statsDto = result.getActiveProjects().get(0);
//        assertEquals("Project Alpha", statsDto.getProjectName());
//        assertEquals(1L, statsDto.getParticipantCount()); // user1 участвует активно в project1
//    }
//}
//
