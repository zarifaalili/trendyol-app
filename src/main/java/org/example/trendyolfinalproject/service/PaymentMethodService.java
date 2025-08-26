package org.example.trendyolfinalproject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.client.CardClient;
import org.example.trendyolfinalproject.client.TransactionClient;
import org.example.trendyolfinalproject.dao.entity.Seller;
import org.example.trendyolfinalproject.dao.repository.AuditLogRepository;
import org.example.trendyolfinalproject.dao.repository.PaymentMethodRepository;
import org.example.trendyolfinalproject.dao.repository.PaymentTransactionRepository;
import org.example.trendyolfinalproject.dao.repository.UserRepository;
import org.example.trendyolfinalproject.exception.customExceptions.NotFoundException;
import org.example.trendyolfinalproject.mapper.PaymentMethodMapper;
import org.example.trendyolfinalproject.model.NotificationType;
import org.example.trendyolfinalproject.model.Status;
import org.example.trendyolfinalproject.request.*;
import org.example.trendyolfinalproject.response.PaymentMethodResponse;
import org.example.trendyolfinalproject.response.PaymentResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentMethodMapper paymentMethodMapper;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;
    private final PaymentTransactionService paymentTransactionService;
    private final CardClient cardClient;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final TransactionClient transactionClient;

    public PaymentMethodResponse createPaymentMethod(PaymentMethodCreateRequest request) {
        log.info("Actionlog.createPaymentMethod.start : ");
        var userId = getCurrentUserId();

        var user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User not found with id: " + userId));

        var holderName = request.getCardHolderName().toUpperCase();
        var validation = cardClient.validateCard(request.getCardNumber(), holderName);
        if (!validation) {
            throw new RuntimeException("Card is not valid");
        }
        boolean exists = paymentMethodRepository.findByCardNumber(request.getCardNumber()).isPresent();

        if (exists) {
            throw new RuntimeException("PaymentMethod already exists");
        }

        var balance = cardClient.getBalance(request.getCardNumber());
        var entity = paymentMethodMapper.toEntity(request);
        entity.setUserId(user);
        entity.setBalance(balance);
        entity.setCardHolderName(holderName);


        boolean existIsDefaultTrue = paymentMethodRepository.existsByUserId_Id(userId);
        entity.setIsDefault(!existIsDefaultTrue);

        var saved = paymentMethodRepository.save(entity);
        var response = paymentMethodMapper.toResponse(saved);
        response.setBalance(balance);

        auditLogService.createAuditLog(user, "PaymentMethod created", "PaymentMethod created successfully. PaymentMethod id: " + saved.getId());
        log.info("Actionlog.createPaymentMethod.end : ");
        return response;

    }


    public void addbalance(AddBalanceRequest request) {

        log.info("Actionlog.addbalance.start : ");
        var paymentMethod = paymentMethodRepository.findByCardNumber(request.getCardNumber()).orElseThrow(
                () -> new RuntimeException("PaymentMethod not found with cardNumber: " + request.getCardNumber()));


        paymentMethod.setBalance(paymentMethod.getBalance().add(request.getAmount()));
        cardClient.addBalance(paymentMethod.getCardNumber(), request.getAmount());
        auditLogService.createAuditLog(paymentMethod.getUserId(), "Balance added", "Balance added successfully. PaymentMethod id: " + paymentMethod.getId());
        transactionClient.createTransaction(TransactionRequest.builder()
                .amount(request.getAmount())
                .currency(paymentMethod.getCurrency())
                .sender(null)
                .receiver(paymentMethod.getCardNumber())
                .status(Status.PENDING)
                .build()
        );
        paymentMethodRepository.save(paymentMethod);
        log.info("Actionlog.addbalance.end : ");
    }


    public PaymentMethodResponse changeDefaultPaymentMethod(ChangeDefaultPaymentMethod request) {
        log.info("Actionlog.changeDefaultPaymentMethod.start : ");
        var userId = getCurrentUserId();
        var user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User not found with id: " + userId));
        var paymentMethod = paymentMethodRepository.findById(request.getPaymentMethodId()).orElseThrow(
                () -> new RuntimeException("PaymentMethod not found with id: " + request.getPaymentMethodId()));

        if (!paymentMethod.getUserId().getId().equals(userId)) {
            throw new RuntimeException("PaymentMethod dont relate with user");
        }
        if (paymentMethod.getIsDefault()) {
            throw new RuntimeException("PaymentMethod already default");
        }

        var userPaymentMethods = paymentMethodRepository.findByUserId_Id(userId);
        userPaymentMethods.forEach(pm -> pm.setIsDefault(false));
        paymentMethodRepository.saveAll(userPaymentMethods);

        paymentMethod.setIsDefault(true);
        paymentMethodRepository.save(paymentMethod);
        var response = paymentMethodMapper.toResponse(paymentMethod);

        auditLogService.createAuditLog(user, "Default PaymentMethod changed", "Default PaymentMethod changed successfully. PaymentMethod id: " + paymentMethod.getId());
        log.info("Actionlog.changeDefaultPaymentMethod.end : ");
        return response;
    }


    @Transactional
    public void payToSellers(Map<Seller, BigDecimal> earnings) {
        earnings.forEach((seller, amount) -> {
            var paymentMethod = paymentMethodRepository.findByUserId_IdAndIsDefault(seller.getUser().getId(), true).orElseThrow(
                    () -> new NotFoundException("PaymentMethod not found for seller: " + seller.getUser().getName())
            );
            var admin = userRepository.findById(9L).orElseThrow(
                    () -> new NotFoundException("User not found with id: " + (9L))
            );
            var adminPaymentMethod = paymentMethodRepository.findByUserId_IdAndIsDefault(admin.getId(), true).orElseThrow(
                    () -> new NotFoundException("PaymentMethod not found for admin: " + admin.getName())
            );


            var softAmount = amount.multiply(new BigDecimal("0.95"));
            adminPaymentMethod.setBalance(adminPaymentMethod.getBalance().subtract(softAmount));
            paymentMethod.setBalance(paymentMethod.getBalance().add(softAmount));
            paymentMethodRepository.save(paymentMethod);
            paymentMethodRepository.save(adminPaymentMethod);


            paymentTransactionService.createSuccessPaymentTransactionFromAdminToSellers(admin, seller.getUser(), amount, paymentMethod);
            transactionClient.createTransaction(TransactionRequest.builder()
                    .amount(amount)
                    .currency(paymentMethod.getCurrency())
                    .sender(adminPaymentMethod.getCardNumber())
                    .receiver(paymentMethod.getCardNumber())
                    .status(Status.SUCCESS)
                    .providerResponse("Payment is successfully received from admın " + admin.getName())
                    .transactionId(generateTransactionId())
                    .transactionDate(LocalDateTime.now())
                    .build()
            );
            notificationService.sendNotification(admin, "Payment received from " + seller.getUser().getName(), NotificationType.PAYMENT_RECEIVED, seller.getId());
            notificationService.sendNotification(seller.getUser(), "Payment received from " + admin.getName(), NotificationType.PAYMENT_RECEIVED, seller.getId());
            auditLogService.createAuditLog(admin, "Payment received from " + seller.getUser().getName(), "Payment received from " + seller.getUser().getName());
        });
    }

    @Transactional
    public PaymentResponse pay(PaymentRequest request) {
        log.info("Actionlog.pay.start : ");
        var userId = getCurrentUserId();
        var user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found with id: " + userId));
        var paymentMethod = paymentMethodRepository.findByUserId_IdAndIsDefault(userId, true).orElseThrow(
                () -> new NotFoundException("PaymentMethod not found for user: " + user.getName())
        );
        var receiver = paymentMethodRepository.findByCardNumber(request.getCardNumber()).orElseThrow(
                () -> new NotFoundException("PaymentMethod not found for user: " + user.getName())
        );

        var existingCard = cardClient.simplevalidateCard(request.getCardNumber());
        if (!existingCard) {
            throw new RuntimeException("Card is not valid");
        }

        if (paymentMethod.getBalance().compareTo(request.getAmount()) < 0) {
            transactionClient.createTransaction(TransactionRequest.builder()
                    .amount(request.getAmount())
                    .currency(paymentMethod.getCurrency())
                    .sender(paymentMethod.getCardNumber())
                    .receiver(receiver.getCardNumber())
                    .status(Status.FAILED)
                    .providerResponse("Not enough balance")
                    .transactionId(generateTransactionId())
                    .transactionDate(LocalDateTime.now())
                    .build()
            );
            throw new RuntimeException("Not enough balance");

        }

        paymentMethod.setBalance(paymentMethod.getBalance().subtract(request.getAmount()));
        paymentMethodRepository.save(paymentMethod);

        transactionClient.createTransaction(TransactionRequest.builder()
                .amount(request.getAmount())
                .currency(paymentMethod.getCurrency())
                .sender(paymentMethod.getCardNumber())
                .receiver(receiver.getCardNumber())
                .status(Status.SUCCESS)
                .providerResponse("Payment is successfully")
                .transactionId(generateTransactionId())
                .transactionDate(LocalDateTime.now())
                .build()
        );

        var holderName = cardClient.getHolderName(request.getCardNumber());
        var response = PaymentResponse.builder()
                .amount(request.getAmount())
                .paymentMethodId(paymentMethod.getId())
                .fromCardNumber(paymentMethod.getCardNumber())
                .toCardNumber(request.getCardNumber())
                .holderName(holderName)
                .toCardNumber(maskCardNumber(request.getCardNumber()))
                .fromCardNumber(maskCardNumber(paymentMethod.getCardNumber()))
                .paymentDate(LocalDateTime.now())
                .status(Status.SUCCESS)
                .build();
        cardClient.transfer(paymentMethod.getCardNumber(), request.getCardNumber(), request.getAmount());
        log.info("Actionlog.pay.end : ");
        return response;
    }


    public String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 8) {
            throw new IllegalArgumentException("Card number is too short!");
        }
        String first4 = cardNumber.substring(0, 4);
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        String masked = "X".repeat(cardNumber.length() - 8);
        return first4 + masked + last4;
    }


    private Long getCurrentUserId() {
        return (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId");
    }

    private Integer generateTransactionId() {
        return (int) (Math.random() * 900000) + 100000;
    }


}
