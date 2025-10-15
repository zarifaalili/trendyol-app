package org.example.trendyolfinalproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.client.CardClient;
import org.example.trendyolfinalproject.client.TransactionClient;
import org.example.trendyolfinalproject.dao.entity.ReturnRequest;
import org.example.trendyolfinalproject.dao.repository.*;
import org.example.trendyolfinalproject.exception.customExceptions.NotFoundException;
import org.example.trendyolfinalproject.mapper.OrderItemMapper;
import org.example.trendyolfinalproject.mapper.OrderMapper;
import org.example.trendyolfinalproject.model.enums.NotificationType;
import org.example.trendyolfinalproject.model.enums.Status;
import org.example.trendyolfinalproject.model.request.TransactionRequest;
import org.example.trendyolfinalproject.model.response.ApiResponse;
import org.example.trendyolfinalproject.model.response.ReturnRequestResponse;
import org.example.trendyolfinalproject.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReturnRequestImpl implements ReturnRequestService {


    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final UserRepository userRepository;
    private final AdressRepository adressRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final BasketRepository basketRepository;
    private final BasketElementRepository basketElementRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentTransactionService paymentTransactionService;
    private final ShipmentService shipmentService;
    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final AuditLogService auditLogService;
    private final SellerPaymentLogRepository sellerPaymentLogRepository;
    private final SellerRepository sellerRepository;
    private final ReturnRequestRepository returnRequestRepository;
    private final FileStorageService fileStorageService;
    private final TransactionClient transactionClient;
    private final CardClient cardClient;


    @Transactional
    @Override
    public ApiResponse<String> approveReturnRequest(Long returnRequestId) {
        log.info("Actionlog.approveReturnRequest.start : returnRequestId={}", returnRequestId);
        var userId = getCurrentUserId();
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        var returnRequest = returnRequestRepository.findById(returnRequestId)
                .orElseThrow(() -> new NotFoundException("ReturnRequest not found with id: " + returnRequestId));

        returnRequest.setApproved(true);
        returnRequestRepository.save(returnRequest);

        var orderItemPrice = returnRequest.getOrderItem().getUnitPrice().multiply(returnRequest.getOrderItem().getQuantity());
        var seller = returnRequest.getOrderItem().getProductId().getSeller().getUser().getId();

        var userPayment = paymentMethodRepository.findByUserId_IdAndIsDefault(returnRequest.getUser().getId(), true).orElseThrow(() -> new NotFoundException("Payment method not found"));
        var adminPayment = paymentMethodRepository.findByUserId_IdAndIsDefault(userId, true).orElseThrow(() -> new NotFoundException("Payment method not found"));
        var sellerPayment = paymentMethodRepository.findByUserId_IdAndIsDefault(seller, true).orElseThrow(() -> new NotFoundException("Payment method not found"));


        BigDecimal adminAmount = orderItemPrice.multiply(BigDecimal.valueOf(0.05));
        BigDecimal sellerAmount = orderItemPrice.multiply(BigDecimal.valueOf(0.95));

        if (adminPayment.getBalance().compareTo(adminAmount) < 0
                || sellerPayment.getBalance().compareTo(sellerAmount) < 0) {

            transactionClient.createTransaction(TransactionRequest.builder()
                    .amount(adminAmount)
                    .receiver(userPayment.getCardNumber())
                    .sender(adminPayment.getCardNumber())
                    .status(Status.FAILED)
                    .transactionDate(LocalDateTime.now())
                    .transactionId(generateTransactionId())
                    .currency(userPayment.getCurrency())
                    .providerResponse("Payment of Refund is failed received from admin orderItem: " + returnRequest.getOrderItem().getProductId().getName())
                    .build());


            transactionClient.createTransaction(TransactionRequest.builder()
                    .amount(sellerAmount)
                    .receiver(userPayment.getCardNumber())
                    .sender(sellerPayment.getCardNumber())
                    .status(Status.FAILED)
                    .transactionDate(LocalDateTime.now())
                    .transactionId(generateTransactionId())
                    .currency(userPayment.getCurrency())
                    .providerResponse("Payment of Refund is failed received from seller orderItem: " + returnRequest.getOrderItem().getProductId().getName())
                    .build());

            throw new RuntimeException("Trendyols balance is not enough");
        }

        userPayment.setBalance(userPayment.getBalance().add(orderItemPrice));
        adminPayment.setBalance(adminPayment.getBalance().subtract(adminAmount));
        sellerPayment.setBalance(sellerPayment.getBalance().subtract(sellerAmount));


        paymentMethodRepository.save(userPayment);
        paymentMethodRepository.save(adminPayment);
        paymentMethodRepository.save(sellerPayment);

        cardClient.transfer(adminPayment.getCardNumber(), userPayment.getCardNumber(), adminAmount);
        cardClient.transfer(sellerPayment.getCardNumber(), userPayment.getCardNumber(), sellerAmount);

        transactionClient.createTransaction(TransactionRequest.builder()
                .amount(adminAmount)
                .receiver(userPayment.getCardNumber())
                .sender(adminPayment.getCardNumber())
                .status(Status.SUCCESS)
                .transactionDate(LocalDateTime.now())
                .transactionId(generateTransactionId())
                .currency(userPayment.getCurrency())
                .providerResponse("Payment of Refund is successfully received from admin orderItem: " + returnRequest.getOrderItem().getProductId().getName())
                .build());

        transactionClient.createTransaction(TransactionRequest.builder()
                .amount(sellerAmount)
                .receiver(userPayment.getCardNumber())
                .sender(sellerPayment.getCardNumber())
                .status(Status.SUCCESS)
                .transactionDate(LocalDateTime.now())
                .transactionId(generateTransactionId())
                .currency(userPayment.getCurrency())
                .providerResponse("Payment of Refund is successfully received from seller orderItem: " + returnRequest.getOrderItem().getProductId().getName())
                .build());


        auditLogService.createAuditLog(user, "APPROVE RETURN REQUEST", "Return request approved successfully. Return request id: " + returnRequestId);
        notificationService.sendNotification(returnRequest.getUser(), "Your return request has been approved.", NotificationType.RETURN_REQUEST_APPROVED, returnRequestId);
        log.info("Actionlog.approveReturnRequest.end : returnRequestId={}", returnRequestId);
        return ApiResponse.<String>builder()
                .status(200)
                .message("Return request approved successfully.")
                .data("Return request approved successfully.")
                .build();
    }


    @Override
    public ApiResponse<String> sendReturnRequest(Long orderItemId, String reason, MultipartFile imageFile) {
        log.info("Actionlog.sendReturnRequest.start : orderItem={}", orderItemId);

        Long userId = getCurrentUserId();

        var orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new NotFoundException("OrderItem not found with id: " + orderItemId));
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        if (!userId.equals(orderItem.getOrderId().getUser().getId())) {
            throw new RuntimeException("You cannot send return request for this order item. Order item id: " + orderItemId);
        }
        if (!orderItem.getOrderId().getStatus().equals(Status.DELIVERED)) {
            throw new RuntimeException("Order item is not delivered. You cannot send return request. Order item id: " + orderItemId);
        }
        if (orderItem.getOrderId().getUpdatedAt().isBefore(LocalDateTime.now().minusDays(7))) {
            throw new RuntimeException("Return request period (7 days) has expired for order item id: " + orderItemId);
        }
        String imagePath = fileStorageService.storeFilee(imageFile);
        var returnRequest = ReturnRequest.builder()
                .orderItem(orderItem)
                .user(user)
                .reason(reason)
                .imageUrl(imagePath)
                .createdAt(LocalDateTime.now())
                .isApproved(false)
                .build();

        returnRequestRepository.save(returnRequest);
        auditLogService.createAuditLog(user, "Send return request", "Return request sent successfully. Order item id: " + orderItem.getId());
        notificationService.sendToAllUsers("New return request", NotificationType.RETURN_REQUEST, returnRequest.getId());
        notificationService.sendNotification(user, "Your return request has been sent successfully. Please wait for admin approval.", NotificationType.RETURN_REQUEST, returnRequest.getId());
        log.info("Actionlog.sendReturnRequest.end : orderItem={}", orderItemId);
        return ApiResponse.<String>builder()
                .status(200)
                .message("Return request sent successfully.")
                .data("Your return request has been sent successfully. Please wait for admin approval.")
                .build();
    }

    @Override
    public ApiResponse<String> getReturnRequestStatus(Long returnRequestId) {
        log.info("Actionlog.getReturnRequestStatus.start : returnRequestId={}", returnRequestId);
        var userId = getCurrentUserId();
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        var returnRequest = returnRequestRepository.findById(returnRequestId)
                .orElseThrow(() -> new NotFoundException("ReturnRequest not found with id: " + returnRequestId));
        if (!user.getId().equals(returnRequest.getUser().getId())) {
            throw new RuntimeException("You can not get this return request status. Return request id: " + returnRequestId);
        }
        log.info("Actionlog.getReturnRequestStatus.end : returnRequestId={}", returnRequestId);
        String status = returnRequest.isApproved() ? "Approved" : "Pending";
        return ApiResponse.<String>builder()
                .status(200)
                .message("Return request status retrieved successfully.")
                .data(status)
                .build();
    }

    @Override
    public ApiResponse<List<ReturnRequestResponse>> getReturnRequests() {
        Long userId = getCurrentUserId();
        log.info("Actionlog.getReturnRequestsByUserId.start : userId={}", userId);
        var returnRequests = returnRequestRepository.findAll();
        var responseList = maptoList(returnRequests);
        log.info("Actionlog.getReturnRequestsByUserId.end : userId={}, size={}", userId, responseList.size());
        auditLogService.createAuditLog(
                userRepository.findById(userId).orElseThrow(),
                "GET ALL RETURN REQUESTS",
                "Return requests retrieved successfully."
        );
        return ApiResponse.<List<ReturnRequestResponse>>builder()
                .status(200)
                .message("Return requests retrieved successfully.")
                .data(responseList)
                .build();
    }

    @Override
    public ApiResponse<List<ReturnRequestResponse>> getReturnRequestsByUser() {
        Long userId = getCurrentUserId();
        log.info("Actionlog.getReturnRequestsByUser.start : userId={}", userId);
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        var returnRequests = returnRequestRepository.findByUser(user);
        var list = maptoList(returnRequests);
        log.info("Actionlog.getReturnRequestsByUser.end : userId={}, size={}", userId, list.size());
        auditLogService.createAuditLog(user, "GET ALL RETURN REQUESTS", "Return requests retrieved successfully.");
        return new ApiResponse<>(200, "Return requests retrieved successfully.", list);
    }

    public List<ReturnRequestResponse> maptoList(List<ReturnRequest> returnRequests) {
        return returnRequests.stream()
                .map(rr -> ReturnRequestResponse.builder()
                        .id(rr.getId())
                        .orderItemId(rr.getOrderItem().getId())
                        .orderItemName(rr.getOrderItem().getProductId().getName())
                        .userId(rr.getUser().getId())
                        .reason(rr.getReason())
                        .imageUrl(rr.getImageUrl())
                        .createdAt(rr.getCreatedAt())
                        .isApproved(rr.isApproved())
                        .build())
                .toList();
    }

    @Override
    public ApiResponse<List<ReturnRequestResponse>> getNotApprovedReturnRequests() {
        Long userId = getCurrentUserId();
        log.info("Actionlog.getNotApprovedReturnRequests.start : userId={}", userId);
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        var returnRequests = returnRequestRepository.findByIsApproved(false);
        var list = maptoList(returnRequests);
        log.info("Actionlog.getNotApprovedReturnRequests.end : userId={}, size={}", userId, list.size());
        auditLogService.createAuditLog(user, "GET ALL RETURN REQUESTS", "Return requests retrieved successfully.");
        return ApiResponse.<List<ReturnRequestResponse>>builder()
                .status(200)
                .message(" Not Approved Return requests retrieved successfully.")
                .data(list)
                .build();
    }

    @Override
    public ApiResponse<List<ReturnRequestResponse>> getApprovedReturnRequests() {
        Long userId = getCurrentUserId();
        log.info("Actionlog.getApprovedReturnRequests.start : userId={}", userId);
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        var returnRequests = returnRequestRepository.findByIsApproved(true);
        var list = maptoList(returnRequests);
        log.info("Actionlog.getApprovedReturnRequests.end : userId={}, size={}", userId, list.size());
        auditLogService.createAuditLog(user, "GET ALL RETURN REQUESTS", "Return requests retrieved successfully.");
        return ApiResponse.<List<ReturnRequestResponse>>builder()
                .status(200)
                .message("Approved Return requests retrieved successfully.")
                .data(list)
                .build();
    }

    private Long getCurrentUserId() {
        return (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId");
    }


    private Integer generateTransactionId() {
        return (int) (Math.random() * 900000) + 100000;
    }
}
