package org.example.trendyolfinalproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.entity.Product;
import org.example.trendyolfinalproject.dao.repository.*;
import org.example.trendyolfinalproject.exception.customExceptions.NotFoundException;
import org.example.trendyolfinalproject.mapper.ProductMapper;
import org.example.trendyolfinalproject.mapper.ProductVariantMapper;
import org.example.trendyolfinalproject.model.enums.NotificationType;
import org.example.trendyolfinalproject.model.Status;
import org.example.trendyolfinalproject.model.request.ProductRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.ProductResponse;
import org.example.trendyolfinalproject.service.AuditLogService;
import org.example.trendyolfinalproject.service.NotificationService;
import org.example.trendyolfinalproject.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final SellerRepository sellerRepository;
    private final ProductMapper productMapper;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;
    private final OrderItemRepository orderItemRepository;

    private static final String PRODUCT_KEY_PREFIX = "product:";
    private final ProductVariantRepository productVariantRepository;
    private final ProductVariantMapper productVariantMapper;

    @Override
    public ApiResponse<ProductResponse> createProduct(ProductRequest request) {
        log.info("Actionlog.createProduct.start : name={}", request.getName());

        Long currentUserId = (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId");

        var user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("Seller not found with id: " + currentUserId));

        var checkCategory = categoryRepository.findById(request.getCategoryId()).orElseThrow(
                () -> new NotFoundException("Category not found")
        );

        var checkBrand = brandRepository.findById(request.getBrandId()).orElseThrow(
                () -> new NotFoundException("Brand not found")
        );


        var checkSeller = sellerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NotFoundException("Seller not found with userId: 12"));

        if (!checkSeller.getStatus().equals(Status.ACTIVE)) {
            throw new RuntimeException("Seller is not active. Please wait for approval.");
        }


        var entity = productMapper.toEntity(request);
        entity.setCategory(checkCategory);
        entity.setBrand(checkBrand);
        entity.setSeller(checkSeller);
        entity.setPreviousPrice(BigDecimal.ZERO);


        var saved = productRepository.save(entity);
        var response = productMapper.toResponse(saved);

        auditLogService.createAuditLog(user, "Product created", "Product created successfully. Product id: " + saved.getId());

        log.info("Actionlog.createProduct.end : name={}", request.getName());
        return ApiResponse.<ProductResponse>builder()
                .status(201)
                .message("Product created successfully")
                .data(response)
                .build();
    }


    @Override
    public ApiResponse<Page<ProductResponse>> getProducts(int page, int size) {
        log.info("Actionlog.getProducts.start : ");

        Pageable pageable = PageRequest.of(page, size);

        Page<Product> products = productRepository.findAllByStatus(Status.ACTIVE, pageable);
        log.info("Actionlog.getProducts.end : ");
//        Long currentUserId = (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
//                .getRequest().getAttribute("userId");
//        var user = userRepository.findById(currentUserId).orElseThrow();
//        if (user.getId() != null) {
//            auditLogService.createAuditLog(user, "Get all products", "Get all products successfully. Product id: " + products.get(0).getId());
//        }
        Page<ProductResponse> response = products.map(productMapper::toResponse);

        return ApiResponse.<Page<ProductResponse>>builder()
                .status(200)
                .message("Products fetched successfully")
                .data(response)
                .build();
    }

    @Override
    public ApiResponse<List<ProductResponse>> getProductByName(String name) {
        log.info("Actionlog.getProductByName.start : name={}", name);
        Long currentUserId = (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId");
        var user = userRepository.findById(currentUserId).orElseThrow();
        List<Product> products = productRepository.findbyName(name);
        if (products.isEmpty()) {
            throw new NotFoundException("Product not found");
        }
        var mapper = productMapper.toResponseList(products);

        auditLogService.createAuditLog(user, "Get product by name", "Get product by name successfully. Product id: " + products.get(0).getId());

        log.info("Actionlog.getProductByName.end : name={}", name);
        return ApiResponse.<List<ProductResponse>>builder()
                .status(200)
                .message("Products fetched successfully by name")
                .data(mapper)
                .build();
    }

    @Override
    public ApiResponse<ProductResponse> updateProductPrice(Long productId, BigDecimal newPrice) {
        log.info("Actionlog.updateProductPrice.start : productId={}", productId);
        var currentUserId = getCurrentUserId();
        var seller1 = sellerRepository.findByUserId(currentUserId).orElseThrow(() -> new NotFoundException("Seller not found with userId: " + currentUserId));
        var product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        var seller = product.getSeller();

        if (!seller.getId().equals(seller1.getId())) {
            throw new RuntimeException("You are not authorized to update this product");
        }
        product.setPreviousPrice(product.getPrice());
        product.setPrice(newPrice);
        product.setUpdatedAt(LocalDateTime.now());
        var updated = productRepository.save(product);
        var response = productMapper.toResponse(updated);
        auditLogService.createAuditLog(seller.getUser(), "Update product price", "Update product price successfully. Product id: " + updated.getId());

        if (product.getPreviousPrice().compareTo(product.getPrice()) > 0) {
            notificationService.sendToUsersWithBasketVariant("HURRY UP Your basket product price decreased", NotificationType.PRODUCT_PRICE_UPDATE, updated.getId());
            notificationService.sendToUsersWithWishListVariant("HURRY UP Your wish list product price decreased.Product : " + updated.getName(), NotificationType.PRODUCT_PRICE_UPDATE, updated.getId());
            notificationService.sendToAllUsers("HURRY UP Product price decreased", NotificationType.PRODUCT_PRICE_UPDATE, updated.getId());
        }

        log.info("Actionlog.updateProductPrice.end : productId={}", productId);
        return ApiResponse.<ProductResponse>builder()
                .status(200)
                .message("Product price updated successfully")
                .data(response)
                .build();

    }

    @Override
    public ApiResponse<List<ProductResponse>> getSellerProducts() {
        log.info("Actionlog.getSellerProducts.start : ");
        var userId = getCurrentUserId();

        var seller = sellerRepository.findByUserId(userId).orElseThrow(() -> new NotFoundException("Seller not found with userId: " + userId));

        var products = productRepository.findBySellerId(seller.getId());
        var mapper = productMapper.toResponseList(products);
        log.info("Actionlog.getSellerProducts.end : ");
        return ApiResponse.<List<ProductResponse>>builder()
                .status(200)
                .message("Seller products fetched successfully")
                .data(mapper)
                .build();
    }


    @Override
    public ApiResponse<Page<ProductResponse>> getTotalProductsBetweenDates(  String startDateStr, String endDateStr, int page, int size) {
        log.info("Actionlog.getTotalProductsBetweenDates.start : productId={}", startDateStr);
        var userId = getCurrentUserId();
        var seller = sellerRepository.findByUserId(userId).orElseThrow(() -> new NotFoundException("Seller not found with userId: " + userId));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate startDate = LocalDate.parse(startDateStr, formatter);
        LocalDate endDate = LocalDate.parse(endDateStr, formatter);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);


        if (startDate.isAfter(endDate)) {
            throw new RuntimeException("Start date must be before end date");
        }

        Pageable pageable = PageRequest.of(page, size);

        var orderItems = orderItemRepository.findByCreatedAtBetweenAndProductId_Seller_Id(startDateTime, endDateTime, seller.getId(), pageable);

        if (orderItems.isEmpty()) {
            throw new NotFoundException("No products found between these dates");
        }

        var productIds = orderItems.stream()
                .map(orderItem -> orderItem.getProductId().getId())
                .distinct()
                .toList();
        Page<Product> products = productRepository.findSellerProductsBetweenDates(
                seller.getId(), startDateTime, endDateTime, pageable);
        Page<ProductResponse> response = products.map(productMapper::toResponse);

        log.info("Actionlog.getTotalProductsBetweenDates.end : productId={}", startDateStr);
        return ApiResponse.<Page<ProductResponse>>builder()
                .status(200)
                .message("Products fetched successfully between dates")
                .data(response)
                .build();
    }


    private Long getCurrentUserId() {
        return (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId");
    }
}