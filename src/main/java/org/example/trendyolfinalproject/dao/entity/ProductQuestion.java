package org.example.trendyolfinalproject.dao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.trendyolfinalproject.model.Status;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_questions")
public class ProductQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question;

    private String answer;

    @Enumerated(EnumType.STRING)
    private Status status; // PENDING, ANSWERED, REJECTED

    @ManyToOne
    private User customer;

    @ManyToOne
    private ProductVariant productVariant;

    @ManyToOne
    private Seller seller;

    private LocalDateTime createdAt;

    private LocalDateTime answeredAt;

}
