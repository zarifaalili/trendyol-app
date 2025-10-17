package org.example.trendyolfinalproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.model.request.BasketElementRequest;
import org.example.trendyolfinalproject.model.request.DeleteBasketElementRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.BasketElementResponse;
import org.example.trendyolfinalproject.model.response.BasketSummaryResponse;
import org.example.trendyolfinalproject.service.BasketElementService;
import org.example.trendyolfinalproject.service.BasketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/v1/baskets")
@RequiredArgsConstructor
public class BasketController {
    private final BasketService basketService;
    private final BasketElementService basketElementService;


    @GetMapping("/price")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<BigDecimal>> getBasketPrice() {
        return ResponseEntity.ok().body(basketService.getTotalAmount());
    }


    @GetMapping("/raw-price")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<BigDecimal>> getRowTotalAmount() {
        return ResponseEntity.ok().body(basketService.calculateRawTotalAmount());
    }


    @PostMapping("/elements")
    @PreAuthorize("hasRole('CUSTOMER')")
    ResponseEntity<ApiResponse<BasketElementResponse>> createBasketElement(@RequestBody @Valid BasketElementRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(basketElementService.createBasketElement(request));
    }

    @DeleteMapping("/elements")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<Void>> deleteBasketElement(@RequestBody @Valid DeleteBasketElementRequest request) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(basketElementService.deleteBasketElement(request));
    }

    @PostMapping("/elements/decrease")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<BasketElementResponse>> decrieceQuantity(@RequestBody @Valid DeleteBasketElementRequest request) {
        return ResponseEntity.ok().body(basketElementService.decrieceQuantity(request));
    }

    @GetMapping("/elements")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<List<BasketElementResponse>>> getBasketElements() {
        return ResponseEntity.ok().body(basketElementService.getBasketElements());
    }

    @PostMapping("/actions/notify-abandoned")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> notifyAbandonedBasket() {
        int notifiedCount = basketService.notifyAbandonedBaskets();
        return ResponseEntity.ok().body(ApiResponse.success("Notified " + notifiedCount + " baskets"));
    }

    @GetMapping("/summary")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<BasketSummaryResponse>> getBasketSummary() {
        return ResponseEntity.ok().body(basketService.getBasketSummary());
    }
}
