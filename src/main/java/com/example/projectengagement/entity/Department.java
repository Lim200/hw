package com.example.projectengagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "departments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "is_contractor", nullable = false)
    private boolean isContractor = false;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private List<User> users;
}
