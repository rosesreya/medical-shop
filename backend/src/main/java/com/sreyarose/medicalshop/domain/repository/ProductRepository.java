
package com.sreyarose.medicalshop.domain.repository;

import com.sreyarose.medicalshop.domain.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(Long id);

    List<Product> findAll();

    boolean existsByNameIgnoreCase(String name);
}
