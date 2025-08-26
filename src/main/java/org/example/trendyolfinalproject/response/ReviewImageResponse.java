package org.example.trendyolfinalproject.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewImageResponse {

    private Long id;
    private Long reviewId;
    private String imageUrl;
    private LocalDateTime createdAt;
}
