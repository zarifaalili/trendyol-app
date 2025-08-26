package org.example.trendyolfinalproject.response;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.example.trendyolfinalproject.dao.entity.OrderItem;
import org.example.trendyolfinalproject.dao.entity.User;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ReturnRequestResponse {

    private Long id;

    private Long orderItemId;

    private String orderItemName;

    private Long userId;

    private String reason;

    private String imageUrl;

    private LocalDateTime createdAt;

    private boolean isApproved;
}
