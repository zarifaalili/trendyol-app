package org.example.trendyolfinalproject.dao.repository;

import org.example.trendyolfinalproject.dao.entity.BasketElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface BasketElementRepository extends JpaRepository<BasketElement, Long> {
    Optional<BasketElement> findByBasket_IdAndProductId_IdAndProductVariantId_Id(Long basketId, Long productId, Long productVariantId);
    List<BasketElement> findByBasket_Id(Long basketId);
}
