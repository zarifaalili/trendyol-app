package org.example.trendyolfinalproject.dao.repository;

import org.example.trendyolfinalproject.dao.entity.BasketElement;
import org.example.trendyolfinalproject.dao.entity.ProductVariant;
import org.example.trendyolfinalproject.dao.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BasketElementRepository extends JpaRepository<BasketElement, Long> {
    Optional<BasketElement> findByBasket_IdAndProductId_IdAndProductVariantId_Id(Long basketId, Long productId, Long productVariantId);
    List<BasketElement> findByBasket_Id(Long basketId);


    @Query("SELECT DISTINCT b.basket.user FROM BasketElement b " +
            "WHERE b.addedAt < :threshold")
    List<User> findAllUsersWithAbandonedBaskets(@Param("threshold") LocalDateTime threshold);

    void deleteAllByProductVariantId(ProductVariant productVariantId);

    void deleteAllByProductVariantId_Id(Long productVariantIdId);
}
