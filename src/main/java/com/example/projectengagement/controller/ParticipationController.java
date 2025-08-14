package com.example.projectengagement.controller;

import com.example.projectengagement.dto.CreateParticipationDto;
import com.example.projectengagement.entity.Participation;
import com.example.projectengagement.service.ParticipationService;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/participations")
@RequiredArgsConstructor
@Tag(name = "Participations", description = "API for managing project participations")
public class ParticipationController {

    private final ParticipationService participationService;

    // ✅ CREATE: через DTO
    @PostMapping
    @Operation(
            summary = "Create a new participation",
            description = "Adds a new participation record for a project and returns it with the assigned ID",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Participation successfully created",
                            content = @Content(schema = @Schema(implementation = Participation.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation error",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponse"))
                    )
            }
    )
    public ResponseEntity<Participation> createParticipation(
            @Valid @RequestBody CreateParticipationDto dto,
            UriComponentsBuilder uriBuilder
    ) {
        Participation saved = participationService.create(dto);
        URI uri = uriBuilder.path("/api/participations/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(uri).body(saved);
    }

    @GetMapping
    @Operation(summary = "Get all participations")
    public List<Participation> findAll() {
        return participationService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get participation by ID")
    public ResponseEntity<Participation> findById(@PathVariable Long id) {
        Optional<Participation> result = participationService.findById(id);
        return result.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update participation")
    public ResponseEntity<Participation> update(
            @PathVariable Long id,
            @Valid @RequestBody Participation updated
    ) {
        Participation saved = participationService.update(id, updated);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete participation")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        participationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
