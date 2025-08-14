package com.example.projectengagement.service;

import com.example.projectengagement.dto.StatisticsDto;

import java.time.LocalDate;

public interface StatisticsService {
    StatisticsDto getStatistics(LocalDate date);
}

