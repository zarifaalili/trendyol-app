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
    public ApiResponse<BigDecimal> getBasketPrice() {

        return basketService.getTotalAmount();
    }


    @GetMapping("/raw-price")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<BigDecimal> getRowTotalAmount() {

        return basketService.calculateRawTotalAmount();
    }


    @PostMapping("/elements")
    @PreAuthorize("hasRole('CUSTOMER')")
    ApiResponse<BasketElementResponse> createBasketElement(@RequestBody @Valid BasketElementRequest request) {
        return basketElementService.createBasketElement(request);
    }

    @DeleteMapping("/elements")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<String> deleteBasketElement(@RequestBody @Valid DeleteBasketElementRequest request) {
        return basketElementService.deleteBasketElement(request);
    }

    @PostMapping("/elements/decrease")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<BasketElementResponse> decrieceQuantity(@RequestBody @Valid DeleteBasketElementRequest request) {
        return basketElementService.decrieceQuantity(request);
    }

    @GetMapping("/elements")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<List<BasketElementResponse>> getBasketElements() {
        return basketElementService.getBasketElements();
    }

    @PostMapping("/actions/notify-abandoned")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> notifyAbandonedBasket() {
        int notifiedCount = basketService.notifyAbandonedBaskets();
        return ResponseEntity.ok(notifiedCount + " users notified about abandoned baskets");
    }

    @GetMapping("/summary")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<BasketSummaryResponse> getBasketSummary() {
        return basketService.getBasketSummary();
    }
}
