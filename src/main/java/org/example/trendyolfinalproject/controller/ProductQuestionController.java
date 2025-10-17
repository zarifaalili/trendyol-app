package org.example.trendyolfinalproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.model.request.ProductAnswerRequest;
import org.example.trendyolfinalproject.model.request.ProductQuestionRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.ProductQuestionResponse;
import org.example.trendyolfinalproject.service.ProductQuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/product-questions")
@RequiredArgsConstructor
public class ProductQuestionController {
    private final ProductQuestionService productQuestionService;


    @PostMapping
    public ResponseEntity<ApiResponse<String>> createProductQuestion(@RequestBody @Valid ProductQuestionRequest productQuestionRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productQuestionService.createProductQuestion(productQuestionRequest));
    }

    @PostMapping("/answer")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<ProductQuestionResponse>> answerProductQuestion(@RequestBody @Valid ProductAnswerRequest productAnswerRequest) {
        return ResponseEntity.ok().body(productQuestionService.answerProductQuestion(productAnswerRequest));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<Void>> deleteProductQuestion(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(productQuestionService.deleteProductQuestion(id));
    }

    @PatchMapping("/{id}/answer")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<String>> deleteProductAnswer(@PathVariable Long id) {
        return ResponseEntity.ok().body(productQuestionService.deleteProductAnswer(id));
    }

    @GetMapping("/{productVariantId}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<List<ProductQuestionResponse>>> getAllProductQuestions(@PathVariable Long productVariantId) {
        return ResponseEntity.ok().body(productQuestionService.getAllProductQuestions(productVariantId));
    }

    @GetMapping("/{productVariantId}/status")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<List<ProductQuestionResponse>>> getAnsweredProductQuestions(@PathVariable Long productVariantId,
                                                                                  @RequestParam String status) {
        return ResponseEntity.ok().body(productQuestionService.getProductQuestionsWithStatus(productVariantId, status));
    }

}
