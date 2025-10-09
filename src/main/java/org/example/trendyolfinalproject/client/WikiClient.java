package org.example.trendyolfinalproject.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "wikiapp", contextId = "wikiClient", url = "http://localhost:8888/wiki")
public interface WikiClient {

    @GetMapping
    String getWikiArticle(@RequestParam String query);
}
