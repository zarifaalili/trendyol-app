package org.example.trendyolfinalproject.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.trendyolfinalproject.model.enums.GroupVisibility;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatGroupResponse {
    private Long id;
    private String title;
    private String description;
    private GroupVisibility visibility;
    private Long ownerId;
    private LocalDateTime createdAt;
}