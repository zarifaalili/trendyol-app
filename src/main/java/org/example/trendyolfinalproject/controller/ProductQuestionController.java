package org.example.trendyolfinalproject.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PastOrPresent;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.model.Status;
import org.example.trendyolfinalproject.request.ProductAnswerRequest;
import org.example.trendyolfinalproject.request.ProductQuestionRequest;
import org.example.trendyolfinalproject.response.ProductQuestionResponse;
import org.example.trendyolfinalproject.service.ProductQuestionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Stack;

@RestController
@RequestMapping("/v1/product-Question")
@RequiredArgsConstructor
public class ProductQuestionController {
    private final ProductQuestionService productQuestionService;


    @PostMapping("/createProductQuestion")
    public String createProductQuestion(@RequestBody @Valid ProductQuestionRequest productQuestionRequest) {
        return productQuestionService.createProductQuestion(productQuestionRequest);
    }

    @PostMapping("/answerProductQuestion")
    @PreAuthorize("hasRole('SELLER')")
    public ProductQuestionResponse answerProductQuestion(@RequestBody @Valid ProductAnswerRequest productAnswerRequest) {
        return productQuestionService.answerProductQuestion(productAnswerRequest);
    }


    @DeleteMapping("/deleteProductQuestion/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String deleteProductQuestion(@PathVariable Long id) {
        return productQuestionService.deleteProductQuestion(id);
    }

    @PatchMapping("/deleteProductAnswer/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public String deleteProductAnswer(@PathVariable Long id) {
        return productQuestionService.deleteProductAnswer(id);
    }

    @GetMapping("/getAllProductQuestions/{productVariantId}")
    @PreAuthorize("hasRole('SELLER')")
    public List<ProductQuestionResponse> getAllProductQuestions(@PathVariable Long productVariantId) {
        return productQuestionService.getAllProductQuestions(productVariantId);
    }

    @GetMapping("/getAnsweredProductQuestions/{productVariantId}")
    @PreAuthorize("hasRole('SELLER')")
    public List<ProductQuestionResponse> getAnsweredProductQuestions(@PathVariable Long productVariantId,
                                                                     @PathVariable String status) {
        return productQuestionService.getProductQuestionsWithStatus(productVariantId,status);
    }

}
