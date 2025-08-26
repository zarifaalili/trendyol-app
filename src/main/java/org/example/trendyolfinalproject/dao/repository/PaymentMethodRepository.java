package org.example.trendyolfinalproject.dao.repository;

import org.example.trendyolfinalproject.dao.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    Optional<PaymentMethod> findByCardNumber(String cardNumber);
    List<PaymentMethod> findByUserId_Id(Long userId);

    Optional<PaymentMethod> findByUserId_IdAndIsDefault(Long userId, Boolean isDefault);
    boolean existsByUserId_Id(Long userId);

}
