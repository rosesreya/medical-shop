
package com.sreyarose.medicalshop.application.usecase;

import org.springframework.stereotype.Service;

import com.sreyarose.medicalshop.application.exception.ResourceNotFoundException;
import com.sreyarose.medicalshop.domain.entity.Category;
import com.sreyarose.medicalshop.domain.repository.CategoryRepository;

@Service
public class ChangeCategoryStatusUsecase {

    private final CategoryRepository categoryRepository;

    public ChangeCategoryStatusUsecase(
            CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void activate(Long id) {
        Category category = findCategory(id);

        category.activate();

        categoryRepository.save(category);
    }

    public void deactivate(Long id) {
        Category category = findCategory(id);

        category.deactivate();

        categoryRepository.save(category);
    }

    private Category findCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found"));
    }
}
