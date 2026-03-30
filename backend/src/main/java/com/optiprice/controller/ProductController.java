package com.optiprice.controller;

import com.optiprice.dto.response.MasterProductResponse;
import com.optiprice.dto.response.PagedResponse;
import com.optiprice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/deals")
    public ResponseEntity<PagedResponse<MasterProductResponse>> getDeals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return ResponseEntity.ok(productService.getArbitrageDeals(page, size));
    }

    @GetMapping("/deals/drops")
    public ResponseEntity<PagedResponse<MasterProductResponse>> getPriceDrops(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        return ResponseEntity.ok(productService.getPriceDropDeals(page, size));
    }
}
