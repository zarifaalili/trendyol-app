package org.example.trendyolfinalproject.dao.repository;

import org.example.trendyolfinalproject.dao.entity.ChatGroup;
import org.example.trendyolfinalproject.dao.entity.GroupMember;
import org.example.trendyolfinalproject.dao.entity.GroupMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupMessageRepository extends JpaRepository<GroupMessage, Long> {
    Page<GroupMessage> findByGroupAndDeletedForAllFalse(ChatGroup group, Pageable pageable);

    Optional<GroupMessage> findByIdAndGroup(Long id, ChatGroup group);


    @Query("""
    SELECT m FROM GroupMessage m
    WHERE m.group = :group
      AND m.deletedForAll = false
      AND NOT EXISTS (
          SELECT 1 FROM DeletedMessage dm
          WHERE dm.member = :member AND dm.message = m
      )
    """)
    Page<GroupMessage> findVisibleMessages(@Param("group") ChatGroup group,
                                           @Param("member") GroupMember member,
                                           Pageable pageable);


    Optional<GroupMessage> findByPinnedAndGroup(Boolean pinned, ChatGroup group);
}
