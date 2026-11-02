package com.sreyarose.medicalshop.infrastructure.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.sreyarose.medicalshop.domain.entity.Category;
import com.sreyarose.medicalshop.domain.repository.CategoryRepository;
import com.sreyarose.medicalshop.infrastructure.mapper.CategoryMapper;
import com.sreyarose.medicalshop.infrastructure.persistence.CategoryJpaEntity;
import com.sreyarose.medicalshop.infrastructure.persistence.CategoryJpaRepository;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryJpaRepository jpaRepository;

    public CategoryRepositoryImpl(CategoryJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Category save(Category category) {
        CategoryJpaEntity categoryJpaEntity =
                CategoryMapper.toJpaCategory(category);

        CategoryJpaEntity savedEntity =
                jpaRepository.save(categoryJpaEntity);

        return CategoryMapper.toDomainCategory(savedEntity);
    }

    @Override
    public Optional<Category> findById(Long id) {
        return jpaRepository.findById(id)
                .map(CategoryMapper::toDomainCategory);
    }

    @Override
    public List<Category> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(CategoryMapper::toDomainCategory)
                .toList();
    }

    @Override
    public boolean existsByNameIgnoreCase(String name) {
        return jpaRepository.existsByNameIgnoreCase(name);
    }
}