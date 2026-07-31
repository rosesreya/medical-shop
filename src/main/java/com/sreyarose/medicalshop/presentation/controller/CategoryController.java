package com.sreyarose.medicalshop.presentation.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sreyarose.medicalshop.application.usecase.CreateCategoryUsecase;
import com.sreyarose.medicalshop.presentation.dto.CategoryResponse;
import com.sreyarose.medicalshop.presentation.dto.CreateCategoryRequest;
import com.sreyarose.medicalshop.domain.entity.Category;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CreateCategoryUsecase createCategoryUsecase;
    public CategoryController(CreateCategoryUsecase createCategoryUsecase){
        this.createCategoryUsecase = createCategoryUsecase;
    }

    @PostMapping
    public CategoryResponse createCategory(@RequestBody CreateCategoryRequest request){
        Category category = createCategoryUsecase.execute(request.getName());
        return new CategoryResponse(category.getId(), category.getName());
    }
}
