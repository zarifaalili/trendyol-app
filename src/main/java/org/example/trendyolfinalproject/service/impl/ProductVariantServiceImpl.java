package org.example.trendyolfinalproject.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.entity.Category;
import org.example.trendyolfinalproject.dao.entity.Product;
import org.example.trendyolfinalproject.dao.entity.ProductImage;
import org.example.trendyolfinalproject.dao.entity.ProductVariant;
import org.example.trendyolfinalproject.dao.repository.ProductRepository;
import org.example.trendyolfinalproject.dao.repository.ProductVariantRepository;
import org.example.trendyolfinalproject.dao.repository.SellerRepository;
import org.example.trendyolfinalproject.dao.repository.UserRepository;
import org.example.trendyolfinalproject.exception.customExceptions.AlreadyException;
import org.example.trendyolfinalproject.exception.customExceptions.NotFoundException;
import org.example.trendyolfinalproject.mapper.ProductVariantMapper;
import org.example.trendyolfinalproject.model.enums.NotificationType;
import org.example.trendyolfinalproject.model.request.ProductVariantCreateRequest;
import org.example.trendyolfinalproject.model.request.ProductVariantFilterRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.ProductVariantDetailResponse;
import org.example.trendyolfinalproject.model.response.ProductVariantResponse;
import org.example.trendyolfinalproject.service.*;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;
    private final ProductVariantMapper productVariantMapper;
    private final AuditLogService auditLogService;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final EntityManager entityManager;
    private final RecommendationService recommendationService;
    private final FileStorageService fileStorageService;
    private final SellerRepository sellerRepository;

    @Transactional
    @Override
    public ApiResponse<ProductVariantResponse> createProductVariant(ProductVariantCreateRequest request) {
        log.info("Actionlog.createProductVariant.start : productId={}", request.getProductId());
        var user = getCurrentUserId();
        var user1 = userRepository.findById(user).orElseThrow(() -> new RuntimeException("User not found"));
        var product = productRepository.findById(request.getProductId()).orElseThrow(
                () -> new NotFoundException("Product not found with id: " + request.getProductId())
        );
        var relatedProduct = product.getSeller().getUser().getId();

        if (!user.equals(relatedProduct)) {
            throw new RuntimeException("You are not authorized to created this product");
        }
        var existingVariant = productVariantRepository.findBySku(request.getSku()).orElse(null);
        if (existingVariant != null) {
            throw new AlreadyException("ProductVariant already exists with sku: " + request.getSku());
        }
        var productVariant = productVariantMapper.toEntity(request);
        productVariant.setProduct(product);

        var saved = productVariantRepository.save(productVariant);
        product.setStockQuantity(product.getStockQuantity() + request.getStockQuantity());
        productRepository.save(product);
        var response = productVariantMapper.toResponse(saved);
        notificationService.sendToAllCustomers("There is a new product variant : " + saved.getSku(), NotificationType.PRODUCT, saved.getId());
        auditLogService.createAuditLog(user1, "Create ProductVariant", "ProductVariant created successfully. ProductVariant id: " + saved.getId());
        log.info("Actionlog.createProductVariant.end : productId={}", request.getProductId());
        return ApiResponse.<ProductVariantResponse>builder()
                .status(200)
                .message("Product variant created successfully")
                .data(response)
                .build();
    }

    @Override
    public ApiResponse<Void> deleteProductVariant(Long id) {
        log.info("Actionlog.deleteProductVariant.start : id={}", id);
        var user = getCurrentUserId();
        var productVariant = productVariantRepository.findById(id).orElseThrow(() -> new RuntimeException("ProductVariant not found with id: " + id));
        var product = productVariant.getProduct();
        if (product.getSeller().getId().equals(user)) {
            throw new RuntimeException("You are not authorized to update this product");
        }
        product.setStockQuantity(product.getStockQuantity() - productVariant.getStockQuantity());
        productRepository.save(product);
        productVariantRepository.delete(productVariant);
        log.info("Actionlog.deleteProductVariant.end : id={}", id);

        auditLogService.createAuditLog(userRepository.findById(user).orElseThrow(() -> new RuntimeException("User not found")), "Delete ProductVariant", "ProductVariant deleted successfully. ProductVariant id: " + id);
        productVariantMapper.toResponse(productVariant);
        return ApiResponse.<Void>builder()
                .status(200)
                .message("Product variant deleted successfully")
                .data(null)
                .build();
    }

    @Override
    public ApiResponse<ProductVariantResponse> getProductVariant(Long id) {
        log.info("Actionlog.getProductVariant.start : id={}", id);
        var productVariant = productVariantRepository.findById(id).orElseThrow(() -> new RuntimeException("ProductVariant not found with id: " + id));
        recommendationService.saveUserView(getCurrentUserId(), productVariant.getId());
        log.info("Actionlog.getProductVariant.end : id={}", id);
        var mapper = productVariantMapper.toResponse(productVariant);
        return ApiResponse.<ProductVariantResponse>builder()
                .status(200)
                .message("Product variant retrieved successfully")
                .data(mapper)
                .build();
    }

    @Override
    public ApiResponse<ProductVariantDetailResponse> getProductVariantDetails(Long id) {
        var productVariant = productVariantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProductVariant not found with id: " + id));

        List<String> imageUrls = productVariant.getVariantImages()
                .stream()
                .map(ProductImage::getImageUrl)
                .toList();

        List<String> imageBase64s = productVariant.getVariantImages()
                .stream()
                .map(img -> encodeImageToBase64(img.getImageUrl()))
                .toList();

        var response = new ProductVariantDetailResponse(
                productVariant.getId(),
                productVariant.getProduct().getId(),
                productVariant.getColor(),
                productVariant.getSize(),
                productVariant.getAttributeValue1(),
                productVariant.getAttributeValue2(),
                productVariant.getStockQuantity(),
                productVariant.getSku(),
                productVariant.getProduct().getPrice().doubleValue(),
                productVariant.getProduct().getPreviousPrice().doubleValue(),
                imageUrls,
                imageBase64s
        );

        return ApiResponse.<ProductVariantDetailResponse>builder()
                .status(200)
                .message("Product variant details retrieved successfully")
                .data(response)
                .build();
    }

    private String encodeImageToBase64(String imagePath) {
        try {
            Path path = Paths.get(System.getProperty("user.dir") + imagePath);
            byte[] bytes = Files.readAllBytes(path);
            return Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            throw new RuntimeException("Could not read image: " + imagePath, e);
        }
    }

    @Override
    public ApiResponse<List<ProductVariantResponse>> getUserRecommendations() {
        var userId = getCurrentUserId();
        var ids = recommendationService.getLastViewedIds(userId);
        var response = ids.getData().stream()
                .map(pid -> productVariantRepository.findById(pid)
                        .map(productVariantMapper::toResponse)
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();

        return ApiResponse.<List<ProductVariantResponse>>builder()
                .status(200)
                .message("User recommendations retrieved successfully")
                .data(response)
                .build();
    }

    @Override
    public ApiResponse<List<ProductVariantResponse>> getProductVariantsByFilter(ProductVariantFilterRequest filter) {
        log.info("Actionlog.getProductVariantsByFilter.start : ");

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductVariant> query = cb.createQuery(ProductVariant.class);
        Root<ProductVariant> variantRoot = query.from(ProductVariant.class);

        Join<ProductVariant, Product> productJoin = variantRoot.join("product", JoinType.INNER);
        Join<Product, Category> categoryJoin = productJoin.join("category", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        if (filter.getColor() != null) {
            predicates.add(cb.equal(variantRoot.get("color"), filter.getColor()));
        }
        if (filter.getSize() != null) {
            predicates.add(cb.equal(variantRoot.get("size"), filter.getSize()));
        }
        if (filter.getCategoryId() != null) {
            predicates.add(cb.equal(categoryJoin.get("id"), filter.getCategoryId()));
        }
        if (filter.getMinPrice() != null) {
            predicates.add(cb.greaterThanOrEqualTo(productJoin.get("price"), filter.getMinPrice()));
        }
        if (filter.getMaxPrice() != null) {
            predicates.add(cb.lessThanOrEqualTo(productJoin.get("price"), filter.getMaxPrice()));
        }
        predicates.add(cb.greaterThan(variantRoot.get("stockQuantity"), 0));
        query.select(variantRoot).where(cb.and(predicates.toArray(new Predicate[0])));
        List<ProductVariant> variants = entityManager.createQuery(query).getResultList();

        if (variants.isEmpty()) {
            throw new NotFoundException("ProductVariants not found");
        }
        var responseList = variants.stream()
                .map(productVariantMapper::toResponse)
                .toList();

        log.info("Actionlog.getProductVariantsByFilter.end : ");
        return ApiResponse.<List<ProductVariantResponse>>builder()
                .status(200)
                .message("Product variants filtered successfully")
                .data(responseList)
                .build();
    }

    @Transactional
    @Override
    public ApiResponse<ProductVariantResponse> addImages(Long variantId, List<MultipartFile> images) {
        var variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found"));
        var userId = getCurrentUserId();
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        if(!variant.getProduct().getSeller().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You cannot add images to your other product");
        }
        int displayOrder = variant.getVariantImages() != null ? variant.getVariantImages().size() + 1 : 1;
        boolean isFirstImage = variant.getVariantImages() == null || variant.getVariantImages().isEmpty();

        List<ProductImage> variantImages = variant.getVariantImages() != null ?
                variant.getVariantImages() : new ArrayList<>();
        for (MultipartFile image : images) {
            String imageUrl = fileStorageService.storeFile(image);
            ProductImage productImage = new ProductImage();
            productImage.setImageUrl(imageUrl);
            productImage.setProductVariant(variant);
            productImage.setIsMainImage(isFirstImage);
            productImage.setDisplayOrder(displayOrder++);
            variantImages.add(productImage);
            isFirstImage = false;
        }

        variant.setVariantImages(variantImages);
        var saved = productVariantRepository.save(variant);
        var mapper = productVariantMapper.toResponse(saved);
        return ApiResponse.<ProductVariantResponse>builder()
                .status(200)
                .message("Images added successfully to product variant")
                .data(mapper)
                .build();
    }

    @Override
    public ApiResponse<ProductVariantResponse> updateProductVariantStock(Long productVariantId, Integer newStock) {
        log.info("Actionlog.updateProductVariantStock.start : productId={}", productVariantId);
        var currentUserId = getCurrentUserId();
        var seller = sellerRepository.findByUserId(currentUserId).orElseThrow(() -> new NotFoundException("Seller not found with userId: " + currentUserId));
        var productVariant = productVariantRepository.findById(productVariantId).orElseThrow(() -> new RuntimeException("Product not found with id: " + productVariantId));
        var product = productVariant.getProduct();
        var oldStock = productVariant.getStockQuantity();
        var seller1 = productVariant.getProduct().getSeller();
        if (!seller.getId().equals(seller1.getId())) {
            throw new RuntimeException("You are not authorized to update this product");
        }
        productVariant.setStockQuantity(newStock);
        if (newStock > oldStock) {
            product.setStockQuantity(product.getStockQuantity() + newStock - oldStock);
        } else {
            product.setStockQuantity(product.getStockQuantity() - oldStock - newStock);
        }
        var updated = productVariantRepository.save(productVariant);
        var response = productVariantMapper.toResponse(updated);
        auditLogService.createAuditLog(seller.getUser(), "Update product variant stock", "Update product variant stock successfully. Product id: " + updated.getId());
        if (oldStock < newStock) {
            notificationService.sendToAllUsers("HURRY UP Product stock increased", NotificationType.PRODUCT_STOCK_UPDATE, updated.getId());
        }
        log.info("Actionlog.updateProductVariantStock.end : productId={}", productVariantId);
        return ApiResponse.<ProductVariantResponse>builder()
                .status(200)
                .message("Product variant stock updated successfully")
                .data(response)
                .build();
    }

    private Long getCurrentUserId() {
        return (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId");
    }
    }
