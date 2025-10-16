package org.example.trendyolfinalproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.model.request.AdressCreateRequest;
import org.example.trendyolfinalproject.model.response.AdressResponse;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.service.AdressService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/addresses")
@RequiredArgsConstructor
public class AdressController {
    private final AdressService adressService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    ApiResponse<AdressResponse> createAdress(@RequestBody @Valid AdressCreateRequest request) {
        return adressService.createAdress(request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<String> deleteAdress(@PathVariable Long id) {
        return adressService.deleteAdress(id);
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<java.util.List<AdressResponse>> getAdresses() {
        return adressService.getAdresses();
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<AdressResponse> updateAdress(@PathVariable Long id, @RequestBody @Valid AdressCreateRequest request) {
        return adressService.updateAdress(id, request);
    }


}
