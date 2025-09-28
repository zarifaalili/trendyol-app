package org.example.trendyolfinalproject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.entity.OrderItem;
import org.example.trendyolfinalproject.dao.entity.Review;
import org.example.trendyolfinalproject.dao.entity.ReviewImage;
import org.example.trendyolfinalproject.dao.repository.*;
import org.example.trendyolfinalproject.exception.customExceptions.NotFoundException;
import org.example.trendyolfinalproject.mapper.ReviewMapper;
import org.example.trendyolfinalproject.model.enums.NotificationType;
import org.example.trendyolfinalproject.model.Status;
import org.example.trendyolfinalproject.projection.NegativeReviewProjection;
import org.example.trendyolfinalproject.model.request.ReviewCreateRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.ReviewResponse;
import org.example.trendyolfinalproject.model.response.TopRatedProductResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final ReviewMapper reviewMapper;
    private final ReviewImageRepository reviewImageRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;


    public ApiResponse<String> createReview(ReviewCreateRequest request) {
        log.info("Actionlog.createReview.start : productId={}", request.getProductId());
        var userId = getCurrentUserId();

        var user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        List<OrderItem> orderItem = orderItemRepository.findByProductId_Id(request.getProductId());
        if (orderItem.isEmpty()) {
            throw new NotFoundException("Product not found with id: " + request.getProductId());
        }


        var matchingOrderItem = orderItem.stream()
                .filter(item -> item.getOrderId().getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("You did not buy this product"));

        var order1 = matchingOrderItem.getOrderId();

//        var order1=orderItem.get(0).getOrderId();
        var orderUser = order1.getUser();
        if (!orderUser.getId().equals(userId)) {
            throw new RuntimeException("You can not add review to this product because you did not buy this product");
        }


        if (!order1.getStatus().equals(Status.DELIVERED)) {
            throw new RuntimeException("Order is not delivered. You cant add review");
        }


        var existingReview = reviewRepository.findByProductId_IdAndUserId_Id(request.getProductId(), userId);

        if (existingReview != null && !existingReview.getIsApproved()) {
            throw new RuntimeException("You already have a non-approved review for this product.");
        }

        var product = productRepository.findById(request.getProductId()).orElseThrow(() -> new RuntimeException("Product not found with id: " + request.getProductId()));


//        var productFromOrderItem = orderItemRepository.findByProductId_Id(request.getProductId()).orElseThrow(() -> new RuntimeException("Product not found with id: " + request.getProductId()));
//        List<OrderItem> productsFromOrderItem = orderItemRepository.findByProductId_Id(request.getProductId());

//        if (productsFromOrderItem.isEmpty()) {
//            throw new RuntimeException("Product not found with id: " + request.getProductId());
//        }
//        var productFromOrderItem = productsFromOrderItem.get(0);
//
//        var order = productFromOrderItem.getOrderId();


        var review = reviewMapper.toEntity(request);
        review.setUser(user);
        review.setProduct(product);
        var savedReview = reviewRepository.save(review);


        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            for (String imageUrl : request.getImageUrls()) {
                ReviewImage reviewImage = new ReviewImage();
                reviewImage.setImageUrl(imageUrl);
                reviewImage.setReview(savedReview);
                reviewImageRepository.save(reviewImage);
            }
        }


        notificationService.sendToAdmins("New Review", NotificationType.NEW_REVIEW, savedReview.getId());
        auditLogService.createAuditLog(user, "Review created", "Review created with id: " + savedReview.getId());
        log.info("Actionlog.createReview.end : productId={}", request.getProductId());

        return ApiResponse.success("Review is waiting for approval");

    }

    public ApiResponse<Double> getAverageRating(Long productId) {
        var user = (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId");

        log.info("Actionlog.getAverageRating.start : productId={}", productId);
        List<Review> productfromReview = reviewRepository.findByProduct_IdAndIsApproved(productId, true);
//        var approved=reviewRepository.findByProduct_IdAndIsApproved(productId,true);

        if (productfromReview.isEmpty()) {
            return ApiResponse.success(0.0);
        }
        Double totalRating = 0.0;
        for (Review review : productfromReview) {
            totalRating += review.getRating();
        }
        if (user != null) {
            auditLogService.createAuditLog(userRepository.findById(user).orElseThrow(), "Average rating", "Average rating for product with id: " + productId + " is: " + totalRating);

        }
        log.info("Actionlog.getAverageRating.end : productId={}", productId);
        var averageRating = !productfromReview.isEmpty() ? totalRating / (double) productfromReview.size() : 0.0;
        return ApiResponse.success(averageRating);


    }

    public ApiResponse<List<ReviewResponse>> getreviews(Long productId) {
        log.info("Actionlog.getreviews.start : productId={}", productId);
        List<Review> productfromReview = reviewRepository.findByProduct_Id(productId);
        if (productfromReview.isEmpty()) {
            throw new NotFoundException("Product not found with id: " + productId);
        }

        List<ReviewResponse> responselist = reviewMapper.toResponseList(productfromReview);
        for (int i = 0; i < productfromReview.size(); i++) {
            Review review = productfromReview.get(i);
            ReviewResponse response = responselist.get(i);

            List<String> imageUrls = reviewImageRepository.findByReviewId(review.getId())
                    .stream()
                    .map(ReviewImage::getImageUrl)
                    .collect(Collectors.toList());

            response.setImageUrls(imageUrls);
        }
        var user = userRepository.findById(getCurrentUserId()).orElseThrow();
        auditLogService.createAuditLog(user, "Get reviews", "Get reviews for product with id: " + productId);
        log.info("Actionlog.getreviews.end : productId={}", productId);
        return new ApiResponse<>(200, "Reviews fetched successfully", responselist);


    }


    public ApiResponse<List<TopRatedProductResponse>> getTopRatedProducts() {
        log.info("Actionlog.getTopRatedProducts.start");
        var userId = getCurrentUserId();
        var user = userRepository.findById(userId).orElseThrow();
        var topRatedProducts = reviewRepository.findTopRatedProducts();

//        var response = topRatedProducts.stream()
//                .map(p -> new TopRatedProductResponse(
//                        p.getProductId(),
//                        p.getProductName(),
//                        p.getAvgRating()
//                ))
//                .toList();

        var response = reviewMapper.toResponseListProjection(topRatedProducts);

        auditLogService.createAuditLog(user, "Get top rated products", "Get top rated products");
        log.info("Actionlog.getTopRatedProducts.end");
        return ApiResponse.<List<TopRatedProductResponse>>builder()
                .status(200)
                .message("Top rated products fetched successfully")
                .data(response)
                .build();

    }


    public ApiResponse<List<ReviewResponse>> getUserReviews() {
        log.info("ActionLog.getUserReviews.start");
        var userId = getCurrentUserId();
        var user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found with id: " + userId)
        );
        var userReviews = reviewRepository.findByUser(user);

        var response = reviewMapper.toResponseList(userReviews);
        auditLogService.createAuditLog(user, "Get user reviews", "Get user reviews");
        log.info("ActionLog.getUserReviews.end");
        return ApiResponse.<List<ReviewResponse>>builder()
                .status(200)
                .message("User reviews fetched successfully")
                .data(response)
                .build();
    }

    public ApiResponse<List<ReviewResponse>> getUserReviewsByAdmin(Long userId) {
        log.info("ActionLog.getUserReviewsByAdmin.start");
        var user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found with id: " + userId)
        );
        var userReviews = reviewRepository.findByUser(user);

        var response = reviewMapper.toResponseList(userReviews);
        auditLogService.createAuditLog(user, "Get user reviews", "Get user reviews");
        log.info("ActionLog.getUserReviewsByAdmin.end");
        return ApiResponse.<List<ReviewResponse>>builder()
                .status(200)
                .message("User reviews fetched by Admin successfully")
                .data(response)
                .build();

    }


    public ApiResponse<List<NegativeReviewProjection>> getNegativeReview() {
        log.info("ActionLog.getNegativeReview.start");
        var userId = getCurrentUserId();
        var user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found with id: " + userId)
        );
        var negativeReview = reviewRepository.findNegativeReviews();
        var response = reviewMapper.toNegativeReviewProjectionList(negativeReview);
        auditLogService.createAuditLog(user, "Get negative reviews", "Get negative reviews");
        log.info("ActionLog.getNegativeReview.end");
        return ApiResponse.<List<NegativeReviewProjection>>builder()
                .status(200)
                .message("Negative reviews fetched successfully")
                .data(response)
                .build();

    }


    public ApiResponse<List<ReviewResponse>> getProductReviewsWithFilter(Long productId, Integer[] rating, String subject) {
        log.info("ActionLog.getProductReviewsWithFilter.start");
        var userId = getCurrentUserId();
        var user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found with id: " + userId)
        );
        var product = productRepository.findById(productId).orElseThrow(
                () -> new NotFoundException("Product not found with id: " + productId)
        );

        if (rating == null && subject == null) {
            return getreviews(productId);
        }
        var productReviews = reviewRepository.findByProductAndFilterNative(productId, rating, subject);
        if (productReviews.isEmpty()) {
            throw new NotFoundException("Any Items not found with this filter ");
        }
        var response = reviewMapper.toResponseList(productReviews);
        auditLogService.createAuditLog(user, "Get product reviews", "Get product reviews");
        log.info("ActionLog.getProductReviewsWithFilter.end");
        return new ApiResponse<>(200, "Product reviews fetched successfully", response);


    }

    private Long getCurrentUserId() {
        return (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId");
    }


//    public void deleteReview(Long reviewId) {
//        log.info("Actionlog.deleteReview.start : reviewId={}", reviewId);
//        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new RuntimeException("Review not found with id: " + reviewId));
//
//        var reviewImages = reviewImageRepository.findByReviewId(reviewId);
//        for (ReviewImage reviewImage : reviewImages) {
//            reviewImageRepository.delete(reviewImage);
//        }
//        reviewRepository.delete(review);
//        log.info("Actionlog.deleteReview.end : reviewId={}", reviewId);
//    }

}
