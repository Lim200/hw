package com.example.projectengagement.service.impl;

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
import org.mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ParticipationServiceImplTest {

    @Mock
    ParticipationRepository participationRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ProjectRepository projectRepository;

    @InjectMocks
    ParticipationServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_shouldSaveParticipation_whenValid() {
        CreateParticipationDto dto = new CreateParticipationDto(
                1L, 2L, "Dev", 60.0,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 6, 1)
        );

        User user = User.builder().id(1L).build();
        Project project = Project.builder().id(2L).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(projectRepository.findById(2L)).thenReturn(Optional.of(project));

        Participation expected = Participation.builder()
                .id(100L)
                .user(user)
                .project(project)
                .role("Dev")
                .participationPercentage(60.0)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .build();

        when(participationRepository.save(any())).thenReturn(expected);

        Participation result = service.create(dto);

        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getRole()).isEqualTo("Dev");
        assertThat(result.getParticipationPercentage()).isEqualTo(60.0);
    }

    @Test
    void create_shouldThrow_whenUserNotFound() {
        CreateParticipationDto dto = new CreateParticipationDto(1L, 2L, "Dev", 60.0, null, null);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found: 1");

        verifyNoInteractions(projectRepository, participationRepository);
    }

    @Test
    void create_shouldThrow_whenProjectNotFound() {
        CreateParticipationDto dto = new CreateParticipationDto(1L, 2L, "Dev", 60.0, null, null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(User.builder().id(1L).build()));
        when(projectRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Project not found: 2");

        verifyNoInteractions(participationRepository);
    }

    @Test
    void create_shouldThrow_whenStartDateAfterEndDate() {
        CreateParticipationDto dto = new CreateParticipationDto(
                1L, 2L, "Dev", 60.0,
                LocalDate.of(2025, 6, 1),
                LocalDate.of(2025, 1, 1)
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(User.builder().id(1L).build()));
        when(projectRepository.findById(2L)).thenReturn(Optional.of(Project.builder().id(2L).build()));

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("startDate cannot be after endDate");

        verifyNoInteractions(participationRepository);
    }

    @Test
    void findAll_shouldReturnList() {
        Participation p1 = Participation.builder().id(1L).build();
        Participation p2 = Participation.builder().id(2L).build();

        when(participationRepository.findAll()).thenReturn(List.of(p1, p2));

        List<Participation> result = service.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void findById_shouldReturnParticipation() {
        Participation p = Participation.builder().id(10L).build();
        when(participationRepository.findById(10L)).thenReturn(Optional.of(p));

        Optional<Participation> result = service.findById(10L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(10L);
    }

    @Test
    void update_shouldModifyAndSaveParticipation() {
        Participation existing = Participation.builder().id(1L).role("Old").build();
        Participation updated = Participation.builder().role("New").participationPercentage(80.0).build();

        when(participationRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(participationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Participation result = service.update(1L, updated);

        assertThat(result.getRole()).isEqualTo("New");
        assertThat(result.getParticipationPercentage()).isEqualTo(80.0);
    }

    @Test
    void delete_shouldRemoveParticipation_whenExists() {
        when(participationRepository.existsById(5L)).thenReturn(true);

        service.delete(5L);

        verify(participationRepository).deleteById(5L);
    }

    @Test
    void delete_shouldThrow_whenNotFound() {
        when(participationRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Participation not found: 99");
    }
}
