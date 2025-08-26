package org.example.trendyolfinalproject.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    private Long id;
    private Long userId;
    private String userName;

    private Long productId;
    private String productName;

    private Integer rating;
    private String comment;
    private LocalDateTime reviewDate;
    private Boolean isApproved;
    private List<String> imageUrls;

}
