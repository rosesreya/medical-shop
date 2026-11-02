
package com.sreyarose.medicalshop.application.usecase;

import org.springframework.stereotype.Service;

import com.sreyarose.medicalshop.domain.entity.Manufacturer;
import com.sreyarose.medicalshop.domain.repository.ManufacturerRepository;

@Service
public class CreateManufacturerUsecase {

    private final ManufacturerRepository manufacturerRepository;

    public CreateManufacturerUsecase(
            ManufacturerRepository manufacturerRepository) {
        this.manufacturerRepository = manufacturerRepository;
    }

    public Manufacturer execute(
            String name,
            String address,
            String contactNumber,
            String email) {

        String normalizedName = name == null ? null : name.trim();

        if (normalizedName != null &&
                manufacturerRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new IllegalArgumentException(
                    "Manufacturer already exists");
        }

        Manufacturer manufacturer = new Manufacturer(name);
        manufacturer.setAddress(address);
        manufacturer.setContactNumber(contactNumber);
        manufacturer.setEmail(email);

        return manufacturerRepository.save(manufacturer);
    }
}
