package org.example.trendyolfinalproject.exception.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Response {
    private String message;
    private String code;
}
