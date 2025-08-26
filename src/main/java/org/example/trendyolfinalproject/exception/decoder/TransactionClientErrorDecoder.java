package org.example.trendyolfinalproject.exception.decoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.example.trendyolfinalproject.exception.response.Responsee;

import java.io.IOException;

public class TransactionClientErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.body() != null) {
            try {
                var body = response.body().asInputStream();
                Responsee apiResponse = new ObjectMapper().readValue(body, Responsee.class);
                return new RuntimeException(apiResponse.getMessage());
            } catch (IOException e) {
                return new RuntimeException("Cannot read response body");
            }
        } else {
            switch (response.status()) {
                case 404:
                    return new RuntimeException("Transactions not found (404)");
                case 400:
                    return new RuntimeException("Bad request (400)");
                default:
                    return new RuntimeException("Unknown error: " + response.status());
            }
        }
    }

}
