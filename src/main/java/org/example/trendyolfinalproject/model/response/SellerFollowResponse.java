package org.example.trendyolfinalproject.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class SellerFollowResponse {

    private String follower;


    private String seller;

    private LocalDateTime followedAt;
}
