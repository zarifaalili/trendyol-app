package org.example.trendyolfinalproject.dao.repository;

import org.example.trendyolfinalproject.dao.entity.ChatGroup;
import org.example.trendyolfinalproject.dao.entity.ChatGroupKeys;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatGroupKeysRepository extends JpaRepository<ChatGroupKeys, Long> {
    Optional<ChatGroupKeys> findByGroup(ChatGroup group);
}
