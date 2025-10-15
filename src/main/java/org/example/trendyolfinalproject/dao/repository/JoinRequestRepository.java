package org.example.trendyolfinalproject.dao.repository;

import org.example.trendyolfinalproject.dao.entity.ChatGroup;
import org.example.trendyolfinalproject.dao.entity.JoinRequest;
import org.example.trendyolfinalproject.dao.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JoinRequestRepository extends JpaRepository<JoinRequest, Long> {
    List<JoinRequest> findByGroup(ChatGroup group);

    Optional<JoinRequest> findByGroupAndRequester(ChatGroup group, User requester);
}
