package org.example.trendyolfinalproject.dao.repository;

import org.example.trendyolfinalproject.dao.entity.DeletedMessage;
import org.example.trendyolfinalproject.dao.entity.GroupMember;
import org.example.trendyolfinalproject.dao.entity.GroupMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeletedMessageRepository extends JpaRepository<DeletedMessage, Long> {
    Boolean existsByMemberAndMessage_Id(GroupMember member, Long messageId);

    List<DeletedMessage> findByMemberAndMessage(GroupMember member, GroupMessage message);
}
