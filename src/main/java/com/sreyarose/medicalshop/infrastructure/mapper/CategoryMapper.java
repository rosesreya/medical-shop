package com.sreyarose.medicalshop.infrastructure.mapper;

import com.sreyarose.medicalshop.domain.entity.Category;
import com.sreyarose.medicalshop.infrastructure.persistence.CategoryJpaEntity;

public class CategoryMapper {
    // domain to jpa entity
    public static CategoryJpaEntity toJpaCategory(Category category){
        return new CategoryJpaEntity(category.getId(),category.getName());
    }
    // jpa entity to domain
    public static Category toDomainCategory(CategoryJpaEntity jpaentity){
        return new Category(jpaentity.getId(), jpaentity.getName());
    }
}
 