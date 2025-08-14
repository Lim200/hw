package com.example.projectengagement.service;

import com.example.projectengagement.dto.CreateParticipationDto;
import com.example.projectengagement.entity.Participation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ParticipationService {
    @Transactional
    Participation create(CreateParticipationDto dto);
    List<Participation> findAll();
    Optional<Participation> findById(Long id);
    Participation update(Long id, Participation participation);
    void delete(Long id);

}
