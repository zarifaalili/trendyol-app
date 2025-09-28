package org.example.trendyolfinalproject.dao.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.trendyolfinalproject.model.enums.MessageType;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "group_messages")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class GroupMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private ChatGroup group;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MessageType type = MessageType.TEXT;

    @Column(length = 4000)
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id")
    private ProductVariant productVariant;

    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt;

    @Column(nullable = false)
    private Boolean pinned = false;

    private Long forwardedFromMessageId;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Attachment> attachments = new HashSet<>();

    private Boolean edited = false;

    private Boolean deletedForAll = false;


    private Long deletedBy;


    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MessageReadStatus> readStatuses = new HashSet<>();


    @PrePersist
    protected void onCreate() {
        this.sentAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupMessage)) return false;
        GroupMessage message = (GroupMessage) o;
        return id != null && id.equals(message.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
