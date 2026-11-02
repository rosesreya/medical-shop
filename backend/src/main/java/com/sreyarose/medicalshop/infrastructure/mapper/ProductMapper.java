
package com.sreyarose.medicalshop.infrastructure.mapper;

import com.sreyarose.medicalshop.domain.entity.Product;
import com.sreyarose.medicalshop.infrastructure.persistence.ProductJpaEntity;

public class ProductMapper {

    private ProductMapper() {
    }

    public static Product toDomain(ProductJpaEntity entity) {
        return new Product(
                entity.getId(),
                entity.getName(),
                entity.getBrandName(),
                entity.getCategoryId(),
                entity.getManufacturerId(),
                entity.getGenericNameId(),
                entity.getStrength(),
                entity.getDosageForm(),
                entity.getDescription(),
                entity.isActive(),
                entity.getBaseUnit(),
                entity.getPackUnit(),
                entity.getUnitsPerPack(),
                entity.getCgstRate(),
                entity.getSgstRate()
        );
    }

    public static ProductJpaEntity toJpaEntity(Product product) {
        ProductJpaEntity entity = new ProductJpaEntity(
                product.getName(),
                product.getBrandName(),
                product.getCategoryId(),
                product.getManufacturerId(),
                product.getGenericNameId(),
                product.getStrength(),
                product.getDosageForm(),
                product.getDescription(),
                product.isActive()
        );

        entity.setBaseUnit(product.getBaseUnit());
        entity.setPackUnit(product.getPackUnit());
        entity.setUnitsPerPack(product.getUnitsPerPack());
        entity.setCgstRate(product.getCgstRate());
        entity.setSgstRate(product.getSgstRate());

        return entity;
    }
}
