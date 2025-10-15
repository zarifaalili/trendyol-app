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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdressControllerTest {

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
        AdressResponse response = new AdressResponse(1L, 2L, "Baku", "Absheron", "Main St", "AZ1000", "Azerbaijan", true);
        ApiResponse<AdressResponse> expectedResponse = ApiResponse.success(response);

        when(adressService.createAdress(request)).thenReturn(expectedResponse);

        ApiResponse<AdressResponse> actualResponse = adressController.createAdress(request);

        assertEquals(200, actualResponse.getStatus());
        assertEquals("success", actualResponse.getMessage());
        verify(adressService, times(1)).createAdress(request);
    }

    @Test
    void testDeleteAdressSuccess() {
        Long id = 1L;
        ApiResponse<String> expectedResponse = ApiResponse.success("Deleted successfully");

        when(adressService.deleteAdress(id)).thenReturn(expectedResponse);

        ApiResponse<String> actualResponse = adressController.deleteAdress(id);

        assertEquals("Deleted successfully", actualResponse.getData());
        verify(adressService, times(1)).deleteAdress(id);
    }

    @Test
    void testGetAdressesSuccess() {
        List<AdressResponse> adressList = List.of(
                new AdressResponse(1L, 2L, "Baku", "Absheron", "Street 1", "AZ1001", "Azerbaijan", false),
                new AdressResponse(2L, 2L, "Ganja", "Ganja", "Street 2", "AZ2002", "Azerbaijan", true)
        );

        ApiResponse<List<AdressResponse>> expectedResponse = ApiResponse.success(adressList);
        when(adressService.getAdresses()).thenReturn(expectedResponse);

        ApiResponse<List<AdressResponse>> actualResponse = adressController.getAdresses();

        assertEquals(2, actualResponse.getData().size());
        verify(adressService, times(1)).getAdresses();
    }

    @Test
    void testCreateAdressFail_whenFieldsBlank() {
        AdressCreateRequest invalidRequest = new AdressCreateRequest("", "Absheron", "", "AZ1000", "Azerbaijan");

        ApiResponse<AdressResponse> expectedResponse = ApiResponse.error("Validation failed");
        when(adressService.createAdress(invalidRequest)).thenReturn(expectedResponse);

        ApiResponse<AdressResponse> response = adressController.createAdress(invalidRequest);

        assertEquals(400, response.getStatus());
        assertEquals("Validation failed", response.getMessage());
        assertNull(response.getData());
        verify(adressService, times(1)).createAdress(invalidRequest);
    }


    @Test
    void testDeleteAdressFail_whenNotFound() {
        Long id = 99L;
        ApiResponse<String> expectedResponse = ApiResponse.error("Address not found");

        when(adressService.deleteAdress(id)).thenReturn(expectedResponse);

        ApiResponse<String> actualResponse = adressController.deleteAdress(id);

        assertEquals(400, actualResponse.getStatus());
        assertEquals("Address not found", actualResponse.getMessage());
        verify(adressService, times(1)).deleteAdress(id);
    }

    @Test
    void testUpdateAdressFail_whenServiceThrowsException() {
        Long id = 1L;
        AdressCreateRequest request = new AdressCreateRequest("Baku", "Absheron", "Main St", "AZ1000", "Azerbaijan");

        when(adressService.updateAdress(id, request)).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                adressController.updateAdress(id, request)
        );

        assertEquals("Database error", exception.getMessage());
        verify(adressService, times(1)).updateAdress(id, request);
    }
}
