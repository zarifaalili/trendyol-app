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

        ApiResponse<BrandResponse> actualResponse = brandController.createBrand(validRequest);

        assertNotNull(actualResponse);
        assertEquals(201, actualResponse.getStatus());
        assertEquals("Brand created successfully", actualResponse.getMessage());
        assertEquals("Nike", actualResponse.getData().getName());
        verify(brandService, times(1)).createBrand(validRequest);
    }

    @Test
    void testCreateBrand_Fail_BlankName() {
        BrandCreateRequest invalidRequest = new BrandCreateRequest("", "Some desc");


        when(brandService.createBrand(invalidRequest)).thenReturn(null);

        ApiResponse<BrandResponse> response = brandController.createBrand(invalidRequest);

        assertNull(response);
        verify(brandService, times(1)).createBrand(invalidRequest);
    }

    @Test
    void testUpdateBrand_Success() {
        ApiResponse<BrandResponse> expectedResponse =
                new ApiResponse<>(200, "Brand updated successfully", brandResponse);

        when(brandService.updateBrand(1L, validRequest)).thenReturn(expectedResponse);

        ApiResponse<BrandResponse> response = brandController.updateBrand(1L, validRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Brand updated successfully", response.getMessage());
        verify(brandService, times(1)).updateBrand(1L, validRequest);
    }

    @Test
    void testDeleteBrand_Success() {
        ApiResponse<String> expectedResponse =
                new ApiResponse<>(200, "Brand deleted successfully", "Deleted");

        when(brandService.deleteBrand(1L)).thenReturn(expectedResponse);

        ApiResponse<String> response = brandController.deleteBrand(1L);

        assertNotNull(response);
        assertEquals("Deleted", response.getData());
        verify(brandService, times(1)).deleteBrand(1L);
    }

    @Test
    void testGetBrandByName_Success() {
        ApiResponse<BrandResponse> expectedResponse =
                new ApiResponse<>(200, "Brand found", brandResponse);

        when(brandService.getBrandbyName("Nike")).thenReturn(expectedResponse);

        ApiResponse<BrandResponse> response = brandController.getBrandbyName("Nike");

        assertNotNull(response);
        assertEquals("Brand found", response.getMessage());
        verify(brandService, times(1)).getBrandbyName("Nike");
    }
}
