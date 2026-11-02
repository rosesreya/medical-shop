
package com.sreyarose.medicalshop.application.usecase;

import com.sreyarose.medicalshop.domain.entity.GenericName;
import com.sreyarose.medicalshop.domain.repository.GenericNameRepository;
import com.sreyarose.medicalshop.application.exception.ResourceNotFoundException;

import org.springframework.stereotype.Service;

@Service
public class ChangeGenericNameStatusUsecase {

    private final GenericNameRepository genericNameRepository;

    public ChangeGenericNameStatusUsecase(
            GenericNameRepository genericNameRepository) {
        this.genericNameRepository = genericNameRepository;
    }

    public void activate(Long id) {
        GenericName genericName = findGenericName(id);
        genericName.activate();
        genericNameRepository.save(genericName);
    }

    public void deactivate(Long id) {
        GenericName genericName = findGenericName(id);
        genericName.deactivate();
        genericNameRepository.save(genericName);
    }

    private GenericName findGenericName(Long id) {
        return genericNameRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Generic name not found"));
    }
}
