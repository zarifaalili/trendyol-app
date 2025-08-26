package org.example.trendyolfinalproject.client;

import org.example.trendyolfinalproject.config.TransactionClientConfig;
import org.example.trendyolfinalproject.request.TransactionRequest;
import org.example.trendyolfinalproject.response.TransactionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "ms-card-trendyol", contextId = "transactionClient", url = "http://localhost:9998/v1/transaction"
,    configuration = TransactionClientConfig.class
)
public interface TransactionClient {

    @PostMapping("/create")
    void createTransaction(@RequestBody TransactionRequest transactionRequest);

    @GetMapping("/all/{receiver}")
    List<TransactionResponse> getAllTransactions(@PathVariable String receiver);
}
