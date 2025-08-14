package com.example.projectengagement.service.impl;

import com.example.projectengagement.dto.UserProjectStatsDto;
import com.example.projectengagement.entity.User;
import com.example.projectengagement.repository.UserRepository;
import com.example.projectengagement.service.UserStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class UserStatisticsServiceImpl implements UserStatisticsService {

    private final UserRepository userRepository;

    // Временное хранилище DTO по полному имени
    private final Map<String, UserProjectStatsDto> statsStorage = new ConcurrentHashMap<>();

    @Override
    public List<UserProjectStatsDto> getUserStats(LocalDate date, String fullName) {
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length < 2) return List.of();

        String lastName = parts[0];
        String firstName = parts[1];
        String middleName = parts.length > 2 ? parts[2] : "";

        List<User> matchedUsers = userRepository.findAll().stream()
                .filter(u -> u.getLastName().equalsIgnoreCase(lastName))
                .filter(u -> u.getFirstName().equalsIgnoreCase(firstName))
                .filter(u -> middleName.isEmpty() || Optional.ofNullable(u.getMiddleName()).orElse("").equalsIgnoreCase(middleName))
                .toList();

        return matchedUsers.stream()
                .map(user -> new UserProjectStatsDto(
                        buildFullName(user),
                        0,    // participationCount — заглушка
                        0.0,  // totalLoadPercentage — заглушка
                        0     // managedProjectCount — заглушка
                ))
                .toList();
    }

    @Override
    public UserProjectStatsDto create(UserProjectStatsDto dto) {
        statsStorage.put(dto.getFullName(), dto);
        return dto;
    }

    @Override
    public List<UserProjectStatsDto> findAll() {
        return new ArrayList<>(statsStorage.values());
    }

    @Override
    public UserProjectStatsDto update(String fullName, UserProjectStatsDto dto) {
        if (!statsStorage.containsKey(fullName)) {
            throw new NoSuchElementException("User statistics not found for: " + fullName);
        }
        statsStorage.put(fullName, dto);
        return dto;
    }

    @Override
    public void delete(String fullName) {
        statsStorage.remove(fullName);
    }

    private String buildFullName(User user) {
        return user.getLastName() + " " + user.getFirstName() +
                (user.getMiddleName() != null && !user.getMiddleName().isBlank() ? " " + user.getMiddleName() : "");
    }
}
