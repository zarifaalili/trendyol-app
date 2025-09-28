package org.example.trendyolfinalproject.model.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class WheelRequest {

    @NotNull(message = "Name cannot be null")
    private String name;

//    @NotNull(message = "Start time cannot be null")
//    @Future(message = "Start time must be in the future")
//    private LocalDateTime startTime;

    @NotNull(message = "End time cannot be null")
    private LocalDateTime endTime;

    @NotNull(message = "Prizes cannot be null")
    private List<WheelPrizeRequest> prizes;

//    @AssertTrue(message = "End time must be after start time")
//    public boolean isEndTimeValid() {
//        if (startTime == null || endTime == null) {
//            return true;
//        }
//        return endTime.isAfter(startTime);
//    }
}
