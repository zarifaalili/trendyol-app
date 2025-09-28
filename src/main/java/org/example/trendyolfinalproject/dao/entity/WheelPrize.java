package org.example.trendyolfinalproject.dao.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "wheel_prizes")
@Getter
@Setter
public class WheelPrize {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private BigDecimal amount;
    private BigDecimal minOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wheel_id")
    private Wheel wheel;
}

