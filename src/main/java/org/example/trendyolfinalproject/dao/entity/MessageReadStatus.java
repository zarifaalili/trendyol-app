package org.example.trendyolfinalproject.dao.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "message_read_status")
@Getter
@Setter
public class MessageReadStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private GroupMessage message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "read_timestamp")
    private LocalDateTime readTimestamp;

    @PreUpdate
    protected void onRead() {
        if (this.isRead) {
            this.readTimestamp = LocalDateTime.now();
        }
    }
}
