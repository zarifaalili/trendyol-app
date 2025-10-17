package org.example.trendyolfinalproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.model.request.AdressCreateRequest;
import org.example.trendyolfinalproject.model.response.AdressResponse;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.service.AdressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/addresses")
@RequiredArgsConstructor
public class AdressController {
    private final AdressService adressService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<AdressResponse>> createAdress(@RequestBody @Valid AdressCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adressService.createAdress(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<Void>> deleteAdress(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(adressService.deleteAdress(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<java.util.List<AdressResponse>>> getAdresses() {
        return ResponseEntity.status(HttpStatus.OK).body(adressService.getAdresses());
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<AdressResponse>> updateAdress(@PathVariable Long id, @RequestBody @Valid AdressCreateRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(adressService.updateAdress(id, request));
    }


}
