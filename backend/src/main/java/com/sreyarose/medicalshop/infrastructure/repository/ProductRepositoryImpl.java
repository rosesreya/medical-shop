
package com.sreyarose.medicalshop.infrastructure.repository;

import com.sreyarose.medicalshop.domain.entity.Product;
import com.sreyarose.medicalshop.domain.repository.ProductRepository;
import com.sreyarose.medicalshop.infrastructure.mapper.ProductMapper;
import com.sreyarose.medicalshop.infrastructure.persistence.ProductJpaEntity;
import com.sreyarose.medicalshop.infrastructure.persistence.ProductJpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository jpaRepository;

    public ProductRepositoryImpl(ProductJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Product save(Product product) {

        ProductJpaEntity entity;

        if (product.getId() == null) {
            entity = ProductMapper.toJpaEntity(product);
        } else {
            entity = jpaRepository.findById(product.getId())
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "Product not found during update"));

            entity.setName(product.getName());
            entity.setBrandName(product.getBrandName());
            entity.setCategoryId(product.getCategoryId());
            entity.setManufacturerId(product.getManufacturerId());
            entity.setGenericNameId(product.getGenericNameId());
            entity.setStrength(product.getStrength());
            entity.setDosageForm(product.getDosageForm());
            entity.setDescription(product.getDescription());
            entity.setActive(product.isActive());
            entity.setBaseUnit(product.getBaseUnit());
            entity.setPackUnit(product.getPackUnit());
            entity.setUnitsPerPack(product.getUnitsPerPack());
            entity.setCgstRate(product.getCgstRate());
            entity.setSgstRate(product.getSgstRate());
        }

        ProductJpaEntity savedEntity = jpaRepository.save(entity);

        return ProductMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return jpaRepository.findById(id)
                .map(ProductMapper::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(ProductMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByNameIgnoreCase(String name) {
        return jpaRepository.existsByNameIgnoreCase(name);
    }
}
