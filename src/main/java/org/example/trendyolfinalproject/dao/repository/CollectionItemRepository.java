package org.example.trendyolfinalproject.dao.repository;

import org.example.trendyolfinalproject.dao.entity.CollectionItem;
import org.example.trendyolfinalproject.dao.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CollectionItemRepository extends JpaRepository<CollectionItem, Long> {

    Optional<CollectionItem> findByProductVariant_IdAndCollection_Id(Long productId, Long collectionId);

    List<CollectionItem> findByCollection_Id(Long collectionId);

    void deleteAllByProductVariant(ProductVariant productVariant);

    void deleteAllByProductVariant_Id(Long productVariantId);
}
