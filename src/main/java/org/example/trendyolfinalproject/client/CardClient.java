package org.example.trendyolfinalproject.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;

@FeignClient(name = "ms-card-trendyol", contextId = "cardClient", url = "http://localhost:9998/v1/cards")
public interface CardClient {

    @GetMapping("/validate/{cardNumber}/{holderName}")
    Boolean validateCard(@PathVariable String cardNumber, @PathVariable String holderName);

    @GetMapping("/validate/{cardNumber}")
    Boolean simplevalidateCard(@PathVariable String cardNumber);

    @GetMapping("/{cardNumber}/holdername")
    String getHolderName(@PathVariable String cardNumber);

    @PostMapping("/transfer/{cardNumber}/{cardNumber2}/{amount}")
    String transfer(@PathVariable String cardNumber, @PathVariable String cardNumber2, @PathVariable BigDecimal amount);

    @GetMapping("/balance/{cardNumber}")
    BigDecimal getBalance(@PathVariable String cardNumber);

    @PostMapping("/balance/{cardNumber}/{amount}")
    public void addBalance(@PathVariable String cardNumber, @PathVariable BigDecimal amount);

}

