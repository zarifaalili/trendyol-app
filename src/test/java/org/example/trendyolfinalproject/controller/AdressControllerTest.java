package org.example.trendyolfinalproject.controller;

import org.example.trendyolfinalproject.model.request.AdressCreateRequest;
import org.example.trendyolfinalproject.model.response.AdressResponse;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.service.AdressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdressControllerTest {

    @Mock
    private AdressService adressService;

    @InjectMocks
    private AdressController adressController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAdressSuccess() {
        AdressCreateRequest request = new AdressCreateRequest("Baku", "Absheron", "Main St", "AZ1000", "Azerbaijan");
        AdressResponse adressResponse = new AdressResponse(1L, 2L, "Baku", "Absheron", "Main St", "AZ1000", "Azerbaijan", true);
        ApiResponse<AdressResponse> apiResponse = ApiResponse.success(adressResponse);

        when(adressService.createAdress(request)).thenReturn(apiResponse);

        ResponseEntity<ApiResponse<AdressResponse>> response = adressController.createAdress(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(adressResponse, response.getBody().getData());
        verify(adressService, times(1)).createAdress(request);
    }

    @Test
    void testDeleteAdressSuccess() {
        Long id = 1L;
        ApiResponse<Void> apiResponse = ApiResponse.noContent();

        when(adressService.deleteAdress(id)).thenReturn(apiResponse);

        ResponseEntity<ApiResponse<Void>> response = adressController.deleteAdress(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody().getData());
        verify(adressService, times(1)).deleteAdress(id);
    }

    @Test
    void testGetAdressesSuccess() {
        List<AdressResponse> list = List.of(
                new AdressResponse(1L, 2L, "Baku", "Absheron", "Street 1", "AZ1001", "Azerbaijan", false),
                new AdressResponse(2L, 2L, "Ganja", "Ganja", "Street 2", "AZ2002", "Azerbaijan", true)
        );

        ApiResponse<List<AdressResponse>> apiResponse = ApiResponse.success(list);
        when(adressService.getAdresses()).thenReturn(apiResponse);

        ResponseEntity<ApiResponse<List<AdressResponse>>> response = adressController.getAdresses();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getData().size());
        verify(adressService, times(1)).getAdresses();
    }

    @Test
    void testUpdateAdressSuccess() {
        Long id = 1L;
        AdressCreateRequest request = new AdressCreateRequest("Baku", "Absheron", "Main St", "AZ1000", "Azerbaijan");
        AdressResponse updated = new AdressResponse(id, 2L, "Baku", "Absheron", "Main St", "AZ1000", "Azerbaijan", true);
        ApiResponse<AdressResponse> apiResponse = ApiResponse.success(updated);

        when(adressService.updateAdress(id, request)).thenReturn(apiResponse);

        ResponseEntity<ApiResponse<AdressResponse>> response = adressController.updateAdress(id, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updated.getCity(), response.getBody().getData().getCity());
        verify(adressService, times(1)).updateAdress(id, request);
    }

    @Test
    void testCreateAdressFail() {
        AdressCreateRequest invalid = new AdressCreateRequest("", "Absheron", "", "AZ1000", "Azerbaijan");
        ApiResponse<AdressResponse> apiResponse = ApiResponse.error("Validation failed");

        when(adressService.createAdress(invalid)).thenReturn(apiResponse);

        ResponseEntity<ApiResponse<AdressResponse>> response = adressController.createAdress(invalid);

        assertEquals("Validation failed", response.getBody().getMessage());
        assertNull(response.getBody().getData());
        verify(adressService, times(1)).createAdress(invalid);
    }

    @Test
    void testUpdateAdressException() {
        Long id = 1L;
        AdressCreateRequest request = new AdressCreateRequest("Baku", "Absheron", "Main St", "AZ1000", "Azerbaijan");

        when(adressService.updateAdress(id, request)).thenThrow(new RuntimeException("Database error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> adressController.updateAdress(id, request));

        assertEquals("Database error", ex.getMessage());
        verify(adressService, times(1)).updateAdress(id, request);
    }
}
