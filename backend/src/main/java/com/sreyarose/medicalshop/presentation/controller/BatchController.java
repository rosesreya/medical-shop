package com.sreyarose.medicalshop.presentation.controller;

import com.sreyarose.medicalshop.application.usecase.GetBatchesByProductUsecase;
import com.sreyarose.medicalshop.domain.entity.Batch;
import com.sreyarose.medicalshop.domain.repository.BatchRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/batches")
public class BatchController {

    private final GetBatchesByProductUsecase getBatchesByProductUsecase;
    private final BatchRepository batchRepository;
    public BatchController(
            GetBatchesByProductUsecase getBatchesByProductUsecase, BatchRepository batchRepository) {
        this.getBatchesByProductUsecase = getBatchesByProductUsecase;
        this.batchRepository = batchRepository;
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Batch>> getBatchesByProduct(
            @PathVariable Long productId) {

        List<Batch> batches =
                getBatchesByProductUsecase.execute(productId);

        return ResponseEntity.ok(batches);
    }
    @GetMapping
    public ResponseEntity<List<Batch>> getAllBatches() {
        return ResponseEntity.ok(batchRepository.findAll());
    }
}