package org.example.trendyolfinalproject.dao.repository;

import org.example.trendyolfinalproject.dao.entity.Coupon;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.dao.entity.UserCoupon;
import org.example.trendyolfinalproject.dao.entity.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
    Optional<UserCoupon> findByUserAndCoupon(User user, Coupon coupon);


    @Query("SELECT SUM(uc.usageCount) FROM UserCoupon uc")
    BigDecimal getTotalCouponsUsed();
}
