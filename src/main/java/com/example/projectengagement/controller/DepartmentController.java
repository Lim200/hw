package com.example.projectengagement.controller;

import com.example.projectengagement.dto.CreateDepartmentDto;
import com.example.projectengagement.entity.Department;
import com.example.projectengagement.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@Tag(name = "Departments", description = "API for managing departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    @Operation(
            summary = "Create a new department",
            description = "Adds a new department to the system and returns it with an assigned ID",
            requestBody = @RequestBody(
                    required = true,
                    description = "New department data",
                    content = @Content(
                            schema = @Schema(implementation = CreateDepartmentDto.class),
                            examples = @ExampleObject(
                                    name = "Department example",
                                    value = "{ \"name\": \"IT\", \"contractor\": false }"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Department successfully created",
                            content = @Content(schema = @Schema(implementation = Department.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation error",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponse"))
                    )
            }
    )
    @PostMapping
    public ResponseEntity<Department> createDepartment(
            @Valid @org.springframework.web.bind.annotation.RequestBody CreateDepartmentDto dto,
            UriComponentsBuilder uriBuilder
    ) {
        Department saved = departmentService.create(dto);
        URI location = uriBuilder.path("/api/departments/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }
}
