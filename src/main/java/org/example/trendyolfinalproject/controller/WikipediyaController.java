package org.example.trendyolfinalproject.controller;

import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.client.WikiClient;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/wiki")
@RequiredArgsConstructor
public class WikipediyaController {

    private final WikiClient wikiClient;

    @GetMapping
    public ResponseEntity<ApiResponse<String>> getWiki(@RequestParam String title) {
        return ResponseEntity.ok(ApiResponse.success(wikiClient.getWikiArticle(title)));
    }

}
