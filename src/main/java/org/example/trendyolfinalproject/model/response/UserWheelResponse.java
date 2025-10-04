package org.example.trendyolfinalproject.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserWheelResponse {
    private Long id;
    private Long userId;
    private Long wheelId;
    private Long prizeId;

    private LocalDateTime usedAt;
    private LocalDateTime startedAt;
    private LocalDateTime expiresAt;
}
