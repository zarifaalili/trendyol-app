package org.example.trendyolfinalproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.entity.UserWheel;
import org.example.trendyolfinalproject.dao.repository.UserRepository;
import org.example.trendyolfinalproject.dao.repository.UserWheelRepository;
import org.example.trendyolfinalproject.dao.repository.WheelRepository;
import org.example.trendyolfinalproject.exception.customExceptions.NotFoundException;
import org.example.trendyolfinalproject.mapper.WheelMapper;
import org.example.trendyolfinalproject.model.enums.NotificationType;
import org.example.trendyolfinalproject.model.request.WheelRequest;
import org.example.trendyolfinalproject.model.response.SpinWheelResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class WheelService {
    private final WheelRepository wheelRepository;
    private final UserWheelRepository userWheelRepository;
    private final UserRepository userRepository;
    private final WheelMapper wheelMapper;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;


    @Transactional
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


    public Map<String, Long> getTimeLeft() {
        log.info("ActionLog.getTimeLeft.start : userId={}", getCurrentUserId());
        var userId = getCurrentUserId();
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        var userWheel = userWheelRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("User wheel not found"));
        var expiresAtInstant = userWheel.getExpiresAt().atZone(ZoneId.systemDefault()).toInstant();

        long millisLeft = expiresAtInstant.toEpochMilli() - System.currentTimeMillis();
        if (millisLeft < 0) millisLeft = 0; // keçibsə 0 göstər

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

    private Long getCurrentUserId() {
        return (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId");
    }
}

