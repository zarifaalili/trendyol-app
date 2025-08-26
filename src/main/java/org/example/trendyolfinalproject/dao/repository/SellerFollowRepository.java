package org.example.trendyolfinalproject.dao.repository;

import org.example.trendyolfinalproject.dao.entity.Seller;
import org.example.trendyolfinalproject.dao.entity.SellerFollow;
import org.example.trendyolfinalproject.dao.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SellerFollowRepository extends JpaRepository<SellerFollow, Long> {
    List<SellerFollow> findBySeller(Seller seller);

    List<SellerFollow> findByFollower(User follower);

    Optional<SellerFollow> findBySellerAndFollower(Seller seller, User follower);
}
