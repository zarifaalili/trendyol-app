package org.example.trendyolfinalproject.model.response;


import lombok.Getter;
import lombok.Setter;
import org.example.trendyolfinalproject.model.enums.MessageType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ChatMessageResponse {
    private Long id;
    private Long senderId;
    private MessageType type;
    private String text;
    private Long groupId;
    private Long forwardedFromMessageId;
    private Long productVariantId;
    private LocalDateTime sentAt;
    private Boolean edited;
    private Boolean deletedForAll;
    private List<AttachmentResponse> attachments;
}

