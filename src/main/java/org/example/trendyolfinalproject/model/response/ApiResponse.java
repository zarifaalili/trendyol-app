package org.example.trendyolfinalproject.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;


    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .status(200)
                .message("success")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .status(400)
                .message(message)
                .data(null)
                .build();
    }

    public static <T> ApiResponse<T> created(T data, String message) {
        return ApiResponse.<T>builder()
                .status(201)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> forbidden(String message) {
        return ApiResponse.<T>builder()
                .status(403)
                .message(message)
                .data(null)
                .build();
    }

    public static <T> ApiResponse<T> notFound(String message) {
        return ApiResponse.<T>builder()
                .status(404)
                .message(message)
                .data(null)
                .build();
    }
}

