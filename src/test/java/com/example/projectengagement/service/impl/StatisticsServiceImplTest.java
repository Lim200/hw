package com.example.projectengagement.service.impl;

import com.example.projectengagement.dto.ProjectStatsDto;
import com.example.projectengagement.dto.StatisticsDto;
import com.example.projectengagement.entity.*;
import com.example.projectengagement.repository.ParticipationRepository;
import com.example.projectengagement.repository.ProjectContractorRepository;
import com.example.projectengagement.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class StatisticsServiceImplTest {

    @Mock
    ParticipationRepository participationRepository;

    @Mock
    ProjectRepository projectRepository;

    @Mock
    ProjectContractorRepository projectContractorRepository;

    @InjectMocks
    StatisticsServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getStatistics_shouldCalculateCorrectly() {
        LocalDate date = LocalDate.of(2025, 8, 10);

        User user1 = User.builder().id(1L).build();
        User user2 = User.builder().id(2L).build();

        Project project1 = Project.builder()
                .id(1L).name("Alpha")
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2025, 12, 31))
                .isActive(true)
                .build();


        Project project2 = Project.builder()
                .id(2L).name("Beta")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .isActive(true)
                .build();


        Participation p1 = Participation.builder()
                .user(user1).project(project1)
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2025, 12, 31))
                .build();

        Participation p2 = Participation.builder()
                .user(user2).project(project1)
                .startDate(LocalDate.of(2025, 5, 1))
                .endDate(LocalDate.of(2025, 7, 31)) // неактивен
                .build();

        Participation p3 = Participation.builder()
                .user(user2).project(project2)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 12, 31)) // неактивен
                .build();

        Department dept1 = Department.builder().id(10L).build();
        Department dept2 = Department.builder().id(20L).build();

        ProjectContractor pc1 = ProjectContractor.builder().project(project1).department(dept1).build();
        ProjectContractor pc2 = ProjectContractor.builder().project(project2).department(dept2).build();

        when(participationRepository.findAll()).thenReturn(List.of(p1, p2, p3));
        when(projectRepository.findAll()).thenReturn(List.of(project1, project2));
        when(projectContractorRepository.findAll()).thenReturn(List.of(pc1, pc2));

        StatisticsDto result = service.getStatistics(date);

        assertThat(result.getEmployeeCount()).isEqualTo(1);
        assertThat(result.getActiveProjectCount()).isEqualTo(1);
        assertThat(result.getDepartmentCount()).isEqualTo(1);
        assertThat(result.getActiveProjects()).hasSize(1);
        assertThat(result.getActiveProjects().get(0).getProjectName()).isEqualTo("Alpha");
        assertThat(result.getActiveProjects().get(0).getParticipantCount()).isEqualTo(1);
    }

    @Test
    void create_shouldStoreStatistics() {
        LocalDate date = LocalDate.of(2025, 8, 10);
        StatisticsDto dto = StatisticsDto.builder()
                .date(date)
                .employeeCount(5)
                .activeProjectCount(2)
                .departmentCount(3)
                .activeProjects(List.of(new ProjectStatsDto("Alpha", 2L)))
                .build();

        StatisticsDto saved = service.create(dto);

        assertThat(saved).isEqualTo(dto);
        assertThat(service.findAll()).contains(dto);
    }

    @Test
    void update_shouldModifyExistingRecord() {
        LocalDate date = LocalDate.of(2025, 8, 10);
        StatisticsDto original = StatisticsDto.builder()
                .date(date)
                .employeeCount(1)
                .activeProjectCount(1)
                .departmentCount(1)
                .activeProjects(List.of(new ProjectStatsDto("Alpha", 1L)))
                .build();

        service.create(original);

        StatisticsDto updated = StatisticsDto.builder()
                .date(date)
                .employeeCount(10)
                .activeProjectCount(5)
                .departmentCount(2)
                .activeProjects(List.of(new ProjectStatsDto("Beta", 3L)))
                .build();

        StatisticsDto result = service.update(date, updated);

        assertThat(result.getEmployeeCount()).isEqualTo(10);
        assertThat(service.findAll()).contains(updated);
    }

    @Test
    void update_shouldThrowIfNotExists() {
        LocalDate date = LocalDate.of(2025, 8, 10);
        StatisticsDto dto = StatisticsDto.builder()
                .date(date)
                .employeeCount(1)
                .activeProjectCount(1)
                .departmentCount(1)
                .activeProjects(List.of(new ProjectStatsDto("Alpha", 1L)))
                .build();

        assertThatThrownBy(() -> service.update(date, dto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Statistics not found for date");
    }

    @Test
    void delete_shouldRemoveRecord() {
        LocalDate date = LocalDate.of(2025, 8, 10);
        StatisticsDto dto = StatisticsDto.builder()
                .date(date)
                .employeeCount(1)
                .activeProjectCount(1)
                .departmentCount(1)
                .activeProjects(List.of(new ProjectStatsDto("Alpha", 1L)))
                .build();

        service.create(dto);
        service.delete(date);

        assertThat(service.findAll()).doesNotContain(dto);
    }

    @Test
    void delete_shouldThrowIfNotExists() {
        LocalDate date = LocalDate.of(2025, 8, 10);

        assertThatThrownBy(() -> service.delete(date))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Statistics not found for date");
    }
}
