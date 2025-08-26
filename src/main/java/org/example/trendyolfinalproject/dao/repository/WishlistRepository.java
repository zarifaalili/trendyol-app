package org.example.trendyolfinalproject.dao.repository;

import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.dao.entity.WishList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<WishList, Long> {
    Optional<WishList> findByProductVariant_Id(Long id);

    List<WishList> findByUser(User user);
    Optional<Object> findByUserAndProductVariant_Id(User user, Long productVariantId);

    List<WishList> findByProductVariant_Product_NameContainingIgnoreCase(String productName);


    @Query("SELECT w FROM WishList w " +
            "WHERE w.productVariant.product.previousPrice IS NOT NULL " +
            "AND w.productVariant.product.price < w.productVariant.product.previousPrice " +
            "ORDER BY w.productVariant.product.price ASC")
    List<WishList> getProductVariantsByDecreasedCost();


}
