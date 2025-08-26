package org.example.trendyolfinalproject.dao.repository;

import org.example.trendyolfinalproject.dao.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    Optional<ProductVariant> findBySku(String sku);

    @Query("SELECT pv FROM ProductVariant pv " +
            "JOIN pv.product p " +
            "WHERE p.category.id = :categoryId " +
            "AND p.status = 'ACTIVE' " +
            "AND EXISTS (SELECT 1 FROM ProductVariant pv2 WHERE pv2.product = p AND pv2.stockQuantity > 0) " +
            "AND p.price BETWEEN :minPrice AND :maxPrice " +
            "AND p.id <> :productId " +
            "AND pv.stockQuantity > 0")
    List<ProductVariant> findSimilarProductVariants(@Param("categoryId") Long categoryId,
                                                    @Param("minPrice") BigDecimal minPrice,
                                                    @Param("maxPrice") BigDecimal maxPrice,
                                                    @Param("productId") Long productId);

}
