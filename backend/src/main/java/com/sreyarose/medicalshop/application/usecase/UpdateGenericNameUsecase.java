
package com.sreyarose.medicalshop.application.usecase;

import com.sreyarose.medicalshop.domain.entity.GenericName;
import com.sreyarose.medicalshop.domain.repository.GenericNameRepository;
import com.sreyarose.medicalshop.application.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UpdateGenericNameUsecase {

    private final GenericNameRepository genericNameRepository;

    public UpdateGenericNameUsecase(
            GenericNameRepository genericNameRepository) {
        this.genericNameRepository = genericNameRepository;
    }

    public GenericName execute(Long id, String name) {

        GenericName genericName = genericNameRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Generic name not found"));

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Generic name cannot be empty");
        }

        name = name.trim();

        if (!genericName.getName().equalsIgnoreCase(name)
                && genericNameRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException(
                    "Generic name already exists");
        }

        genericName.setName(name);

        return genericNameRepository.save(genericName);
    }
}
