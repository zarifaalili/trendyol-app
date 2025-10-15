package org.example.trendyolfinalproject.dao.repository;

import org.example.trendyolfinalproject.dao.entity.Product;
import org.example.trendyolfinalproject.model.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("select p from Product p where lower(p.name) like lower(concat('%', :name, '%') ) ")
    List<Product> findbyName(String name);

    @Query("select p from Product p left join p.variants v" +
            " GROUP BY p.id having sum(v.stockQuantity) = 0")
    List<Product> findAllOutOfStockProducts();

    List<Product> findBySellerId(Long sellerId);

    Optional<Product> findByIdAndStatus(Long id, Status status);

    Page<Product> findAllByStatus(Status status, Pageable pageable);


    @Query("SELECT DISTINCT p FROM Product p JOIN OrderItem oi ON p.id = oi.productId.id " +
            "WHERE p.seller.id = :sellerId AND oi.createdAt BETWEEN :startDate AND :endDate")
    Page<Product> findSellerProductsBetweenDates(@Param("sellerId") Long sellerId,
                                                 @Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate,
                                                 Pageable pageable);
}
