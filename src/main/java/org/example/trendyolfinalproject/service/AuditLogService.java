package org.example.trendyolfinalproject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.entity.AuditLog;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.dao.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.awt.event.ActionListener;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {
    private final AuditLogRepository auditLogRepository;

    public void createAuditLog(User user,String action,String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(user);
        auditLog.setCreatedAt(LocalDateTime.now());
        auditLog.setAction(action);
        auditLog.setDetails(details);
        auditLogRepository.save(auditLog);
    }
}
