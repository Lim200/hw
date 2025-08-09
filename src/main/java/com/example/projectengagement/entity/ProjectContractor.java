package com.example.projectengagement.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "project_contractors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectContractor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(optional = false)
    @JoinColumn(name = "department_id")
    private Department department;
}
