package org.example.trendyolfinalproject.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttachmentResponse {
    private Long id;
    private String url;
    private String mimeType;
    private Long size;

    public AttachmentResponse(Long id, String url, String mimeType, Long size) {
        this.id = id;
        this.url = url;
        this.mimeType = mimeType;
        this.size = size;
    }
}
