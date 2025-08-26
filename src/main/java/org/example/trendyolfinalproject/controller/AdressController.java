package org.example.trendyolfinalproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.request.AdressCreateRequest;
import org.example.trendyolfinalproject.response.AdressResponse;
import org.example.trendyolfinalproject.service.AdressService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/adress")
@RequiredArgsConstructor
public class AdressController {
    private final AdressService adressService;

    @PostMapping("/createAdress")
    AdressResponse createAdress(@RequestBody @Valid AdressCreateRequest request) {
        return adressService.createAdress(request);
    }

    @DeleteMapping("/deleteAdress/{id}")
    public void deleteAdress(@PathVariable Long id) {
        adressService.deleteAdress(id);
    }

    @GetMapping("/getAdresses")
    public List<AdressResponse> getAdresses() {
        return adressService.getAdresses();
    }

    @PatchMapping("/updateAdress/{id}")
    public AdressResponse updateAdress(@PathVariable Long id, @RequestBody @Valid AdressCreateRequest request) {
        return adressService.updateAdress(id, request);
    }




}
