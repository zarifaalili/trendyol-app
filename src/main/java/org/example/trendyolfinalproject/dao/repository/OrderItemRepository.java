package org.example.trendyolfinalproject.dao.repository;

import org.example.trendyolfinalproject.dao.entity.Order;
import org.example.trendyolfinalproject.dao.entity.OrderItem;
import org.example.trendyolfinalproject.dao.entity.ProductVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId_Id(Long orderId);

    List<OrderItem> findByProductId_Id(Long productId);

    @Query("""
                select oi.orderId from OrderItem oi
                where lower(oi.productId.name) like lower(concat('%', :productName, '%') ) 
                and oi.orderId.user.id = :userId
            """)
    List<Order> findOrdersByUserIdAndProductName(Long userId, String productName);


    Page<OrderItem> findByCreatedAtBetweenAndProductId_Seller_Id(LocalDateTime createdAtAfter, LocalDateTime createdAtBefore, Long sellerId, Pageable pageable);

    @Query("SELECT SUM(oi.unitPrice * oi.quantity) FROM OrderItem oi")
    BigDecimal getTotalRevenue();

    @Query("""
                SELECT oi.productId.name 
                FROM OrderItem oi 
                GROUP BY oi.productId.id, oi.productId.name 
                ORDER BY SUM(oi.quantity) DESC
            """)
    List<String> findMostSoldProduct(Pageable pageable);

    // `OrderItemRepository` interfeysində
    @Query("""
       SELECT pv, SUM(oi.quantity) AS totalSales
       FROM OrderItem oi
       JOIN oi.productVariantId pv
       WHERE oi.createdAt >= :startDate
       GROUP BY pv
       ORDER BY totalSales DESC
    """)
    List<Object[]> findTrendingProductsWithSales(@Param("startDate") LocalDateTime startDate);

    @Query("""
       SELECT pv, SUM(oi.quantity) AS totalSales
       FROM OrderItem oi
       JOIN oi.productVariantId pv
       GROUP BY pv
       ORDER BY totalSales DESC
    """)
    List<Object[]> findDefaultTrendingProductsWithSales();
}
