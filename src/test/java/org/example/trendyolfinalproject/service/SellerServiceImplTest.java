package org.example.trendyolfinalproject.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.dao.entity.Seller;
import org.example.trendyolfinalproject.dao.entity.Product;
import org.example.trendyolfinalproject.dao.entity.Review;
import org.example.trendyolfinalproject.dao.repository.*;
import org.example.trendyolfinalproject.exception.customExceptions.AlreadyException;
import org.example.trendyolfinalproject.mapper.SellerMapper;
import org.example.trendyolfinalproject.model.enums.Status;
import org.example.trendyolfinalproject.model.enums.Role;
import org.example.trendyolfinalproject.model.request.SellerCreateRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.SellerResponse;
import org.example.trendyolfinalproject.service.impl.SellerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SellerServiceImplTest {

    @Mock
    private SellerRepository sellerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SellerMapper sellerMapper;
    @Mock
    private BasketRepository basketRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private SellerServiceImpl sellerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ServletRequestAttributes attributes = mock(ServletRequestAttributes.class);
        when(attributes.getRequest()).thenReturn(mock(HttpServletRequest.class));
        RequestContextHolder.setRequestAttributes(attributes);
        when(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId")).thenReturn(1L);
    }

    @Test
    void createSeller_success() {
        User user = new User();
        user.setRole(Role.CUSTOMER);
        user.setEmail("user@example.com");
        user.setId(1L);

        SellerCreateRequest request = new SellerCreateRequest("TestCompany", 123456);

        Seller seller = new Seller();
        seller.setUser(user);
        seller.setStatus(Status.PENDING);
        seller.setContactEmail(user.getEmail());
        seller.setId(1L);

        SellerResponse response = new SellerResponse();
        response.setCompanyName("TestCompany");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(sellerRepository.existsByCompanyName("TestCompany")).thenReturn(false);
        when(sellerRepository.existsByTaxId(123456)).thenReturn(false);
        when(sellerMapper.toEntity(request)).thenReturn(seller);
        when(sellerMapper.toResponse(seller)).thenReturn(response);

        ApiResponse<SellerResponse> apiResponse = sellerService.createSeller(request);

        assertEquals(201, apiResponse.getStatus());
        assertEquals("Seller created successfully", apiResponse.getMessage());
        assertEquals("TestCompany", apiResponse.getData().getCompanyName());

        verify(userRepository).save(user);
        verify(sellerRepository).save(seller);
        verify(notificationService).sendToAdmins(anyString(), any(), nullable(Long.class));
        verify(notificationService).sendNotification(eq(user), anyString(), any(), nullable(Long.class));
    }

    @Test
    void getSellers_success() {
        Seller seller = new Seller();
        SellerResponse response = new SellerResponse();
        response.setCompanyName("Company1");

        when(sellerRepository.findAll()).thenReturn(List.of(seller));
        when(sellerMapper.toResponse(seller)).thenReturn(response);

        ApiResponse<List<SellerResponse>> apiResponse = sellerService.getSellers();

        assertEquals(200, apiResponse.getStatus());
        assertEquals(1, apiResponse.getData().size());
        assertEquals("Company1", apiResponse.getData().get(0).getCompanyName());
    }


    @Test
    void getSellerAverageRating_success() {
        Product product = new Product();
        product.setId(1L);
        Review review1 = new Review();
        review1.setRating(4);
        Review review2 = new Review();
        review2.setRating(5);

        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findBySellerId(1L)).thenReturn(List.of(product));
        when(reviewRepository.findByProduct_IdAndIsApproved(1L, true)).thenReturn(List.of(review1, review2));

        ApiResponse<Double> apiResponse = sellerService.getSellerAverageRating(1L);

        assertEquals(200, apiResponse.getStatus());
        assertEquals(4.5, apiResponse.getData());
    }

    @Test
    void createSeller_userAlreadySeller_fail() {
        User user = new User();
        user.setRole(Role.SELLER);
        user.setId(1L);

        SellerCreateRequest request = new SellerCreateRequest("TestCompany", 123456);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        AlreadyException exception = assertThrows(AlreadyException.class, () -> {
            sellerService.createSeller(request);
        });

        assertEquals("User is already a seller", exception.getMessage());
    }

    @Test
    void createSeller_userIsAdmin_fail() {
        User user = new User();
        user.setRole(Role.ADMIN);
        user.setId(1L);

        SellerCreateRequest request = new SellerCreateRequest("TestCompany", 123456);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            sellerService.createSeller(request);
        });

        assertEquals("You cant be a seller", exception.getMessage());
    }

    @Test
    void createSeller_companyNameExists_fail() {
        User user = new User();
        user.setRole(Role.CUSTOMER);
        user.setId(1L);

        SellerCreateRequest request = new SellerCreateRequest("TestCompany", 123456);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(sellerRepository.existsByCompanyName("TestCompany")).thenReturn(true);

        AlreadyException exception = assertThrows(AlreadyException.class, () -> {
            sellerService.createSeller(request);
        });

        assertEquals("Company name already exists", exception.getMessage());
    }

    @Test
    void createSeller_taxIdExists_fail() {
        User user = new User();
        user.setRole(Role.CUSTOMER);
        user.setId(1L);

        SellerCreateRequest request = new SellerCreateRequest("TestCompany", 123456);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(sellerRepository.existsByCompanyName("TestCompany")).thenReturn(false);
        when(sellerRepository.existsByTaxId(123456)).thenReturn(true);

        AlreadyException exception = assertThrows(AlreadyException.class, () -> {
            sellerService.createSeller(request);
        });

        assertEquals("Tax ID already exists", exception.getMessage());
    }

    @Test
    void getSeller_notFound_fail() {
        when(sellerRepository.findFirstByCompanyName("UnknownCompany")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            sellerService.getSeller("UnknownCompany");
        });

        assertEquals("Seller not found with company name: UnknownCompany", exception.getMessage());
    }

    @Test
    void getSellerAverageRating_noProducts_success() {
        when(productRepository.findBySellerId(1L)).thenReturn(List.of());
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ApiResponse<Double> apiResponse = sellerService.getSellerAverageRating(1L);

        assertEquals(200, apiResponse.getStatus());
        assertEquals(0.0, apiResponse.getData());
    }

}
