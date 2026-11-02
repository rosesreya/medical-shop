package com.sreyarose.medicalshop.application.usecase;

import com.sreyarose.medicalshop.domain.entity.Batch;
import com.sreyarose.medicalshop.domain.repository.BatchRepository;
import com.sreyarose.medicalshop.domain.repository.ProductRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetBatchesByProductUsecase {

    private final BatchRepository batchRepository;
    private final ProductRepository productRepository;

    public GetBatchesByProductUsecase(
            BatchRepository batchRepository,
            ProductRepository productRepository) {

        this.batchRepository = batchRepository;
        this.productRepository = productRepository;
    }

    public List<Batch> execute(Long productId) {

        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("Invalid product ID");
        }

        if (productRepository.findById(productId).isEmpty()) {
            throw new IllegalArgumentException("Product does not exist");
        }

        return batchRepository.findByProductId(productId);
    }
}