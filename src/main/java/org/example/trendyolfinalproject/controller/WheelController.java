package org.example.trendyolfinalproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.model.request.WheelRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.SpinWheelResponse;
import org.example.trendyolfinalproject.service.WheelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/wheels")
public class WheelController {

    private final WheelService wheelService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> createWheel(@RequestBody @Valid WheelRequest wheelRequest) {
        wheelService.createWheel(wheelRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder()
                        .data(null)
                        .status(HttpStatus.CREATED.value())
                        .message("Wheel created").build());
    }


    @PostMapping("/{wheelId}/spin")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<SpinWheelResponse>> spinWheel(@PathVariable Long wheelId) {
        SpinWheelResponse response = wheelService.spinWheel(wheelId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }


    @GetMapping("/time-left")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getTimeLeft() {
        return ResponseEntity.ok(ApiResponse.success(wheelService.getTimeLeft()));
    }

    @PostMapping("/{userWheelId}/use-wheel-price")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<Void>> useWheelPrice(@PathVariable Long userWheelId) {

        wheelService.useWheelPrice(userWheelId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }


    @PostMapping("/{userWheelId}/cancel-use-wheel-price")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<Void>> cancelUseWheelPrice(@PathVariable Long userWheelId) {

        wheelService.cancelWheelPrize(userWheelId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
