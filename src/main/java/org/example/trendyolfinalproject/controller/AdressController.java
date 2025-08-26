package org.example.trendyolfinalproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.request.AdressCreateRequest;
import org.example.trendyolfinalproject.response.AdressResponse;
import org.example.trendyolfinalproject.response.ApiResponse;
import org.example.trendyolfinalproject.service.AdressService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/adress")
@RequiredArgsConstructor
public class AdressController {
    private final AdressService adressService;

    @PostMapping("/createAdress")
    ApiResponse<AdressResponse> createAdress(@RequestBody @Valid AdressCreateRequest request) {
        return adressService.createAdress(request);
    }

    @DeleteMapping("/deleteAdress/{id}")
    public ApiResponse<String> deleteAdress(@PathVariable Long id) {
        return adressService.deleteAdress(id);
    }

    @GetMapping("/getAdresses")
    public ApiResponse<java.util.List<AdressResponse>> getAdresses() {
        return adressService.getAdresses();
    }

    @PatchMapping("/updateAdress/{id}")
    public ApiResponse<AdressResponse> updateAdress(@PathVariable Long id, @RequestBody @Valid AdressCreateRequest request) {
        return adressService.updateAdress(id, request);
    }


}
