package com.example.projectengagement.service.impl;

import com.example.projectengagement.dto.CreateParticipationDto;
import com.example.projectengagement.entity.Participation;
import com.example.projectengagement.entity.Project;
import com.example.projectengagement.entity.User;
import com.example.projectengagement.exception.ResourceNotFoundException;
import com.example.projectengagement.repository.ParticipationRepository;
import com.example.projectengagement.repository.ProjectRepository;
import com.example.projectengagement.repository.UserRepository;
import com.example.projectengagement.service.ParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParticipationServiceImpl implements ParticipationService {
    private final ParticipationRepository participationRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    @Override
    public Participation create(CreateParticipationDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + dto.getUserId()));
        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + dto.getProjectId()));

        if (dto.getStartDate() != null && dto.getEndDate() != null &&
                dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new IllegalArgumentException("startDate cannot be after endDate");
        }

        Participation p = Participation.builder()
                .user(user)
                .project(project)
                .role(dto.getRole())
                .participationPercentage(dto.getParticipationPercentage())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .build();

        return participationRepository.save(p);
    }
}
