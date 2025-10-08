package org.example.trendyolfinalproject.service;

import org.example.trendyolfinalproject.model.request.WheelRequest;
import org.example.trendyolfinalproject.model.response.SpinWheelResponse;
import org.example.trendyolfinalproject.model.response.UserWheelResponse;

import java.util.List;
import java.util.Map;

public interface WheelService {

    void createWheel(WheelRequest wheelRequest);

    SpinWheelResponse spinWheel(Long wheelId);

    Map<String, Long> getTimeLeft();

    void useWheelPrice(Long userWheelId);

    void cancelWheelPrize(Long userWheelId);

    List<UserWheelResponse> getAllWheels();
}
