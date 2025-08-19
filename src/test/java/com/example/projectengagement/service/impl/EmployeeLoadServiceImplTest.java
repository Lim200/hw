package com.example.projectengagement.service.impl;

import com.example.projectengagement.dto.EmployeeLoadDto;
import com.example.projectengagement.entity.Department;
import com.example.projectengagement.entity.Participation;
import com.example.projectengagement.entity.User;
import com.example.projectengagement.repository.ParticipationRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EmployeeLoadServiceImplTest {

    @Test
    void getEmployeeLoad_returnsCorrectDto() {
        // Arrange
        ParticipationRepository mockRepo = mock(ParticipationRepository.class);
        EmployeeLoadServiceImpl service = new EmployeeLoadServiceImpl(mockRepo);

        User user = new User();
        user.setFirstName("Ivan");
        user.setLastName("Ivanov");
        user.setMiddleName(null);

        Department department = new Department();
        department.setName("Sales");
        user.setDepartment(department);

        Participation p1 = new Participation();
        p1.setUser(user);
        p1.setStartDate(LocalDate.of(2025, 1, 1));
        p1.setEndDate(LocalDate.of(2025, 12, 31));
        p1.setParticipationPercentage(50.0);

        Participation p2 = new Participation();
        p2.setUser(user);
        p2.setStartDate(LocalDate.of(2025, 6, 1));
        p2.setEndDate(LocalDate.of(2025, 8, 31));
        p2.setParticipationPercentage(25.0);

        when(mockRepo.findAll()).thenReturn(List.of(p1, p2));

        // Act
        List<EmployeeLoadDto> result = service.getEmployeeLoad(LocalDate.of(2025, 8, 10));

        // Assert
        assertThat(result).hasSize(1);
        EmployeeLoadDto dto = result.get(0);
        assertThat(dto.getFullName()).isEqualTo("Ivanov Ivan");
        assertThat(dto.getDepartmentName()).isEqualTo("Sales");
        assertThat(dto.getTotalLoadPercentage()).isEqualTo(75.0);
    }
}
