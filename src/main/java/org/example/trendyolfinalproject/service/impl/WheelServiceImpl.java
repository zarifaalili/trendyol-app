package org.example.trendyolfinalproject.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.entity.BasketElement;
import org.example.trendyolfinalproject.dao.entity.UserWheel;
import org.example.trendyolfinalproject.dao.repository.*;
import org.example.trendyolfinalproject.exception.customExceptions.NotFoundException;
import org.example.trendyolfinalproject.mapper.WheelMapper;
import org.example.trendyolfinalproject.model.enums.NotificationType;
import org.example.trendyolfinalproject.model.request.WheelRequest;
import org.example.trendyolfinalproject.model.response.SpinWheelResponse;
import org.example.trendyolfinalproject.model.response.UserWheelResponse;
import org.example.trendyolfinalproject.service.AuditLogService;
import org.example.trendyolfinalproject.service.NotificationService;
import org.example.trendyolfinalproject.service.WheelService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class WheelServiceImpl implements WheelService {
    private final WheelRepository wheelRepository;
    private final UserWheelRepository userWheelRepository;
    private final UserRepository userRepository;
    private final WheelMapper wheelMapper;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;
    private final BasketRepository basketRepository;
    private final BasketElementRepository basketElementRepository;

    @Transactional
    @Override
    public void createWheel(WheelRequest wheelRequest) {
        log.info("ActionLog.createWheel.start : userId={}", getCurrentUserId());
        var userId = getCurrentUserId();
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        var wheel = wheelMapper.toEntity(wheelRequest);

        var prizes = wheelRequest.getPrizes().stream()
                .map(prizeRequest -> {
                    var prize = wheelMapper.toEntity(prizeRequest);
                    prize.setWheel(wheel);
                    return prize;
                })
                .toList();

        wheel.setPrizes(prizes);

        wheelRepository.save(wheel);
        auditLogService.createAuditLog(user, "createWheel", "Wheel created");
        notificationService.sendToAllUsers("New wheel created", NotificationType.WHEEL_CREATED, wheel.getId());

        log.info("ActionLog.createWheel.end : wheelId={}, prizesCount={}", wheel.getId(), prizes.size());
    }


    @Override
    public SpinWheelResponse spinWheel(Long wheelId) {
        log.info("ActionLog.spinWheel.start : userId={}, wheelId={}", getCurrentUserId(), wheelId);
        var userId = getCurrentUserId();
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        var wheel = wheelRepository.findById(wheelId)
                .orElseThrow(() -> new NotFoundException("Wheel not found"));

        var existingUserWheel = userWheelRepository.findByUserAndWheel(user, wheel)
                .orElse(null);

        if (existingUserWheel != null) {
            throw new RuntimeException("User already spun this wheel");
        }
        var prices = wheel.getPrizes();
        var randomIndex = ThreadLocalRandom.current().nextInt(prices.size());
        var selectedPrice = prices.get(randomIndex);

        UserWheel wheelOfUser = existingUserWheel;

        if (wheelOfUser == null) {
            wheelOfUser = new UserWheel();
            wheelOfUser.setUser(user);
            wheelOfUser.setWheel(wheel);
            wheelOfUser.setStartedAt(LocalDateTime.now());
            wheelOfUser.setPrize(selectedPrice);
            wheelOfUser.setExpiresAt(wheel.getEndTime());
            userWheelRepository.save(wheelOfUser);
        }
        var response = wheelMapper.toResponse(wheelOfUser);
        log.info("ActionLog.spinWheel.end : userId={}, wheelId={}", userId, wheelId);
        auditLogService.createAuditLog(user, "spinWheel", "Wheel spun");
        return response;
    }

    @Override
    public Map<String, Long> getTimeLeft() {
        log.info("ActionLog.getTimeLeft.start : userId={}", getCurrentUserId());
        var userId = getCurrentUserId();
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        var userWheel = userWheelRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("User wheel not found"));
        var expiresAtInstant = userWheel.getExpiresAt().atZone(ZoneId.systemDefault()).toInstant();

        long millisLeft = expiresAtInstant.toEpochMilli() - System.currentTimeMillis();
        if (millisLeft < 0) millisLeft = 0;
        Duration duration = Duration.ofMillis(millisLeft);
        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        log.info("ActionLog.getTimeLeft.end : userId={}, days={}, hours={}, minutes={}, seconds={}",
                userId, days, hours, minutes, seconds);

        return Map.of(
                "days", days,
                "hours", hours,
                "minutes", minutes,
                "seconds", seconds
        );
    }


    @Transactional
    @Override
    public void useWheelPrice(Long userWheelId) {
        log.info("ActionLog.useWheelPrice.start : userWheelId={}", userWheelId);
        var user = userRepository.findById(getCurrentUserId()).orElseThrow(() -> new NotFoundException("User not found"));
        var userWheel = userWheelRepository.findById(userWheelId).orElseThrow(() -> new NotFoundException("User wheel not found"));
        if (!userWheel.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only use your own wheel");
        }
        if (userWheel.getUsedAt() != null) {
            throw new RuntimeException("You have already used this wheel");
        }
        if (userWheel.getExpiresAt() != null && userWheel.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("This wheel has expired");
        }
        var basket = basketRepository.findByUserId(getCurrentUserId()).orElseThrow(() -> new NotFoundException("Basket not found"));
        List<BasketElement> basketElements = basketElementRepository.findByBasket_Id(basket.getId());
        if (basketElements.isEmpty()) {
            throw new RuntimeException("Basket is empty");
        }
        BigDecimal total = BigDecimal.ZERO;
        for (BasketElement basketElement : basketElements) {
            total = total.add(
                    basketElement.getProductId().getPrice().multiply(BigDecimal.valueOf(basketElement.getQuantity()))
            );
        }

        var prize = userWheel.getPrize();
        if (prize == null) {
            throw new RuntimeException("No prize associated with this user wheel");
        }

        BigDecimal baseAmount;
        if (basket.getDiscountAmount() != null &&
                basket.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            baseAmount = basket.getFinalAmount();
        } else {
            baseAmount = total;
        }
        if (prize.getMinOrder() != null &&
                baseAmount.compareTo(prize.getMinOrder()) < 0) {
            BigDecimal minAdd = prize.getMinOrder().subtract(baseAmount);
            throw new RuntimeException("You need to add " + minAdd + " to your order to use this wheel");
        }
        BigDecimal discountAmount = prize.getAmount();
        if (discountAmount.compareTo(baseAmount) > 0) {
            discountAmount = baseAmount;
        }
        BigDecimal totalDiscount = basket.getDiscountAmount() != null
                ? basket.getDiscountAmount().add(discountAmount)
                : discountAmount;

        basket.setDiscountAmount(totalDiscount);
        basket.setFinalAmount(total.subtract(totalDiscount));
        basketRepository.save(basket);

        userWheel.setUsedAt(LocalDateTime.now());
        userWheelRepository.save(userWheel);

        auditLogService.createAuditLog(user, "useWheelPrice", "Wheel price used");
        log.info("ActionLog.useWheelPrice.end : userWheelId={}", userWheelId);
    }

    @Transactional
    @Override
    public void cancelWheelPrize(Long userWheelId) {
        log.info("ActionLog.cancelWheelPrize.start : userWheelId={}", userWheelId);
        var user = userRepository.findById(getCurrentUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        var userWheel = userWheelRepository.findById(userWheelId)
                .orElseThrow(() -> new NotFoundException("User wheel not found"));
        if (!userWheel.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only cancel your own wheel prize");
        }
        if (userWheel.getUsedAt() == null) {
            throw new RuntimeException("This wheel prize has not been used yet");
        }
        var basket = basketRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NotFoundException("Basket not found"));
        var prize = userWheel.getPrize();
        if (prize == null) {
            throw new RuntimeException("No prize associated with this user wheel");
        }
        List<BasketElement> basketElements = basketElementRepository.findByBasket_Id(basket.getId());
        BigDecimal total = basketElements.stream()
                .map(be -> be.getProductId().getPrice().multiply(BigDecimal.valueOf(be.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal currentDiscount = basket.getDiscountAmount() != null ? basket.getDiscountAmount() : BigDecimal.ZERO;
        BigDecimal updatedDiscount = currentDiscount.subtract(prize.getAmount());

        if (updatedDiscount.compareTo(BigDecimal.ZERO) < 0) {
            updatedDiscount = BigDecimal.ZERO;
        }

        basket.setDiscountAmount(updatedDiscount);
        basket.setFinalAmount(total.subtract(updatedDiscount));
        basketRepository.save(basket);
        userWheel.setUsedAt(null);
        userWheelRepository.save(userWheel);
        auditLogService.createAuditLog(user, "cancelWheelPrize", "Wheel prize cancelled");
        log.info("ActionLog.cancelWheelPrize.end : userWheelId={}", userWheelId);
    }

    @Override
    public List<UserWheelResponse> getAllWheels() {
        log.info("ActionLog.getAllWheels.start : userId={}", getCurrentUserId());
        var user = userRepository.findById(getCurrentUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        var userWheels = userWheelRepository.findByUser_IdAndUsedAtIsNull(getCurrentUserId());
        if (userWheels.isEmpty()) {
            throw new NotFoundException("No wheels found");
        }
        var responses = userWheels.stream()
                .map(wheelMapper::toUserWheelResponse)
                .toList();

        log.info("ActionLog.getAllWheels.end : userId={}", getCurrentUserId());
        return responses;
    }

    private Long getCurrentUserId() {
        return (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId");
    }
}

