package com.example.projectengagement.service;

import com.example.projectengagement.dto.StatisticsDto;

import java.time.LocalDate;
import java.util.List;

public interface StatisticsService {
    StatisticsDto getStatistics(LocalDate date);
    StatisticsDto create(StatisticsDto dto);
    List<StatisticsDto> findAll();
    StatisticsDto update(LocalDate date, StatisticsDto dto);
    void delete(LocalDate date);

}

