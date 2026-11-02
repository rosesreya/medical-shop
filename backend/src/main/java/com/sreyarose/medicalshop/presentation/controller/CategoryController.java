
package com.sreyarose.medicalshop.presentation.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sreyarose.medicalshop.application.usecase.CreateCategoryUsecase;
import com.sreyarose.medicalshop.application.usecase.UpdateCategoryUsecase;
import com.sreyarose.medicalshop.application.usecase.ChangeCategoryStatusUsecase;
import com.sreyarose.medicalshop.domain.entity.Category;
import com.sreyarose.medicalshop.domain.repository.CategoryRepository;
import com.sreyarose.medicalshop.presentation.dto.CategoryResponse;
import com.sreyarose.medicalshop.presentation.dto.CreateCategoryRequest;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CreateCategoryUsecase createCategoryUsecase;
    private final UpdateCategoryUsecase updateCategoryUsecase;
    private final CategoryRepository categoryRepository;
    private final ChangeCategoryStatusUsecase changeCategoryStatusUsecase;

    public CategoryController(
            CreateCategoryUsecase createCategoryUsecase,
            UpdateCategoryUsecase updateCategoryUsecase,
            CategoryRepository categoryRepository,
            ChangeCategoryStatusUsecase changeCategoryStatusUsecase) {

        this.createCategoryUsecase = createCategoryUsecase;
        this.updateCategoryUsecase = updateCategoryUsecase;
        this.categoryRepository = categoryRepository;
        this.changeCategoryStatusUsecase = changeCategoryStatusUsecase;
    }

    @PostMapping
    public CategoryResponse createCategory(
            @RequestBody CreateCategoryRequest request) {

        Category category =
                createCategoryUsecase.execute(request.getName());

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.isActive()
        );
    }

    @GetMapping
    public List<CategoryResponse> getCategories() {

        return categoryRepository.findAll()
                .stream()
                .map(category -> new CategoryResponse(
                        category.getId(),
                        category.getName(),
                        category.isActive()
                ))
                .toList();
    }

    @PutMapping("/{id}")
    public CategoryResponse updateCategory(
            @PathVariable Long id,
            @RequestBody CreateCategoryRequest request) {

        Category category =
                updateCategoryUsecase.execute(id, request.getName());

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.isActive()
        );
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateCategory(
            @PathVariable Long id) {

        changeCategoryStatusUsecase.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateCategory(
            @PathVariable Long id) {

        changeCategoryStatusUsecase.activate(id);
        return ResponseEntity.noContent().build();
    }
}
