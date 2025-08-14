package com.example.projectengagement.controller;

import com.example.projectengagement.dto.CreateDepartmentDto;
import com.example.projectengagement.entity.Department;
import com.example.projectengagement.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@Tag(name = "Departments", description = "API for managing departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    // ✅ Создание департамента через DTO
    @PostMapping("/dto")
    @Operation(
            summary = "Create a new department (DTO)",
            description = "Adds a new department using DTO and returns it with an assigned ID",
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
    public ResponseEntity<Department> createFromDto(
            @Valid @RequestBody CreateDepartmentDto dto,
            UriComponentsBuilder uriBuilder
    ) {
        Department saved = departmentService.create(dto);
        URI location = uriBuilder.path("/api/departments/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @PostMapping
    @Operation(summary = "Create department (raw entity)")
    public ResponseEntity<Department> create(@RequestBody Department department) {
        return ResponseEntity.ok(departmentService.create(department));
    }

    @GetMapping
    @Operation(summary = "Get all departments")
    public List<Department> findAll() {
        return departmentService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get department by ID")
    public ResponseEntity<Department> findById(@PathVariable Long id) {
        return departmentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update department")
    public ResponseEntity<Department> update(@PathVariable Long id, @RequestBody Department department) {
        return ResponseEntity.ok(departmentService.update(id, department));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete department")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        departmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
