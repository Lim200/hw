package com.example.projectengagement.service;

import com.example.projectengagement.dto.CreateParticipationDto;
import com.example.projectengagement.entity.Participation;
import com.example.projectengagement.entity.Project;
import com.example.projectengagement.entity.User;
import com.example.projectengagement.exception.ResourceNotFoundException;
import com.example.projectengagement.repository.ParticipationRepository;
import com.example.projectengagement.repository.ProjectRepository;
import com.example.projectengagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ParticipationServiceTest {

    private ParticipationRepository participationRepository;
    private UserRepository userRepository;
    private ProjectRepository projectRepository;
    private ParticipationService participationService;

    @BeforeEach
    void setUp() {
        participationRepository = mock(ParticipationRepository.class);
        userRepository = mock(UserRepository.class);
        projectRepository = mock(ProjectRepository.class);
        participationService = new ParticipationService(participationRepository, userRepository, projectRepository);
    }

    @Test
    void create_shouldSaveParticipation_whenDataIsValid() {
        // given
        CreateParticipationDto dto = new CreateParticipationDto(
                1L, 2L, "Developer", 50.0,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 6, 1)
        );

        User user = User.builder().id(1L).build(); // убрал name, чтобы совпадало с твоей моделью
        Project project = Project.builder().id(2L).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(projectRepository.findById(2L)).thenReturn(Optional.of(project));

        Participation saved = Participation.builder()
                .id(100L)
                .user(user)
                .project(project)
                .role("Developer")
                .participationPercentage(50.0)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .build();

        when(participationRepository.save(any(Participation.class))).thenReturn(saved);

        // when
        Participation result = participationService.create(dto);

        // then
        ArgumentCaptor<Participation> captor = ArgumentCaptor.forClass(Participation.class);
        verify(participationRepository).save(captor.capture());

        Participation captured = captor.getValue();
        assertThat(captured.getUser()).isEqualTo(user);
        assertThat(captured.getProject()).isEqualTo(project);
        assertThat(captured.getRole()).isEqualTo("Developer");

        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getRole()).isEqualTo("Developer");
    }

    @Test
    void create_shouldThrowException_whenUserNotFound() {
        // given
        CreateParticipationDto dto = new CreateParticipationDto(1L, 2L, "Developer", 50.0, null, null);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> participationService.create(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found: 1");

        verifyNoInteractions(projectRepository, participationRepository);
    }

    @Test
    void create_shouldThrowException_whenProjectNotFound() {
        // given
        CreateParticipationDto dto = new CreateParticipationDto(1L, 2L, "Developer", 50.0, null, null);
        User user = User.builder().id(1L).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(projectRepository.findById(2L)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> participationService.create(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Project not found: 2");

        verifyNoInteractions(participationRepository);
    }

    @Test
    void create_shouldThrowException_whenStartDateAfterEndDate() {
        // given
        CreateParticipationDto dto = new CreateParticipationDto(
                1L, 2L, "Developer", 50.0,
                LocalDate.of(2025, 6, 1),
                LocalDate.of(2025, 1, 1)
        );

        User user = User.builder().id(1L).build();
        Project project = Project.builder().id(2L).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(projectRepository.findById(2L)).thenReturn(Optional.of(project));

        // then
        assertThatThrownBy(() -> participationService.create(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("startDate cannot be after endDate");

        verifyNoInteractions(participationRepository);
    }
}
