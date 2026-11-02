
package com.sreyarose.medicalshop.presentation.controller;

import java.util.List;

import tools.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sreyarose.medicalshop.application.usecase.ChangeManufacturerStatusUsecase;
import com.sreyarose.medicalshop.application.usecase.CreateManufacturerUsecase;
import com.sreyarose.medicalshop.application.usecase.UpdateManufacturerUsecase;
import com.sreyarose.medicalshop.domain.entity.Manufacturer;
import com.sreyarose.medicalshop.domain.repository.ManufacturerRepository;
import com.sreyarose.medicalshop.presentation.dto.ManufacturerResponse;

@RestController
@RequestMapping("/api/manufacturers")
public class ManufacturerController {

    private final CreateManufacturerUsecase createManufacturerUsecase;
    private final UpdateManufacturerUsecase updateManufacturerUsecase;
    private final ChangeManufacturerStatusUsecase changeManufacturerStatusUsecase;
    private final ManufacturerRepository manufacturerRepository;

    public ManufacturerController(
            CreateManufacturerUsecase createManufacturerUsecase,
            UpdateManufacturerUsecase updateManufacturerUsecase,
            ChangeManufacturerStatusUsecase changeManufacturerStatusUsecase,
            ManufacturerRepository manufacturerRepository) {

        this.createManufacturerUsecase = createManufacturerUsecase;
        this.updateManufacturerUsecase = updateManufacturerUsecase;
        this.changeManufacturerStatusUsecase = changeManufacturerStatusUsecase;
        this.manufacturerRepository = manufacturerRepository;
    }

    @PostMapping
    public ResponseEntity<ManufacturerResponse> create(
            @RequestBody JsonNode request) {

        String name = getRequiredText(request, "name");
        String address = getOptionalText(request, "address");
        String contactNumber = getOptionalText(request, "contactNumber");
        String email = getOptionalText(request, "email");

        Manufacturer manufacturer = createManufacturerUsecase.execute(
                name, address, contactNumber, email);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(manufacturer));
    }

    @GetMapping
    public List<ManufacturerResponse> getAll() {
        return manufacturerRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ManufacturerResponse> update(
            @PathVariable Long id,
            @RequestBody JsonNode request) {

        Manufacturer manufacturer =
                updateManufacturerUsecase.execute(id, request);

        return ResponseEntity.ok(toResponse(manufacturer));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        changeManufacturerStatusUsecase.activate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        changeManufacturerStatusUsecase.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    private ManufacturerResponse toResponse(Manufacturer manufacturer) {
        return new ManufacturerResponse(
                manufacturer.getId(),
                manufacturer.getName(),
                manufacturer.isActive(),
                manufacturer.getAddress(),
                manufacturer.getContactNumber(),
                manufacturer.getEmail());
    }

    private String getRequiredText(JsonNode request, String field) {
        if (request == null || !request.isObject()
                || !request.has(field)
                || !request.get(field).isTextual()) {
            throw new IllegalArgumentException(
                    "Manufacturer name cannot be empty");
        }

        return request.get(field).asText();
    }

    private String getOptionalText(JsonNode request, String field) {
        if (!request.has(field) || request.get(field).isNull()) {
            return null;
        }

        if (!request.get(field).isTextual()) {
            throw new IllegalArgumentException(
                    field + " must be a string or null");
        }

        return request.get(field).asText();
    }
}
