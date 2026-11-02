
package com.sreyarose.medicalshop.application.usecase;

import com.sreyarose.medicalshop.domain.entity.GenericName;
import com.sreyarose.medicalshop.domain.repository.GenericNameRepository;

import org.springframework.stereotype.Service;

@Service
public class CreateGenericNameUsecase {

    private final GenericNameRepository genericNameRepository;

    public CreateGenericNameUsecase(
            GenericNameRepository genericNameRepository) {
        this.genericNameRepository = genericNameRepository;
    }

    public GenericName execute(String name) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Generic name cannot be empty");
        }

        name = name.trim();

        if (genericNameRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException(
                    "Generic name already exists");
        }

        GenericName genericName = new GenericName(name);

        return genericNameRepository.save(genericName);
    }
}
