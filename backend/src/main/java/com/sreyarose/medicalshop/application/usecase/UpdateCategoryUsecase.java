package com.sreyarose.medicalshop.application.usecase;

import org.springframework.stereotype.Service;

import com.sreyarose.medicalshop.application.exception.ResourceNotFoundException;
import com.sreyarose.medicalshop.domain.entity.Category;
import com.sreyarose.medicalshop.domain.repository.CategoryRepository;

@Service
public class UpdateCategoryUsecase {

    private final CategoryRepository categoryRepository;

    public UpdateCategoryUsecase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category execute(Long id, String newName) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found"));

        category.setName(newName);

        return categoryRepository.save(category);
    }
}

