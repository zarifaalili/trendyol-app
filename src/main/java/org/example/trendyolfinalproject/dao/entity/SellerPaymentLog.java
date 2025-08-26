package org.example.trendyolfinalproject.dao.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class SellerPaymentLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate paymentDate;
    private Long adminId;

    @ManyToOne
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;


    @ManyToOne
    @JoinColumn(name = "seller_id")
    private Seller seller;
}
