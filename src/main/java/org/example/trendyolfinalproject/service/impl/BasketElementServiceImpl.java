package org.example.trendyolfinalproject.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.repository.BasketElementRepository;
import org.example.trendyolfinalproject.dao.repository.BasketRepository;
import org.example.trendyolfinalproject.dao.repository.ProductRepository;
import org.example.trendyolfinalproject.dao.repository.ProductVariantRepository;
import org.example.trendyolfinalproject.exception.customExceptions.NotFoundException;
import org.example.trendyolfinalproject.mapper.BasketElementMapper;
import org.example.trendyolfinalproject.model.request.BasketElementRequest;
import org.example.trendyolfinalproject.model.request.DeleteBasketElementRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.BasketElementResponse;
import org.example.trendyolfinalproject.service.BasketElementService;
import org.example.trendyolfinalproject.service.BasketService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasketElementServiceImpl implements BasketElementService {

    private final BasketElementRepository basketElementRepository;
    private final BasketRepository basketRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final BasketElementMapper basketElementMapper;
    private final BasketService basketService;

    @Override
    public ApiResponse<BasketElementResponse> createBasketElement(BasketElementRequest request) {

        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            throw new RuntimeException("Firstly you should login");
        }
        var basket = basketRepository.findByUserId(currentUserId).orElseThrow(() -> new NotFoundException("Basket not found with User id: " + currentUserId));

        log.info("Actionlog.createBasketElement.start :  basketId={}", basket.getId());
        var productVariant = productVariantRepository.findById(request.getProductVariantId()).orElseThrow(
                () -> new NotFoundException("ProductVariant not found with id: " + request.getProductVariantId()));
        var productId = productVariant.getProduct().getId();

        var product = productRepository.findById(productId).orElseThrow(
                () -> new NotFoundException("Product not found with id: " + productId));

        updateFinalAndDiscountAmount();
        var existingElement = basketElementRepository.findByBasket_IdAndProductId_IdAndProductVariantId_Id(basket.getId(), productId, request.getProductVariantId()).orElse(null);

        if (productVariant.getStockQuantity() == 0) {
            throw new RuntimeException("ProductVariant is out of stock");
        }

        if (existingElement != null) {
            existingElement.setQuantity(existingElement.getQuantity() + 1);
            var updated = basketElementRepository.save(existingElement);
            var response = basketElementMapper.toResponse(updated);
            product.setStockQuantity(product.getStockQuantity() - 1);
            productRepository.save(product);
            productVariant.setStockQuantity(productVariant.getStockQuantity() - 1);
            productVariantRepository.save(productVariant);

            updateFinalAndDiscountAmount();

            basket.setUpdatedAt(LocalDateTime.now());
            basketRepository.save(basket);

            log.info("Actionlog.createBasketElement.end : basketId={}", basket.getId());
            return ApiResponse.<BasketElementResponse>builder()
                    .status(200)
                    .message("Basket element created successfully")
                    .data(response)
                    .build();
        }

        var entity = basketElementMapper.toEntity(request);
        entity.setBasket(basket);
        entity.setProductId(product);
        entity.setProductVariantId(productVariant);
        entity.setQuantity(1);
        var saved = basketElementRepository.save(entity);
        var response = basketElementMapper.toResponse(saved);

        product.setStockQuantity(product.getStockQuantity() - 1);
        productRepository.save(product);
        productVariant.setStockQuantity(productVariant.getStockQuantity() - 1);
        productVariantRepository.save(productVariant);
        updateFinalAndDiscountAmount();

        basket.setUpdatedAt(LocalDateTime.now());
        basketRepository.save(basket);

        log.info("Actionlog.createBasketElement.end : basketId={}", basket.getId());
        return ApiResponse.<BasketElementResponse>builder()
                .status(200)
                .message("Basket element created successfully")
                .data(response)
                .build();
    }

    @Transactional
    @Override
    public ApiResponse<String> deleteBasketElement(DeleteBasketElementRequest request) {

        Long currentUserId = getCurrentUserId();
        var basket = basketRepository.findByUserId(currentUserId).orElseThrow(() -> new NotFoundException("Basket not found with User id: " + currentUserId));
        log.info("Actionlog.deleteBasketElement.start : basketId={}", basket.getId());
        var basketElementId = request.getBasketElementId();
        var basketElement = basketElementRepository.findById(basketElementId).orElseThrow(() -> new NotFoundException("Element not found"));
        var product = basketElement.getProductId();
        var productVariant = basketElement.getProductVariantId();

        var exitingElement = basketElementRepository.findByBasket_IdAndProductId_IdAndProductVariantId_Id(basket.getId(), product.getId(), productVariant.getId()).orElseThrow(
                () -> new RuntimeException("Element not found"));
        basketElementRepository.delete(exitingElement);

        updateFinalAndDiscountAmount();

        product.setStockQuantity(product.getStockQuantity() + exitingElement.getQuantity());
        productRepository.save(product);
        productVariant.setStockQuantity(productVariant.getStockQuantity() + exitingElement.getQuantity());
        productVariantRepository.save(productVariant);

        log.info("Actionlog.deleteBasketElement.end : basketId={}", basket.getId());

        return ApiResponse.<String>builder()
                .status(200)
                .message("Basket element deleted successfully")
                .data("Deleted element id: " + request.getBasketElementId())
                .build();
    }

    @Override
    public ApiResponse<BasketElementResponse> decrieceQuantity(DeleteBasketElementRequest request) {
        Long currentUserId = getCurrentUserId();
        var basket = basketRepository.findByUserId(currentUserId).orElseThrow(() -> new NotFoundException("Basket not found with User id: " + currentUserId));
        log.info("Actionlog.decreaseQuantity.start : basketId={}", basket.getId());

        var basketElementId = request.getBasketElementId();
        var basketElement = basketElementRepository.findById(basketElementId).orElseThrow(() -> new NotFoundException("Element not found"));
        var product = basketElement.getProductId();
        var productVariant1 = basketElement.getProductVariantId();
        var exitingElement = basketElementRepository.findByBasket_IdAndProductId_IdAndProductVariantId_Id(basket.getId(), product.getId(), productVariant1.getId()).orElseThrow(
                () -> new RuntimeException("Element not found"));

        var productVariant = productVariantRepository.findById(productVariant1.getId()).orElseThrow(() -> new NotFoundException("ProductVariant not found with id: " + productVariant1.getId()));

        if (exitingElement.getQuantity() == 1) {
            throw new RuntimeException("if quantity is 1 it cannot be decreased");
        }
        exitingElement.setQuantity(exitingElement.getQuantity() - 1);
        var updated = basketElementRepository.save(exitingElement);
        var response = basketElementMapper.toResponse(updated);
        product.setStockQuantity(product.getStockQuantity() + 1);
        productRepository.save(product);
        productVariant.setStockQuantity(productVariant.getStockQuantity() + 1);
        productVariantRepository.save(productVariant);

        updateFinalAndDiscountAmount();

        log.info("Actionlog.decreaseQuantity.end : basketId={}", basket.getId());
        return ApiResponse.<BasketElementResponse>builder()
                .status(200)
                .message("Basket element quantity decreased successfully")
                .data(response)
                .build();
    }


    public void updateFinalAndDiscountAmount() {
        Long currentUserId = getCurrentUserId();
        var basket = basketRepository.findByUserId(currentUserId).orElseThrow(() -> new NotFoundException("Basket not found with User id: " + currentUserId));
        BigDecimal finalAmount = basketService.calculateRawTotalAmount().getData();
        basket.setFinalAmount(finalAmount);
        if (basket.getDiscountAmount() == null) {
            basket.setDiscountAmount(BigDecimal.ZERO);
        }
        basketRepository.save(basket);
    }

    @Override
    public ApiResponse<List<BasketElementResponse>> getBasketElements() {
        log.info("Actionlog.getBasketElements.start");
        Long currentUserId = getCurrentUserId();
        var basket = basketRepository.findByUserId(currentUserId).orElseThrow(() -> new NotFoundException("Basket not found with User id: " + currentUserId));
        var basketElements = basketElementRepository.findByBasket_Id(basket.getId());
        var response = basketElementMapper.toResponseList(basketElements);
        log.info("Actionlog.getBasketElements.end");
        return ApiResponse.<List<BasketElementResponse>>builder()
                .status(200)
                .message("Basket elements retrieved successfully")
                .data(response)
                .build();
    }

    private Long getCurrentUserId() {
        return (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId");
    }

}
