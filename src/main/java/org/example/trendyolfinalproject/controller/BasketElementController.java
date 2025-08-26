//package org.example.trendyolfinalproject.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.example.trendyolfinalproject.request.BasketElementRequest;
//import org.example.trendyolfinalproject.request.DeleteBasketElementRequest;
//import org.example.trendyolfinalproject.response.BasketElementResponse;
//import org.example.trendyolfinalproject.service.BasketElementService;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/v1/basketElement")
//@RequiredArgsConstructor
//public class BasketElementController {
//    private final BasketElementService basketElementService;
//
//    @PostMapping("/createBasketElement")
////    @PreAuthorize("hasRole('CUSTOMER')")
//    BasketElementResponse createBasketElement(@RequestBody BasketElementRequest request) {
//        return basketElementService.createBasketElement(request);
//    }
//
//    @DeleteMapping("/deleteBasketElement")
//    @PreAuthorize("hasRole('CUSTOMER')")
//    public void deleteBasketElement(@RequestBody DeleteBasketElementRequest request) {
//        basketElementService.deleteBasketElement(request);
//    }
//
//    @PostMapping("/decreaseQuantity")
//    @PreAuthorize("hasRole('CUSTOMER')")
//    public BasketElementResponse decrieceQuantity(@RequestBody DeleteBasketElementRequest request) {
//        return basketElementService.decrieceQuantity(request);
//    }
//
//    @GetMapping("/getBasketElements")
//    @PreAuthorize("hasRole('CUSTOMER')")
//    public List<BasketElementResponse> getBasketElements() {
//        return basketElementService.getBasketElements();
//    }
//
//}
