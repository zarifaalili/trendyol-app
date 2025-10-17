package org.example.trendyolfinalproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.repository.*;
import org.example.trendyolfinalproject.exception.customExceptions.NotFoundException;
import org.example.trendyolfinalproject.mapper.ProductQuestionMapper;
import org.example.trendyolfinalproject.model.enums.NotificationType;
import org.example.trendyolfinalproject.model.enums.Status;
import org.example.trendyolfinalproject.model.request.ProductAnswerRequest;
import org.example.trendyolfinalproject.model.request.ProductQuestionRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.ProductQuestionResponse;
import org.example.trendyolfinalproject.service.AuditLogService;
import org.example.trendyolfinalproject.service.NotificationService;
import org.example.trendyolfinalproject.service.ProductQuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductQuestionServiceImpl implements ProductQuestionService {

    private final ProductQuestionRepository productQuestionRepository;
    private final ProductQuestionMapper productQuestionMapper;
    private final UserRepository userRepository;
    private final ProductVariantRepository productVariantRepository;
    private final NotificationService notificationService;
    private final SellerRepository sellerRepository;
    private final AuditLogService auditLogService;
    private final ProductRepository productRepository;

    @Override
    public ApiResponse<String> createProductQuestion(ProductQuestionRequest productQuestionRequest) {
        log.info("Actionlog.createProductQuestion.start : ");

        var userId = getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("You are not logged in");
        }
        var productVariant = productVariantRepository.findById(productQuestionRequest.getProductVariantId()).orElseThrow(() -> new RuntimeException("ProductVariant not found with id: " + productQuestionRequest.getProductVariantId()));
        var seller = productVariant.getProduct().getSeller();
        var sellerUserId = seller.getUser();
        var user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        var entity = productQuestionMapper.toEntity(productQuestionRequest);
        entity.setCustomer(user);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setSeller(seller);
        entity.setStatus(Status.PENDING);
        entity.setProductVariant(productVariant);

        productQuestionRepository.save(entity);
        auditLogService.createAuditLog(user, "ProductQuestion", "ProductQuestion created");
        notificationService.sendNotification(sellerUserId, "You have a new question from " + user.getName() + " " + user.getSurname(), NotificationType.PRODUCT_QUESTION, productVariant.getId());
        log.info("Actionlog.createProductQuestion.end : ");
        return ApiResponse.<String>builder()
                .status(201)
                .message("Your question has been sent successfully")
                .data("Your question has been sent successfully")
                .build();
    }

    @Override
    public ApiResponse<ProductQuestionResponse> answerProductQuestion(ProductAnswerRequest productAnswerRequest) {

        log.info("Actionlog.answerProductQuestion.start : ");
        var userId = getCurrentUserId();
        var questionId = productAnswerRequest.getProductQuestionId();
        var question = productQuestionRepository.findById(questionId).orElseThrow(() -> new RuntimeException("Question not found with id: " + questionId));
        var seller = question.getSeller();
        if (!question.getSeller().getUser().getId().equals(userId)) {
            throw new RuntimeException("You can not answer this question");
        }
        question.setAnsweredAt(LocalDateTime.now());
        question.setAnswer(productAnswerRequest.getAnswer());
        question.setStatus(Status.ANSWERED);
        productQuestionRepository.save(question);
        var response = productQuestionMapper.toResponse(question);
        auditLogService.createAuditLog(seller.getUser(), "ProductQuestion", "ProductQuestion answered");
        notificationService.sendNotification(question.getCustomer(), "Your question has been answered", NotificationType.PRODUCT_QUESTION, question.getId());
        log.info("Actionlog.answerProductQuestion.end : ");

        return ApiResponse.<ProductQuestionResponse>builder()
                .status(200)
                .message("Question answered successfully")
                .data(response)
                .build();
    }

    @Override
    public ApiResponse<Void> deleteProductQuestion(Long id) {
        log.info("Actionlog.deleteProductQuestion.start : ");
        var userdId = getCurrentUserId();
        var user = userRepository.findById(userdId).orElseThrow(() -> new NotFoundException("User not found with id: " + userdId));
        var question = productQuestionRepository.findById(id).orElseThrow(() -> new NotFoundException("Question not found with id: " + id));
        if (!question.getCustomer().getId().equals(user.getId())) {
            throw new RuntimeException("You can not delete this question.Because it is not your question");
        }
        productQuestionRepository.delete(question);
        auditLogService.createAuditLog(user, "ProductQuestion", "ProductQuestion deleted");
        log.info("Actionlog.deleteProductQuestion.end : ");
        return ApiResponse.noContent();
    }

    @Override
    public ApiResponse<String> deleteProductAnswer(Long id) {
        log.info("Actionlog.deleteProductAnswer.start : ");
        var userdId = getCurrentUserId();
        var user = userRepository.findById(userdId).orElseThrow(() -> new NotFoundException("User not found with id: " + userdId));
        var question = productQuestionRepository.findById(id).orElseThrow(() -> new NotFoundException("Question not found with id: " + id));
        if (!question.getSeller().getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"You can not delete this answer.Because it is not your answer");
        }
        question.setAnswer(null);
        question.setAnsweredAt(null);
        question.setStatus(Status.PENDING);
        productQuestionRepository.save(question);
        auditLogService.createAuditLog(user, "ProductQuestion", "ProductQuestion answer deleted");
        log.info("Actionlog.deleteProductAnswer.end : ");
        return ApiResponse.<String>builder()
                .status(200)
                .message("Answer deleted successfully")
                .data("Your answer has been deleted successfully")
                .build();
    }

    @Override
    public ApiResponse<List<ProductQuestionResponse>> getAllProductQuestions(Long productVariantId) {
        log.info("Actionlog.getAllProductQuestions.start : ");
        var userId = getCurrentUserId();
        var seller = sellerRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        var productVariant = productVariantRepository.findById(productVariantId).orElseThrow(() -> new RuntimeException("ProductVariant not found with id: " + productVariantId));
        var product = productVariant.getProduct();
        var seller2 = product.getSeller();
        if (!seller.getId().equals(seller2.getId())) {
            throw new RuntimeException("You are not the seller of this product");
        }
        var productQuestions = productQuestionRepository.findAllByProductVariantId(productVariantId);
        var response = productQuestionMapper.toResponseList(productQuestions);
        log.info("Actionlog.getAllProductQuestions.end : ");
        return ApiResponse.<List<ProductQuestionResponse>>builder()
                .status(200)
                .message("All product questions retrieved successfully")
                .data(response)
                .build();
    }

    @Override
    public ApiResponse<List<ProductQuestionResponse>> getProductQuestionsWithStatus(Long productVariantId, String status) {
        log.info("Actionlog.getAnsweredProductQuestions.start : ");
        var userId = getCurrentUserId();
        var seller = sellerRepository.findByUserId(userId).orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        var productVariant = productVariantRepository.findById(productVariantId).orElseThrow(() -> new NotFoundException("ProductVariant not found with id: " + productVariantId));
        var product = productVariant.getProduct();
        var seller2 = product.getSeller();

        if (!seller.getId().equals(seller2.getId())) {
            throw new RuntimeException("You are not the seller of this product");
        }
        var status2 = status.toUpperCase();
        var status1 = Status.valueOf(status2);
        var productQuestions = productQuestionRepository.findAllByProductVariantIdAndStatus(productVariantId, status1);
        var response = productQuestionMapper.toResponseList(productQuestions);
        log.info("Actionlog.getAnsweredProductQuestions.end : ");
        return ApiResponse.<List<ProductQuestionResponse>>builder()
                .status(200)
                .message("Product questions with status retrieved successfully")
                .data(response)
                .build();
    }

    private Long getCurrentUserId() {
        return (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId");
    }
}


