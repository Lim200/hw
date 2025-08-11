package com.example.projectengagement.repository;

import com.example.projectengagement.entity.Department;
import com.example.projectengagement.entity.DepartmentManager;
import com.example.projectengagement.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
class DepartmentManagerRepositoryTest {

    @Container
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void configureDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private DepartmentManagerRepository departmentManagerRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    private Department department;
    private User user;

    @BeforeEach
    void setUp() {
        department = departmentRepository.save(
                Department.builder()
                        .name("IT Department")
                        .isContractor(false)
                        .build()
        );

        user = userRepository.save(
                User.builder()
                        .firstName("John")
                        .lastName("Doe")
                        .middleName("A.")
                        .position("Manager")
                        .department(department)
                        .build()
        );

        DepartmentManager manager = DepartmentManager.builder()
                .department(department)
                .user(user)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(null)
                .build();

        departmentManagerRepository.save(manager);
    }

    @Test
    void shouldFindDepartmentManagersByDepartmentId() {
        List<DepartmentManager> managers = departmentManagerRepository.findByDepartmentId(department.getId());
        assertThat(managers).hasSize(1);
        assertThat(managers.get(0).getUser().getFirstName()).isEqualTo("John");
    }
}
