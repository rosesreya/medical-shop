package com.sreyarose.medicalshop.domain.repository;

import java.util.List;
import java.util.Optional;

import com.sreyarose.medicalshop.domain.entity.Category;

public interface CategoryRepository {
    public Category save(Category category);
    public Optional<Category> findById(Long id);
    public List<Category> findAll();
    public boolean existsByNameIgnoreCase(String name);
}
