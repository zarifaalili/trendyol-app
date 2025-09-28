package org.example.trendyolfinalproject.model.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class WheelResponse {

    private Long id;
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private List<WheelPrizeResponse> prizes;
}
