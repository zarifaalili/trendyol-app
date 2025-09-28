package org.example.trendyolfinalproject.dao.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_wheels")
@Getter
@Setter
public class UserWheel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Wheel wheel;

    @ManyToOne(fetch = FetchType.LAZY)
    private WheelPrize prize;

    private LocalDateTime usedAt;
    private LocalDateTime startedAt;
    private LocalDateTime expiresAt;
}

