package org.example.trendyolfinalproject.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.request.BasketCreateRequest;
import org.example.trendyolfinalproject.request.BasketElementRequest;
import org.example.trendyolfinalproject.request.DeleteBasketElementRequest;
import org.example.trendyolfinalproject.response.BasketElementResponse;
import org.example.trendyolfinalproject.response.BasketResponse;
import org.example.trendyolfinalproject.service.BasketElementService;
import org.example.trendyolfinalproject.service.BasketService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/v1/basket")
@RequiredArgsConstructor
public class BasketController {
    private final BasketService basketService;
    private final BasketElementService basketElementService;



    @GetMapping("/getBasketPrice")
    @PreAuthorize("hasRole('CUSTOMER')")
    public BigDecimal getBasketPrice()
    {
        return basketService.getTotalAmount();
    }
//    @PostMapping("/createBasket")
//    public BasketResponse createBasket(@RequestBody BasketCreateRequest request) {
//        return basketService.getOrcreateBasket(request);
//    }

    @GetMapping("/getRowTotalAmount")
    @PreAuthorize("hasRole('CUSTOMER')")
    public BigDecimal getRowTotalAmount() {
        return basketService.calculateRawTotalAmount();
    }


    @PostMapping("/element/createBasketElement")
//    @PreAuthorize("hasRole('CUSTOMER')")
    BasketElementResponse createBasketElement(@RequestBody BasketElementRequest request) {
        return basketElementService.createBasketElement(request);
    }

    @DeleteMapping("/element/deleteBasketElement")
    @PreAuthorize("hasRole('CUSTOMER')")
    public void deleteBasketElement(@RequestBody DeleteBasketElementRequest request) {
        basketElementService.deleteBasketElement(request);
    }

    @PostMapping("/element/decreaseQuantity")
    @PreAuthorize("hasRole('CUSTOMER')")
    public BasketElementResponse decrieceQuantity(@RequestBody DeleteBasketElementRequest request) {
        return basketElementService.decrieceQuantity(request);
    }

    @GetMapping("/element/getBasketElements")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<BasketElementResponse> getBasketElements() {
        return basketElementService.getBasketElements();
    }
}
