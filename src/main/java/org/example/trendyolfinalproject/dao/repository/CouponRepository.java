package org.example.trendyolfinalproject.dao.repository;

import org.example.trendyolfinalproject.dao.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByCode(String code);

    Optional<Coupon> findByIdAndIsActive(Long id, Boolean isActive);

    List<Coupon> findByIsActive(Boolean isActive);

    List<Coupon> findByEndDateBefore(LocalDateTime endDateBefore);

    List<Coupon> findAllByIsActiveTrueAndEndDateBefore(LocalDateTime now);

}
