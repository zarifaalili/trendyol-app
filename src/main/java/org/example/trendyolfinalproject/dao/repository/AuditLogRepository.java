package org.example.trendyolfinalproject.dao.repository;

import org.example.trendyolfinalproject.dao.entity.AuditLog;
import org.example.trendyolfinalproject.dao.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AuditLogRepository extends JpaRepository<AuditLog, Integer> {

    @Query("SELECT a FROM AuditLog a WHERE a.userId = :user ORDER BY a.createdAt DESC LIMIT 1")
    Optional<AuditLog> findLastByUser(User user);

    List<AuditLog> findByUserId(User userId);
}
