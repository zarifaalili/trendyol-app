package org.example.trendyolfinalproject.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-card-trendyol", contextId = "emailClient", url = "http://localhost:9998/v1/email")

public interface EmailClient {

    @GetMapping("/exists/{email}")
    public Boolean checkEmailExists(@PathVariable String email);
}
