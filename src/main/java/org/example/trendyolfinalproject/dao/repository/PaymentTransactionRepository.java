package org.example.trendyolfinalproject.dao.repository;

import org.example.trendyolfinalproject.dao.entity.PaymentMethod;
import org.example.trendyolfinalproject.dao.entity.PaymentTransaction;
import org.example.trendyolfinalproject.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    PaymentTransaction findByOrder_Id(Long id);
    List<PaymentTransaction>  findAllByStatus(Status status);

    List<PaymentTransaction> findByPayment(PaymentMethod payment);
}
