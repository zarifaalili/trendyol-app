package org.example.trendyolfinalproject.model.response;


import lombok.Builder;
import lombok.Data;
import org.example.trendyolfinalproject.dao.entity.User;

import java.time.LocalDateTime;

@Data
@Builder
public class AuditLogResponse {

    private User userId;
    private String action;
    private String details;
    private LocalDateTime actionTime;
}
