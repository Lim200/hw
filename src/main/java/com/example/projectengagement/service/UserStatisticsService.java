package com.example.projectengagement.service;

import com.example.projectengagement.dto.UserProjectStatsDto;

import java.time.LocalDate;
import java.util.List;

public interface UserStatisticsService {
    List<UserProjectStatsDto> getUserStats(LocalDate date, String fullName);
}
