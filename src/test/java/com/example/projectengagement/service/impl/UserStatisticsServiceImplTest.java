package com.example.projectengagement.service.impl;

import com.example.projectengagement.dto.UserProjectStatsDto;
import com.example.projectengagement.entity.User;
import com.example.projectengagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserStatisticsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserStatisticsServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserStats_shouldReturnMatchingUserStats() {
        User user = new User();
        user.setLastName("Ivanov");
        user.setFirstName("Ivan");
        user.setMiddleName("Ivanovich");

        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserProjectStatsDto> result = service.getUserStats(LocalDate.now(), "Ivanov Ivan Ivanovich");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFullName()).isEqualTo("Ivanov Ivan Ivanovich");
        assertThat(result.get(0).getParticipationCount()).isEqualTo(0);
        assertThat(result.get(0).getTotalLoadPercentage()).isEqualTo(0.0);
        assertThat(result.get(0).getManagedProjectCount()).isEqualTo(0);
    }

    @Test
    void getUserStats_shouldReturnEmptyListForInvalidName() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserProjectStatsDto> result = service.getUserStats(LocalDate.now(), "Ivanov");
        assertThat(result).isEmpty();

        result = service.getUserStats(LocalDate.now(), "");
        assertThat(result).isEmpty();
    }

    @Test
    void create_shouldStoreAndReturnDto() {
        UserProjectStatsDto dto = UserProjectStatsDto.builder()
                .fullName("Ivanov Ivan Ivanovich")
                .participationCount(2)
                .totalLoadPercentage(75.0)
                .managedProjectCount(1)
                .build();

        UserProjectStatsDto saved = service.create(dto);

        assertThat(saved).isEqualTo(dto);
        assertThat(service.findAll()).contains(dto);
    }

    @Test
    void update_shouldModifyExistingRecord() {
        UserProjectStatsDto original = UserProjectStatsDto.builder()
                .fullName("Ivanov Ivan Ivanovich")
                .participationCount(1)
                .totalLoadPercentage(50.0)
                .managedProjectCount(1)
                .build();

        service.create(original);

        UserProjectStatsDto updated = UserProjectStatsDto.builder()
                .fullName("Ivanov Ivan Ivanovich")
                .participationCount(3)
                .totalLoadPercentage(90.0)
                .managedProjectCount(2)
                .build();

        UserProjectStatsDto result = service.update("Ivanov Ivan Ivanovich", updated);

        assertThat(result.getParticipationCount()).isEqualTo(3);
        assertThat(result.getTotalLoadPercentage()).isEqualTo(90.0);
        assertThat(result.getManagedProjectCount()).isEqualTo(2);
    }

    @Test
    void update_shouldThrowIfNotExists() {
        UserProjectStatsDto dto = UserProjectStatsDto.builder()
                .fullName("Ivanov Ivan Ivanovich")
                .participationCount(1)
                .totalLoadPercentage(50.0)
                .managedProjectCount(1)
                .build();

        assertThatThrownBy(() -> service.update("Ivanov Ivan Ivanovich", dto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("User statistics not found");
    }

    @Test
    void delete_shouldRemoveRecord() {
        UserProjectStatsDto dto = UserProjectStatsDto.builder()
                .fullName("Ivanov Ivan Ivanovich")
                .participationCount(1)
                .totalLoadPercentage(50.0)
                .managedProjectCount(1)
                .build();

        service.create(dto);
        service.delete("Ivanov Ivan Ivanovich");

        assertThat(service.findAll()).doesNotContain(dto);
    }
}

