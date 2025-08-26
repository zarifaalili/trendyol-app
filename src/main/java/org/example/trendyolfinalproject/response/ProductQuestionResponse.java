package org.example.trendyolfinalproject.response;

import lombok.Data;
import org.example.trendyolfinalproject.model.Status;

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
