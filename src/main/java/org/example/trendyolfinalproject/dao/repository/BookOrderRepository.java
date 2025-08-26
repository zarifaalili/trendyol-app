package org.example.trendyolfinalproject.dao.repository;

import org.example.trendyolfinalproject.dao.entity.BookOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface BookOrderRepository extends JpaRepository<BookOrder, Long> {
    Optional<BookOrder> findByIdAndUserIdAndIsPaidTrue(Long orderId, Long userId);
}

