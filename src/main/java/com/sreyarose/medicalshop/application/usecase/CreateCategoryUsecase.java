package com.sreyarose.medicalshop.application.usecase;

import com.sreyarose.medicalshop.domain.entity.Category;
import com.sreyarose.medicalshop.domain.repository.CategoryRepository;
import org.springframework.stereotype.Service;
@Service
public class CreateCategoryUsecase {
    private final CategoryRepository categoryRepo;

    public CreateCategoryUsecase(CategoryRepository categoryRepo){
        this.categoryRepo = categoryRepo;

    }
    public Category execute(String name){
        Category category = new Category(name);
        return categoryRepo.save(category);
    }
}
