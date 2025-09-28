package org.example.trendyolfinalproject.model.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MessageReadStatusResponse {

    private Long id;
    private Long messageId;
    private Long userId;
    private String username;
    private Boolean isRead;
    private LocalDateTime readTimestamp;

}
