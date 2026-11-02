
package com.sreyarose.medicalshop.application.usecase;

import org.springframework.stereotype.Service;

import com.sreyarose.medicalshop.application.exception.ResourceNotFoundException;
import com.sreyarose.medicalshop.domain.entity.Manufacturer;
import com.sreyarose.medicalshop.domain.repository.ManufacturerRepository;

@Service
public class ChangeManufacturerStatusUsecase {

    private final ManufacturerRepository manufacturerRepository;

    public ChangeManufacturerStatusUsecase(
            ManufacturerRepository manufacturerRepository) {
        this.manufacturerRepository = manufacturerRepository;
    }

    public void activate(Long id) {
        Manufacturer manufacturer = findManufacturer(id);
        manufacturer.activate();
        manufacturerRepository.save(manufacturer);
    }

    public void deactivate(Long id) {
        Manufacturer manufacturer = findManufacturer(id);
        manufacturer.deactivate();
        manufacturerRepository.save(manufacturer);
    }

    private Manufacturer findManufacturer(Long id) {
        return manufacturerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Manufacturer not found"));
    }
}
