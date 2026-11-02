
package com.sreyarose.medicalshop.presentation.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sreyarose.medicalshop.application.usecase.ChangeGenericNameStatusUsecase;
import com.sreyarose.medicalshop.application.usecase.CreateGenericNameUsecase;
import com.sreyarose.medicalshop.application.usecase.UpdateGenericNameUsecase;
import com.sreyarose.medicalshop.domain.entity.GenericName;
import com.sreyarose.medicalshop.domain.repository.GenericNameRepository;
import com.sreyarose.medicalshop.presentation.dto.GenericNameResponse;

import tools.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api/generic-names")
public class GenericNameController {

    private final CreateGenericNameUsecase createGenericNameUsecase;
    private final UpdateGenericNameUsecase updateGenericNameUsecase;
    private final ChangeGenericNameStatusUsecase changeGenericNameStatusUsecase;
    private final GenericNameRepository genericNameRepository;

    public GenericNameController(
            CreateGenericNameUsecase createGenericNameUsecase,
            UpdateGenericNameUsecase updateGenericNameUsecase,
            ChangeGenericNameStatusUsecase changeGenericNameStatusUsecase,
            GenericNameRepository genericNameRepository) {

        this.createGenericNameUsecase = createGenericNameUsecase;
        this.updateGenericNameUsecase = updateGenericNameUsecase;
        this.changeGenericNameStatusUsecase = changeGenericNameStatusUsecase;
        this.genericNameRepository = genericNameRepository;
    }

    @PostMapping
    public ResponseEntity<GenericNameResponse> create(
            @RequestBody JsonNode request) {

        String name = getRequiredText(request, "name");

        GenericName genericName =
                createGenericNameUsecase.execute(name);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(genericName));
    }

    @GetMapping
    public List<GenericNameResponse> getAll() {

        return genericNameRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<GenericNameResponse> update(
            @PathVariable Long id,
            @RequestBody JsonNode request) {

        String name = getRequiredText(request, "name");

        GenericName genericName =
                updateGenericNameUsecase.execute(id, name);

        return ResponseEntity.ok(toResponse(genericName));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {

        changeGenericNameStatusUsecase.activate(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {

        changeGenericNameStatusUsecase.deactivate(id);

        return ResponseEntity.noContent().build();
    }

    private GenericNameResponse toResponse(GenericName genericName) {

        return new GenericNameResponse(
                genericName.getId(),
                genericName.getName(),
                genericName.isActive());
    }

    private String getRequiredText(JsonNode request, String field) {

        if (request == null
                || !request.isObject()
                || !request.has(field)
                || !request.get(field).isTextual()) {

            throw new IllegalArgumentException(
                    "Generic name cannot be empty");
        }

        return request.get(field).asText();
    }
}