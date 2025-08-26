package org.example.trendyolfinalproject.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.entity.BasketElement;
import org.example.trendyolfinalproject.dao.repository.BasketElementRepository;
import org.example.trendyolfinalproject.dao.repository.BasketRepository;
import org.example.trendyolfinalproject.dao.repository.UserRepository;
import org.example.trendyolfinalproject.exception.customExceptions.NotFoundException;
import org.example.trendyolfinalproject.mapper.BasketMapper;
import org.example.trendyolfinalproject.response.ApiResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
@Slf4j
public class BasketService {
    private final BasketRepository basketRepository;
    private final UserRepository userRepository;
    private final BasketMapper basketMapper;
    private final BasketElementRepository basketElementRepository;
    private final AuditLogService auditLogService;
//    public BasketResponse getOrcreateBasket(BasketCreateRequest request) {
//
//        log.info("Actionlog.createBasket.start : userId={}", request.getUserId());
//        var user = userRepository.findById(request.getUserId()).orElseThrow(
//                () -> new RuntimeException("User not found with id: " + request.getUserId())
//        );
//
//        var basket = basketRepository.findByUserId(user.getId());
//        if (basket != null) {
//            log.info("Actionlog.getOrCreateBasket.exists : userId={}", request.getUserId());
//            return basketMapper.toResponse(basket);
//
//        }
//
//        Basket basket1 = new Basket();
//        basket1.setUser(user);
//        basket1.setCreatedAt(java.time.LocalDateTime.now());
//        basket1.setUpdatedAt(java.time.LocalDateTime.now());
//
//
//        var saved = basketRepository.save(basket1);
//        log.info("Actionlog.createBasket.created : userId={}", request.getUserId());
//        return basketMapper.toResponse(saved);
//
//    }


    public ApiResponse<BigDecimal> getTotalAmount() {
        Long currentUserId = (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId");
        var basket1 = basketRepository.findByUserId(currentUserId).orElseThrow(() -> new NotFoundException("Basket not found with User id: " + currentUserId));

        log.info("Actionlog.getTotalAmount.start : basketId={}", basket1.getId());


//        var basket = basketRepository.findById(basketId).orElseThrow(() -> new RuntimeException("Basket not found with id: " + basketId));
//        var basketElements = basketElementRepository.findByBasket_Id(basket.getId());

        BigDecimal currentTotal = basket1.getFinalAmount();
        if (currentTotal == null) {
            currentTotal = calculateRawTotalAmount().getData();
        }
//        BigDecimal total = BigDecimal.ZERO;
//        for (BasketElement basketElement : basketElements) {
//            BigDecimal price = basketElement.getProductId().getPrice();
//
//            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(basketElement.getQuantity()));
//            total = total.add(subtotal);
//
//        }

        var user = userRepository.findById(currentUserId).orElseThrow(() -> new NotFoundException("User not found with id: " + currentUserId));
        auditLogService.createAuditLog(user, "Get total amount of basket", "Get total price of basket successfully. Basket id: " + basket1.getId());

        log.info("Actionlog.getTotalAmount.end : basketId={}", basket1.getId());
        return ApiResponse.<BigDecimal>builder()
                .status(200)
                .message("Basket total retrieved successfully")
                .data(currentTotal)
                .build();
    }

    public ApiResponse<BigDecimal>  calculateRawTotalAmount() {
        Long currentUserId = (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId");
        var basket1 = basketRepository.findByUserId(currentUserId).orElseThrow(() -> new NotFoundException("Basket not found with User id: " + currentUserId));
        log.info("Actionlog.getTotalPrice.start : basketId={}", basket1.getId(), "userId=" + currentUserId);

        var basketElements = basketElementRepository.findByBasket_Id(basket1.getId());
        BigDecimal rawTotal = BigDecimal.ZERO;
        for (BasketElement basketElement : basketElements) {
            if (basketElement.getProductId() == null || basketElement.getProductId().getPrice() == null) {
                log.warn("Product price is null. Product id: " + basketElement.getProductId().getId());
                continue;
            }
            BigDecimal proce = basketElement.getProductId().getPrice();
            BigDecimal subtotal = proce.multiply(BigDecimal.valueOf(basketElement.getQuantity()));
            rawTotal = rawTotal.add(subtotal);
        }
        log.info("Actionlog.getTotalPrice.end : basketId={}", basket1.getId());
        return ApiResponse.<BigDecimal>builder()
                .status(200)
                .message("Basket raw total retrieved successfully")
                .data(rawTotal)
                .build();
    }

}
