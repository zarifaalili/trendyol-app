package org.example.trendyolfinalproject.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.example.trendyolfinalproject.model.enums.MessageType;

import java.util.List;

@Getter
@Setter
public class ChatMessageRequest {
    @NotNull(message = "Type cannot be null")
    private MessageType type;
    private String text;
    private Long productVariantId;
    private List<AttachmentRequest> attachments;
}
