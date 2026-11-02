
package com.sreyarose.medicalshop.application.usecase;

import org.springframework.stereotype.Service;

import com.sreyarose.medicalshop.application.exception.ResourceNotFoundException;
import com.sreyarose.medicalshop.domain.entity.Product;
import com.sreyarose.medicalshop.domain.repository.ProductRepository;

@Service
public class ChangeProductStatusUsecase {

    private final ProductRepository productRepository;

    public ChangeProductStatusUsecase(
            ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void activate(Long id) {
        Product product = findProduct(id);
        product.activate();
        productRepository.save(product);
    }

    public void deactivate(Long id) {
        Product product = findProduct(id);
        product.deactivate();
        productRepository.save(product);
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found"));
    }
}
