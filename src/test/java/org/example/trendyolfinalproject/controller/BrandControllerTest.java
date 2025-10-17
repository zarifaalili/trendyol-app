package org.example.trendyolfinalproject.controller;

import org.example.trendyolfinalproject.model.request.BrandCreateRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.BrandResponse;
import org.example.trendyolfinalproject.service.BrandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BrandControllerTest {

    @Mock
    private BrandService brandService;

    @InjectMocks
    private BrandController brandController;

    private BrandCreateRequest validRequest;
    private BrandResponse brandResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validRequest = new BrandCreateRequest("Nike", "Sportswear brand");
        brandResponse = new BrandResponse(1L, "Nike", "Sportswear brand");
    }

    @Test
    void testCreateBrand_Success() {
        ApiResponse<BrandResponse> expectedResponse =
                new ApiResponse<>(201, "Brand created successfully", brandResponse);

        when(brandService.createBrand(validRequest)).thenReturn(expectedResponse);

        ResponseEntity<ApiResponse<BrandResponse>> response = brandController.createBrand(validRequest);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Brand created successfully", response.getBody().getMessage());
        assertEquals("Nike", response.getBody().getData().getName());
        verify(brandService, times(1)).createBrand(validRequest);
    }

    @Test
    void testUpdateBrand_Success() {
        ApiResponse<BrandResponse> expectedResponse =
                new ApiResponse<>(200, "Brand updated successfully", brandResponse);

        when(brandService.updateBrand(1L, validRequest)).thenReturn(expectedResponse);

        ResponseEntity<ApiResponse<BrandResponse>> response = brandController.updateBrand(1L, validRequest);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Brand updated successfully", response.getBody().getMessage());
        assertEquals("Nike", response.getBody().getData().getName());
        verify(brandService, times(1)).updateBrand(1L, validRequest);
    }

    @Test
    void testGetBrandByName_Success() {
        ApiResponse<BrandResponse> expectedResponse =
                new ApiResponse<>(200, "Brand found", brandResponse);

        when(brandService.getBrandbyName("Nike")).thenReturn(expectedResponse);

        ResponseEntity<ApiResponse<BrandResponse>> response = brandController.getBrandbyName("Nike");

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Brand found", response.getBody().getMessage());
        assertEquals("Nike", response.getBody().getData().getName());
        verify(brandService, times(1)).getBrandbyName("Nike");
    }

    @Test
    void testCreateBrand_Fail_Validation() {
        BrandCreateRequest invalidRequest = new BrandCreateRequest("", "");

        ApiResponse<BrandResponse> expectedResponse =
                new ApiResponse<>(400, "Validation failed", null);

        when(brandService.createBrand(invalidRequest)).thenReturn(expectedResponse);

        ResponseEntity<ApiResponse<BrandResponse>> response = brandController.createBrand(invalidRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Validation failed", response.getBody().getMessage());
        assertNull(response.getBody().getData());
        verify(brandService, times(1)).createBrand(invalidRequest);
    }
}
