package org.example.trendyolfinalproject.service;

import org.example.trendyolfinalproject.model.request.ProductAnswerRequest;
import org.example.trendyolfinalproject.model.request.ProductQuestionRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.ProductQuestionResponse;

import java.util.List;

public interface ProductQuestionService {

    ApiResponse<String> createProductQuestion(ProductQuestionRequest productQuestionRequest);

    ApiResponse<ProductQuestionResponse> answerProductQuestion(ProductAnswerRequest productAnswerRequest);

    ApiResponse<Void> deleteProductQuestion(Long id);

    ApiResponse<String> deleteProductAnswer(Long id);

    ApiResponse<List<ProductQuestionResponse>> getAllProductQuestions(Long productVariantId);

    ApiResponse<List<ProductQuestionResponse>> getProductQuestionsWithStatus(Long productVariantId, String status);

}
