package com.sreyarose.medicalshop.application.usecase;

import org.springframework.stereotype.Service;

import com.sreyarose.medicalshop.domain.entity.Category;
import com.sreyarose.medicalshop.domain.repository.CategoryRepository;

@Service
public class CreateCategoryUsecase {

    private final CategoryRepository categoryRepo;

    public CreateCategoryUsecase(CategoryRepository categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    public Category execute(String name) {

        Category category = new Category(name);

        if (categoryRepo.existsByNameIgnoreCase(category.getName())) {
            throw new IllegalArgumentException(
                "Category already exists"
            );
        }

        return categoryRepo.save(category);
    }
}