package org.example.trendyolfinalproject.dao.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "starred_group_message")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StarredGroupMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private GroupMessage message;

    @Column(nullable = false)
    private LocalDateTime starredAt = LocalDateTime.now();


}

