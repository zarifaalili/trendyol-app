package org.example.trendyolfinalproject.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.entity.Product;
import org.example.trendyolfinalproject.dao.entity.Review;
import org.example.trendyolfinalproject.dao.entity.Seller;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.dao.repository.*;
import org.example.trendyolfinalproject.exception.customExceptions.AlreadyException;
import org.example.trendyolfinalproject.mapper.SellerMapper;
import org.example.trendyolfinalproject.model.enums.NotificationType;
import org.example.trendyolfinalproject.model.enums.Role;
import org.example.trendyolfinalproject.model.Status;
import org.example.trendyolfinalproject.model.request.SellerCreateRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.SellerResponse;
import org.example.trendyolfinalproject.service.AuditLogService;
import org.example.trendyolfinalproject.service.NotificationService;
import org.example.trendyolfinalproject.service.SellerService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class SellerServiceImpl implements SellerService {

    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final SellerMapper sellerMapper;
    private final BasketRepository basketRepository;
    private final NotificationService notificationService;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final AuditLogService auditLogService;

    @Override
    public ApiResponse<SellerResponse> createSeller(SellerCreateRequest request) {
        log.info("Actionlog.createSeller.start : companyName={}", request.getCompanyName());
        Long sellerId = getCurrentUserId();
        User user = userRepository.findById(sellerId).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getRole().equals(Role.SELLER)) {
            throw new AlreadyException("User is already a seller");
        }
        if (user.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("You cant be a seller");
        }
        if (sellerRepository.existsByCompanyName(request.getCompanyName())) {
            throw new AlreadyException("Company name already exists");
        }
        if (sellerRepository.existsByTaxId(request.getTaxId())) {
            throw new AlreadyException("Tax ID already exists");
        }
        var basket = basketRepository.findByUserId(sellerId);
        basket.ifPresent(basketRepository::delete);

        user.setRole(Role.SELLER);
        user.setIsActive(false);
        userRepository.save(user);

        var seller = sellerMapper.toEntity(request);
        seller.setUser(user);
        seller.setStatus(Status.PENDING);
        seller.setContactEmail(user.getEmail());
        sellerRepository.save(seller);
        var response = sellerMapper.toResponse(seller);
        notificationService.sendToAdmins("New seller request", NotificationType.SELLER_REQUEST, seller.getId());
        notificationService.sendNotification(user, "Your seller account has been created. Please wait for approval", NotificationType.SELLER_CREATED, seller.getId());
        log.info("Actionlog.createSeller.end : sellerName={}", request.getCompanyName());
        return ApiResponse.<SellerResponse>builder()
                .data(response)
                .message("Seller created successfully")
                .status(200)
                .build();
    }

    @Override
    public ApiResponse<List<SellerResponse>> getSellers() {
        var sellers = sellerRepository.findAll();
        if (!sellers.isEmpty()) {
            var response = sellers.stream().map(sellerMapper::toResponse).toList();
            return ApiResponse.<List<SellerResponse>>builder()
                    .data(response)
                    .status(200)
                    .build();
        }
        return ApiResponse.<List<SellerResponse>>builder()
                .message("Sellers not found")
                .status(200)
                .data(List.of())
                .build();
    }

    @Override
    public ApiResponse<SellerResponse> getSeller(String companyName) {
        Seller seller = sellerRepository.findFirstByCompanyName(companyName)
                .orElseThrow(() -> new RuntimeException("Seller not found with company name: " + companyName));
        var response = sellerMapper.toResponse(seller);
        return ApiResponse.<SellerResponse>builder()
                .data(response)
                .status(200)
                .message("Seller found successfully")
                .build();
    }

    @Override
    public ApiResponse<Double> getSellerAverageRating(Long sellerId) {
        var userId = getCurrentUserId();
        log.info("Actionlog.getSellerAverageRating.start : sellerId={}", sellerId);
        List<Product> sellerProducts = productRepository.findBySellerId(sellerId);

        if (sellerProducts.isEmpty()) {
            return ApiResponse.success(0.0);
        }
        double totalRating = 0.0;
        int reviewCount = 0;

        for (Product product : sellerProducts) {
            List<Review> approvedReviews = reviewRepository.findByProduct_IdAndIsApproved(product.getId(), true);
            for (Review review : approvedReviews) {
                totalRating += review.getRating();
                reviewCount++;
            }
        }
        double averageRating = reviewCount > 0 ? totalRating / reviewCount : 0.0;
        auditLogService.createAuditLog(
                userRepository.findById(userId).orElseThrow(),
                "Seller Average Rating",
                "Average rating for seller with id: " + sellerId + " is: " + averageRating
        );
        log.info("Actionlog.getSellerAverageRating.end : sellerId={}", sellerId);
        return ApiResponse.success(averageRating);
    }

    private Long getCurrentUserId() {
        return (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId");
    }
}
