package org.example.trendyolfinalproject.dao.repository;

import org.example.trendyolfinalproject.dao.entity.ChatGroup;
import org.example.trendyolfinalproject.dao.entity.GroupMember;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.model.enums.GroupRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    Optional<GroupMember> findByGroupAndUser(ChatGroup group, User user);

    Optional<GroupMember> findByGroupAndUser_Id(ChatGroup group, Long userId);

    List<GroupMember> findByGroup(ChatGroup group);

    List<GroupMember> findByGroupAndRole(ChatGroup group, GroupRole role);

    Optional<GroupMember> findTopByGroupAndRoleOrderByJoinedAtDesc(ChatGroup group, GroupRole role);

}
