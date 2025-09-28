package org.example.trendyolfinalproject.dao.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "deleted_messages")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DeletedMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private GroupMessage message;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private GroupMember member;


    public DeletedMessage(GroupMember member, Object message) {
        this.member = member;
        this.message = (GroupMessage) message;
    }
}

