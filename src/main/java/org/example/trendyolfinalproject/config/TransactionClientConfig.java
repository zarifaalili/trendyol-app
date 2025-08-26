package org.example.trendyolfinalproject.config;


import feign.codec.ErrorDecoder;
import org.example.trendyolfinalproject.exception.decoder.TransactionClientErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransactionClientConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new TransactionClientErrorDecoder();
    }
}
