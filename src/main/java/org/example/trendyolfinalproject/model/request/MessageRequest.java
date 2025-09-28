package org.example.trendyolfinalproject.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequest {
    @NotNull(message = "You have to write your message for join to group")
    private String message;
}
