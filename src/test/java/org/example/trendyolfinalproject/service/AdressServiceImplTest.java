package org.example.trendyolfinalproject.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.trendyolfinalproject.dao.entity.Adress;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.dao.repository.AdressRepository;
import org.example.trendyolfinalproject.dao.repository.OrderRepository;
import org.example.trendyolfinalproject.dao.repository.UserRepository;
import org.example.trendyolfinalproject.exception.customExceptions.AlreadyException;
import org.example.trendyolfinalproject.exception.customExceptions.NotFoundException;
import org.example.trendyolfinalproject.mapper.AdressMapper;
import org.example.trendyolfinalproject.model.request.AdressCreateRequest;
import org.example.trendyolfinalproject.model.response.AdressResponse;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.service.impl.AdressServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdressServiceImplTest {

    @Mock
    private AdressRepository adressRepository;
    @Mock
    private AdressMapper adressMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuditLogService auditLogService;
    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private AdressServiceImpl adressService;

    @Mock
    private OrderRepository orderRepository;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        when(request.getAttribute("userId")).thenReturn(1L);
    }


    @Test
    void testCreateAdress_Success() {
        User user = new User();
        user.setId(1L);
        AdressCreateRequest requestDto =
                new AdressCreateRequest("Baku", "Nizami", "Main", "AZ1000", "Azerbaijan");

        Adress entity = new Adress();
        Adress saved = new Adress();
        saved.setId(100L);
        AdressResponse response = new AdressResponse();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(adressRepository.findByUserId_IdAndCityAndStateAndStreetAndZipCodeAndCountry(
                1L, "Baku", "Nizami", "Main", "AZ1000", "Azerbaijan"
        )).thenReturn(Optional.empty());
        when(adressMapper.toEntity(requestDto)).thenReturn(entity);
        when(adressRepository.findAllByUserId_Id(1L)).thenReturn(Collections.emptyList());
        when(adressRepository.save(entity)).thenReturn(saved);
        when(adressMapper.toResponse(saved)).thenReturn(response);

        ApiResponse<AdressResponse> result = adressService.createAdress(requestDto);

        assertEquals(201, result.getStatus());
        assertEquals(response, result.getData());
        verify(adressRepository).save(entity);
        verify(auditLogService).createAuditLog(user, "Create Adress",
                "Adress created successfully. Adress id: 100");
    }

    @Test
    void testCreateAdress_UserNotFound_ThrowsException() {
        AdressCreateRequest requestDto =
                new AdressCreateRequest("Baku", "Nizami", "Main", "AZ1000", "Azerbaijan");
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> adressService.createAdress(requestDto));
    }

    @Test
    void testCreateAdress_AlreadyExists_ThrowsException() {
        AdressCreateRequest requestDto =
                new AdressCreateRequest("Baku", "Nizami", "Main", "AZ1000", "Azerbaijan");
        User user = new User(); user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(adressRepository.findByUserId_IdAndCityAndStateAndStreetAndZipCodeAndCountry(
                anyLong(), anyString(), anyString(), anyString(), anyString(), anyString()
        )).thenReturn(Optional.of(new Adress()));

        assertThrows(AlreadyException.class, () -> adressService.createAdress(requestDto));
    }

    @Test
    void testDeleteAdress_Success() {
        User user = new User();
        user.setId(1L);

        Adress adress = new Adress();
        adress.setId(5L);
        adress.setUserId(user);
        adress.setIsDefault(true);

        when(adressRepository.findById(5L)).thenReturn(Optional.of(adress));
        when(adressRepository.findAllByUserId_Id(1L))
                .thenReturn(List.of(new Adress()));

        ApiResponse<Void> result = adressService.deleteAdress(5L);

        assertEquals(204, result.getStatus());

        assertNull(result.getData());

        verify(adressRepository).deleteById(5L);
        verify(auditLogService).createAuditLog(
                user,
                "Delete Adress",
                "Adress deleted successfully. Adress id: 5"
        );
    }

    @Test
    void testDeleteAdress_NotFound_ThrowsNotFoundException() {
        when(adressRepository.findById(11L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> adressService.deleteAdress(11L));
    }

    @Test
    void testDeleteAdress_WrongUser_ThrowsRuntimeException() {
        User otherUser = new User(); otherUser.setId(2L);
        Adress adress = new Adress();
        adress.setUserId(otherUser);
        when(adressRepository.findById(1L)).thenReturn(Optional.of(adress));

        assertThrows(RuntimeException.class, () -> adressService.deleteAdress(1L));
    }


    @Test
    void testGetAdresses_Success() {
        User user = new User(); user.setId(1L);
        Adress adress = new Adress();
        adress.setUserId(user);
        AdressResponse response = new AdressResponse();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(adressRepository.findAllByUserId_Id(1L)).thenReturn(List.of(adress));
        when(adressMapper.toResponseList(anyList())).thenReturn(List.of(response));

        ApiResponse<List<AdressResponse>> result = adressService.getAdresses();

        assertEquals(200, result.getStatus());
        assertEquals(1, result.getData().size());
        verify(auditLogService).createAuditLog(user, "Get all adresses",
                "Get all adresses successfully.");
    }

    @Test
    void testGetAdresses_UserNotFound_ThrowsNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> adressService.getAdresses());
    }

    @Test
    void testGetAdresses_EmptyList_ThrowsRuntimeException() {
        User user = new User(); user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(adressRepository.findAllByUserId_Id(1L)).thenReturn(Collections.emptyList());

        assertThrows(RuntimeException.class, () -> adressService.getAdresses());
    }




    @Test
    void testUpdateAdress_Success() {
        User user = new User(); user.setId(1L);
        Adress adress = new Adress(); adress.setId(7L); adress.setUserId(user);
        AdressCreateRequest req =
                new AdressCreateRequest("Ganja", "Kapaz", "Street", "AZ2000", "Azerbaijan");
        AdressResponse response = new AdressResponse();

        when(adressRepository.findById(7L)).thenReturn(Optional.of(adress));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(adressRepository.save(any())).thenReturn(adress);
        when(adressMapper.toResponse(adress)).thenReturn(response);

        ApiResponse<AdressResponse> result = adressService.updateAdress(7L, req);

        assertEquals(200, result.getStatus());
        verify(auditLogService).createAuditLog(user, "Update Adress",
                "Adress updated successfully. Adress id: 7");
    }

    @Test
    void testUpdateAdress_NotFound_ThrowsRuntimeException() {
        when(adressRepository.findById(9L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> adressService.updateAdress(9L, new AdressCreateRequest()));
    }

    @Test
    void testUpdateAdress_WrongUser_ThrowsRuntimeException() {
        User user1 = new User(); user1.setId(1L);
        User user2 = new User(); user2.setId(2L);
        Adress adress = new Adress(); adress.setUserId(user2);
        when(adressRepository.findById(4L)).thenReturn(Optional.of(adress));

        assertThrows(RuntimeException.class, () -> adressService.updateAdress(4L, new AdressCreateRequest()));
    }
}
