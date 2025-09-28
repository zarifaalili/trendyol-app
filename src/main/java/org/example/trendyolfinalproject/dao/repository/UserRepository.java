package org.example.trendyolfinalproject.dao.repository;

import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.model.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByIdAndRole(Long id, Role role);

    List<User> findByRole(String role);

    @Query("select distinct w.user from WishList w where w.productVariant.id = :variantId")
    List<User> findAllByProductVariantIdInWishList(@Param("variantId") Long variantId);

    @Query("""
            select distinct be.basket.user
            from BasketElement be
            where be.productVariantId.id = :variantId
            """)
    List<User> findAllByProductVariantIdInBasket(@Param("variantId") Long variantId);

    @Query("SELECT u FROM User u WHERE u.role = 'ADMIN'")
    List<User> findAllAdmins();

    List<User> findAllByRole(Role role);

    @Query("SELECT u FROM User u LEFT JOIN Order o ON u.id = o.user.id WHERE o.id IS NULL")
    List<User> findAllWithoutOrders();


    @Query("SELECT u FROM User u WHERE (SELECT COUNT(o) FROM Order o WHERE o.user = u) = :orderCount")
    List<User> findAllByOrderCount(@Param("orderCount") Long orderCount);

    @Query("SELECT u FROM User u " +
            "WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(u.surname) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<User> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    Boolean existsUserByPhoneNumber(String phoneNumber);
}
