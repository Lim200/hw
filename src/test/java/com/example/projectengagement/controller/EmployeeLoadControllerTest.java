package com.example.projectengagement.controller;

import com.example.projectengagement.dto.EmployeeLoadDto;
import com.example.projectengagement.entity.Participation;
import com.example.projectengagement.entity.User;
import com.example.projectengagement.entity.Department;
import com.example.projectengagement.repository.ParticipationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeLoadControllerTest {

    @Mock
    private ParticipationRepository participationRepository;

    @InjectMocks
    private EmployeeLoadController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getEmployeeLoad_returnsCorrectLoadForGivenDate() {
        LocalDate date = LocalDate.of(2025, 8, 10);

        User user1 = new User();
        user1.setFirstName("Ivan");
        user1.setLastName("Ivanov");
        user1.setMiddleName("Ivanovich");
        Department dept1 = new Department();
        dept1.setName("Sales");
        user1.setDepartment(dept1);

        User user2 = new User();
        user2.setFirstName("Anna");
        user2.setLastName("Petrova");
        user2.setMiddleName(null);
        Department dept2 = new Department();
        dept2.setName("Marketing");
        user2.setDepartment(dept2);

        Participation p1 = new Participation();
        p1.setUser(user1);
        p1.setStartDate(LocalDate.of(2025, 1, 1));
        p1.setEndDate(LocalDate.of(2025, 12, 31));
        p1.setParticipationPercentage(50.0);

        Participation p2 = new Participation();
        p2.setUser(user1);
        p2.setStartDate(LocalDate.of(2025, 6, 1));
        p2.setEndDate(LocalDate.of(2025, 8, 15));
        p2.setParticipationPercentage(30.0);

        Participation p3 = new Participation();
        p3.setUser(user2);
        p3.setStartDate(LocalDate.of(2025, 7, 1));
        p3.setEndDate(LocalDate.of(2025, 7, 31));
        p3.setParticipationPercentage(80.0);

        when(participationRepository.findAll()).thenReturn(List.of(p1, p2, p3));

        List<EmployeeLoadDto> result = controller.getEmployeeLoad(date);

        // p3 неактивен на дату 2025-08-10, должен быть исключен
        assertEquals(1, result.size());

        EmployeeLoadDto ivanLoad = result.get(0);

        assertEquals("Ivanov Ivan Ivanovich", ivanLoad.getFullName());
        assertEquals("Sales", ivanLoad.getDepartmentName());
        assertEquals(80.0, ivanLoad.getTotalLoadPercentage());
    }

    @Test
    void getEmployeeLoad_excludesInactiveParticipations() {
        LocalDate date = LocalDate.of(2025, 8, 10);

        User user = new User();
        user.setFirstName("Ivan");
        user.setLastName("Ivanov");

        Participation p = new Participation();
        p.setUser(user);
        p.setStartDate(LocalDate.of(2025, 1, 1));
        p.setEndDate(LocalDate.of(2025, 7, 31)); // завершилось до даты
        p.setParticipationPercentage(50.0);

        when(participationRepository.findAll()).thenReturn(List.of(p));

        List<EmployeeLoadDto> result = controller.getEmployeeLoad(date);

        assertTrue(result.isEmpty(), "Expected empty result since participation inactive on date");
    }

    @Test
    void getEmployeeLoad_handlesNullParticipationPercentageAndDepartment() {
        LocalDate date = LocalDate.of(2025, 8, 10);

        User user = new User();
        user.setFirstName("Ivan");
        user.setLastName("Ivanov");
        user.setMiddleName(null);
        user.setDepartment(null);

        Participation p = new Participation();
        p.setUser(user);
        p.setStartDate(LocalDate.of(2025, 1, 1));
        p.setEndDate(LocalDate.of(2025, 12, 31));
        p.setParticipationPercentage(null);

        when(participationRepository.findAll()).thenReturn(List.of(p));

        List<EmployeeLoadDto> result = controller.getEmployeeLoad(date);

        assertEquals(1, result.size());
        EmployeeLoadDto dto = result.get(0);
        assertEquals("Ivanov Ivan", dto.getFullName());
        assertEquals("—", dto.getDepartmentName());
        assertEquals(0.0, dto.getTotalLoadPercentage());
    }
}
