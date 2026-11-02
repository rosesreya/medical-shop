package com.sreyarose.medicalshop.application.usecase;

import com.sreyarose.medicalshop.domain.entity.Batch;
import com.sreyarose.medicalshop.domain.repository.BatchRepository;
import com.sreyarose.medicalshop.domain.repository.ProductRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class CreateBatchUsecase {

    private final BatchRepository batchRepository;
    private final ProductRepository productRepository;

    public CreateBatchUsecase(
            BatchRepository batchRepository,
            ProductRepository productRepository) {

        this.batchRepository = batchRepository;
        this.productRepository = productRepository;
    }

    public Batch execute(
            Long productId,
            String batchNumber,
            LocalDate manufacturingDate,
            LocalDate expiryDate,
            int openingQuantity,
            BigDecimal mrp) {

        if (productRepository.findById(productId).isEmpty()) {
            throw new IllegalArgumentException("Product does not exist");
        }

        if (batchRepository
        .findByProductIdAndBatchNumberForUpdate(productId, batchNumber)
        .isPresent()) {
    throw new IllegalArgumentException(
            "Batch already exists for this product"
    );
}

        Batch batch = new Batch(
                null,
                productId,
                batchNumber,
                manufacturingDate,
                expiryDate,
                openingQuantity,
                mrp
        );

        return batchRepository.save(batch);
    }
}