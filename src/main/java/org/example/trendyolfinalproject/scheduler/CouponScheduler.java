package org.example.trendyolfinalproject.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.entity.Coupon;
import org.example.trendyolfinalproject.dao.repository.CouponRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponScheduler {

    private final CouponRepository couponRepository;

    @Scheduled(cron = "*/30 * * * * *")
    @Transactional
    public void deactivateExpiredCoupons() {
        log.info("CouponScheduler: deactivateExpiredCoupons started at {}", LocalDateTime.now());
        LocalDateTime now = LocalDateTime.now();
        List<Coupon> expiredCoupons = couponRepository.findAllByIsActiveTrueAndEndDateBefore(now);

        if (!expiredCoupons.isEmpty()) {
            for (Coupon coupon : expiredCoupons) {
                coupon.setIsActive(false);
            }
            couponRepository.saveAll(expiredCoupons);

        }
        log.info("CouponScheduler: deactivateExpiredCoupons ended at {}", LocalDateTime.now());
    }
}
