package org.example.trendyolfinalproject.dao.repository;

import org.example.trendyolfinalproject.dao.entity.PaymentMethod;
import org.example.trendyolfinalproject.dao.entity.PaymentTransaction;
import org.example.trendyolfinalproject.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    PaymentTransaction findByOrder_Id(Long id);
    List<PaymentTransaction>  findAllByStatus(Status status);

    List<PaymentTransaction> findByPayment(PaymentMethod payment);
}
