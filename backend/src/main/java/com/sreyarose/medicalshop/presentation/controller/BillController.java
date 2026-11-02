
package com.sreyarose.medicalshop.presentation.controller;

import com.sreyarose.medicalshop.application.exception.ResourceNotFoundException;
import com.sreyarose.medicalshop.application.usecase.CreateBillUsecase;
import com.sreyarose.medicalshop.domain.entity.Batch;
import com.sreyarose.medicalshop.domain.entity.Bill;
import com.sreyarose.medicalshop.domain.entity.BillItem;
import com.sreyarose.medicalshop.domain.entity.Product;
import com.sreyarose.medicalshop.domain.repository.BatchRepository;
import com.sreyarose.medicalshop.domain.repository.BillItemRepository;
import com.sreyarose.medicalshop.domain.repository.BillRepository;
import com.sreyarose.medicalshop.domain.repository.ProductRepository;
import com.sreyarose.medicalshop.presentation.dto.BillItemResponse;
import com.sreyarose.medicalshop.presentation.dto.BillRequest;
import com.sreyarose.medicalshop.presentation.dto.BillResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    private final CreateBillUsecase createBillUsecase;
    private final BillRepository billRepository;
    private final BillItemRepository billItemRepository;
    private final ProductRepository productRepository;
    private final BatchRepository batchRepository;

    public BillController(
            CreateBillUsecase createBillUsecase,
            BillRepository billRepository,
            BillItemRepository billItemRepository,
            ProductRepository productRepository,
            BatchRepository batchRepository
    ) {
        this.createBillUsecase = createBillUsecase;
        this.billRepository = billRepository;
        this.billItemRepository = billItemRepository;
        this.productRepository = productRepository;
        this.batchRepository = batchRepository;
    }

    @PostMapping
    public ResponseEntity<BillResponse> createBill(
            @RequestBody BillRequest request
    ) {
        Bill bill = createBillUsecase.execute(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(bill));
    }

    @GetMapping
    public ResponseEntity<List<BillResponse>> getAllBills() {
        List<BillResponse> responses = billRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillResponse> getBillById(
            @PathVariable Long id
    ) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bill not found with ID: " + id
                ));

        return ResponseEntity.ok(toResponse(bill));
    }

    private BillResponse toResponse(Bill bill) {
        List<BillItemResponse> items =
                billItemRepository.findByBillId(bill.getId())
                        .stream()
                        .map(this::toItemResponse)
                        .toList();

        return new BillResponse(
                bill.getId(),
                bill.getBillNumber(),
                bill.getBillDate(),
                bill.getCustomerName(),
                bill.getCustomerMobile(),
                bill.getSubtotal(),
                bill.getDiscountAmount(),
                bill.getGrandTotal(),

                // Bill-level GST totals
                bill.getTotalTaxableAmount(),
                bill.getTotalCgstAmount(),
                bill.getTotalSgstAmount(),

                bill.getPaymentMethod(),
                bill.getPaymentStatus(),
                bill.getAmountReceived(),
                bill.getChangeAmount(),
                items
        );
    }

    private BillItemResponse toItemResponse(BillItem item) {
        String productName = productRepository
                .findById(item.getProductId())
                .map(Product::getName)
                .orElse("Unknown product");

        Batch batch = batchRepository.findById(item.getBatchId())
                .orElse(null);

        String batchNumber = batch == null
                ? "Unknown batch"
                : batch.getBatchNumber();

        return new BillItemResponse(
                item.getId(),
                item.getProductId(),
                productName,
                item.getBatchId(),
                batchNumber,
                item.getQuantity(),
                item.getSellingRate(),
                item.getDiscountAmount(),
                item.getTaxableAmount(),
                item.getCgstRate(),
                item.getCgstAmount(),
                item.getSgstRate(),
                item.getSgstAmount(),
                item.getLineTotal()
        );
    }
}
