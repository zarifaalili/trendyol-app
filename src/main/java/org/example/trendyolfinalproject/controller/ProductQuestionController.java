package org.example.trendyolfinalproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.model.request.ProductAnswerRequest;
import org.example.trendyolfinalproject.model.request.ProductQuestionRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.ProductQuestionResponse;
import org.example.trendyolfinalproject.service.ProductQuestionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/product-questions")
@RequiredArgsConstructor
public class ProductQuestionController {
    private final ProductQuestionService productQuestionService;


    @PostMapping
    public ApiResponse<String> createProductQuestion(@RequestBody @Valid ProductQuestionRequest productQuestionRequest) {
        return productQuestionService.createProductQuestion(productQuestionRequest);
    }

    @PostMapping("/answer")
    @PreAuthorize("hasRole('SELLER')")
    public ApiResponse<ProductQuestionResponse> answerProductQuestion(@RequestBody @Valid ProductAnswerRequest productAnswerRequest) {
        return productQuestionService.answerProductQuestion(productAnswerRequest);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<String> deleteProductQuestion(@PathVariable Long id) {
        return productQuestionService.deleteProductQuestion(id);
    }

    @PatchMapping("/{id}/answer")
    @PreAuthorize("hasRole('SELLER')")
    public ApiResponse<String> deleteProductAnswer(@PathVariable Long id) {
        return productQuestionService.deleteProductAnswer(id);
    }

    @GetMapping("/{productVariantId}")
    @PreAuthorize("hasRole('SELLER')")
    public ApiResponse<List<ProductQuestionResponse>> getAllProductQuestions(@PathVariable Long productVariantId) {
        return productQuestionService.getAllProductQuestions(productVariantId);
    }

    @GetMapping("/{productVariantId}/status")
    @PreAuthorize("hasRole('SELLER')")
    public ApiResponse<List<ProductQuestionResponse>> getAnsweredProductQuestions(@PathVariable Long productVariantId,
                                                                                  @RequestParam String status) {
        return productQuestionService.getProductQuestionsWithStatus(productVariantId, status);
    }

}
