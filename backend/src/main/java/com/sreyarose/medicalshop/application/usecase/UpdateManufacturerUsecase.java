
package com.sreyarose.medicalshop.application.usecase;

import org.springframework.stereotype.Service;

import tools.jackson.databind.JsonNode;
import com.sreyarose.medicalshop.application.exception.ResourceNotFoundException;
import com.sreyarose.medicalshop.domain.entity.Manufacturer;
import com.sreyarose.medicalshop.domain.repository.ManufacturerRepository;

@Service
public class UpdateManufacturerUsecase {

    private final ManufacturerRepository manufacturerRepository;

    public UpdateManufacturerUsecase(
            ManufacturerRepository manufacturerRepository) {
        this.manufacturerRepository = manufacturerRepository;
    }

    public Manufacturer execute(Long id, JsonNode request) {

        Manufacturer manufacturer = manufacturerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Manufacturer not found"));

        if (request == null || !request.isObject()) {
            throw new IllegalArgumentException(
                    "Invalid manufacturer update request");
        }

        if (request.has("name")) {
            JsonNode nameNode = request.get("name");

            if (nameNode == null || nameNode.isNull()
                    || !nameNode.isTextual()) {
                throw new IllegalArgumentException(
                        "Manufacturer name cannot be empty");
            }

            String newName = nameNode.asText().trim();

            if (manufacturerRepository.existsByNameIgnoreCase(newName)
                    && !newName.equalsIgnoreCase(manufacturer.getName())) {
                throw new IllegalArgumentException(
                        "Manufacturer already exists");
            }

            manufacturer.setName(newName);
        }

        if (request.has("address")) {
            manufacturer.setAddress(
                    getOptionalText(request.get("address"), "address"));
        }

        if (request.has("contactNumber")) {
            manufacturer.setContactNumber(
                    getOptionalText(
                            request.get("contactNumber"), "contactNumber"));
        }

        if (request.has("email")) {
            manufacturer.setEmail(
                    getOptionalText(request.get("email"), "email"));
        }

        return manufacturerRepository.save(manufacturer);
    }

    private String getOptionalText(JsonNode node, String fieldName) {
        if (node == null || node.isNull()) {
            return null;
        }

        if (!node.isTextual()) {
            throw new IllegalArgumentException(
                    fieldName + " must be a string or null");
        }

        String value = node.asText();

        return value.isBlank() ? null : value.trim();
    }
}
