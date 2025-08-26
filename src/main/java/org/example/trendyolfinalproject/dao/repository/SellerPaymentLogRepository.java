package org.example.trendyolfinalproject.dao.repository;

import org.example.trendyolfinalproject.dao.entity.Order;
import org.example.trendyolfinalproject.dao.entity.OrderItem;
import org.example.trendyolfinalproject.dao.entity.Seller;
import org.example.trendyolfinalproject.dao.entity.SellerPaymentLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface SellerPaymentLogRepository extends JpaRepository<SellerPaymentLog, Long> {
    boolean existsByPaymentDate(LocalDate date);
    boolean existsByOrderItemAndSeller(OrderItem orderItem, Seller seller);
}

