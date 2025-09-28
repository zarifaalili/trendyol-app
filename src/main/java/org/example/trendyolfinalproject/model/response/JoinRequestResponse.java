package org.example.trendyolfinalproject.model.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.trendyolfinalproject.model.enums.RequestStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JoinRequestResponse {
    private Long id;
    private Long groupId;
    private String groupName;
    private Long requesterId;
    private String requesterEmail;
    private String message;
    private LocalDateTime requestedAt;
    private RequestStatus status;
}

