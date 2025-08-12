package com.example.projectengagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDepartmentDto {
    @NotBlank
    @Size(max = 100)
    private String name;

    // можно по умолчанию false
    private boolean contractor = false;
}

