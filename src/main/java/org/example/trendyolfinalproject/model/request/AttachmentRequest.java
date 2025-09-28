package org.example.trendyolfinalproject.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttachmentRequest {
    private String url;
    private String mimeType;
    private Long size;
}