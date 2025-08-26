package org.example.trendyolfinalproject.dao.repository;

import org.example.trendyolfinalproject.dao.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {
    boolean existsByCompanyName(String companyName);
    Optional<Seller> findFirstByCompanyName(String companyName);

    boolean existsByTaxId(Integer taxId);

    boolean existsByContactEmail(String contactEmail);
    Optional<Seller> findByUserId(Long userId);

}
