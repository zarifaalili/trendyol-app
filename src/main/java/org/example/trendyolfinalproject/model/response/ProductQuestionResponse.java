package org.example.trendyolfinalproject.model.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductQuestionResponse {

    private String question;
    private String answer;
    private String customerName;
    private String productName;
    private String sellerName;
    private LocalDateTime createdAt;
    private LocalDateTime answeredAt;

}
