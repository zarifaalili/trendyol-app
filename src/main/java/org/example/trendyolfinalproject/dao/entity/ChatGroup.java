package org.example.trendyolfinalproject.dao.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import org.example.trendyolfinalproject.model.enums.GroupPermission;
import org.example.trendyolfinalproject.model.enums.GroupVisibility;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Entity
@Table(name = "chat_groups")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChatGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    private GroupVisibility visibility = GroupVisibility.PUBLIC;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMessage> messages = new ArrayList<>();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GroupPermission addMemberPermissions = GroupPermission.ALL_MEMBERS;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GroupPermission sendMessagePermissions = GroupPermission.ALL_MEMBERS;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GroupPermission settingPermissions = GroupPermission.ALL_MEMBERS;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatGroup chatGroup = (ChatGroup) o;
        return Objects.equals(id, chatGroup.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
