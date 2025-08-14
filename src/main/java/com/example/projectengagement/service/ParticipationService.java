package com.example.projectengagement.service;

import com.example.projectengagement.dto.CreateParticipationDto;
import com.example.projectengagement.entity.Participation;
import org.springframework.transaction.annotation.Transactional;

public interface ParticipationService {
    @Transactional
    Participation create(CreateParticipationDto dto);
}
