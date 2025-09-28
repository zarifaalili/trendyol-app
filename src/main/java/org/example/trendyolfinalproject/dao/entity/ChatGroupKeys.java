package org.example.trendyolfinalproject.dao.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "chat_group_keys")
@Getter
@Setter
public class ChatGroupKeys {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false, unique = true)
    private ChatGroup group;

    @Column(name = "group_key", nullable = false)
    private String groupKey;
}
