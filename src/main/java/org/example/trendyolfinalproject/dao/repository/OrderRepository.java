package org.example.trendyolfinalproject.dao.repository;

import org.example.trendyolfinalproject.dao.entity.Order;
import org.example.trendyolfinalproject.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select max(o.trackingNumber) from Order o")
    String findMaxTrackingNumber();


    List<Order> findByUserId_Id(Long userId);

    List<Order> findByOrderDate(LocalDateTime orderDate);

    List<Order> findByOrderDateBetween(LocalDateTime orderDateAfter, LocalDateTime orderDateBefore);

    List<Order> findByStatus(Status status);


    @Query("""
                SELECT DISTINCT oi.orderId
                FROM OrderItem oi
                WHERE oi.productVariantId.product.seller.id = :sellerId
            """)
    List<Order> findOrdersBySellerId(@Param("sellerId") Long sellerId);


    @Query("""
                SELECT SUM(oi.unitPrice * oi.quantity)
                FROM OrderItem oi
                WHERE oi.productVariantId.product.seller.id = :sellerId
            """)
    BigDecimal getTotalRevenueBySeller(@Param("sellerId") Long sellerId);

    @Query("""
                SELECT COUNT(DISTINCT oi.orderId.id)
                FROM OrderItem oi
                WHERE oi.productVariantId.product.seller.id = :sellerId
            """)
    Long getTotalOrdersBySeller(@Param("sellerId") Long sellerId);


    @Query("SELECT COUNT(DISTINCT o.user.id) FROM Order o WHERE o.orderDate >= :since")
    Long countActiveUsers(@Param("since") LocalDateTime since);


    long countById(Long id);

    long countByUser_Id(Long userId);
}
