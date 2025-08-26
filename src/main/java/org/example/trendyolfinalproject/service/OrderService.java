package org.example.trendyolfinalproject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.client.CardClient;
import org.example.trendyolfinalproject.client.TransactionClient;
import org.example.trendyolfinalproject.dao.entity.*;
import org.example.trendyolfinalproject.dao.repository.*;
import org.example.trendyolfinalproject.exception.customExceptions.NotFoundException;
import org.example.trendyolfinalproject.mapper.OrderItemMapper;
import org.example.trendyolfinalproject.mapper.OrderMapper;
import org.example.trendyolfinalproject.model.NotificationType;
import org.example.trendyolfinalproject.model.Status;
import org.example.trendyolfinalproject.request.OrderCreateRequest;
import org.example.trendyolfinalproject.request.TransactionRequest;
import org.example.trendyolfinalproject.response.ApiResponse;
import org.example.trendyolfinalproject.response.OrderResponse;
import org.example.trendyolfinalproject.response.ReturnRequestResponse;
import org.example.trendyolfinalproject.response.SellerRevenueResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {
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
    public ApiResponse<OrderResponse> createOrder(OrderCreateRequest request) {

        Long userId = getCurrentUserId();

        log.info("Actionlog.createOrder.start : userId={}", userId);

        var user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found with id: " + userId));

        var shippingAdress = adressRepository.findById(request.getShippingAddressId()).orElseThrow(
                () -> new NotFoundException("Adress not found with id: " + request.getShippingAddressId()));

        var billingAdress = adressRepository.findById(request.getBillingAddressId()).orElseThrow(
                () -> new NotFoundException("Adress not found with id: " + request.getBillingAddressId()));


        Basket basket = basketRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("İstifadəçinin səbəti tapılmadı. İstifadəçi ID: " + userId));
        var basketElements = basketElementRepository.findByBasket_Id(basket.getId());

        BigDecimal total = BigDecimal.ZERO;
        for (BasketElement basketElement : basketElements) {
            BigDecimal price = basketElement.getProductId().getPrice();

            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(basketElement.getQuantity()));
            total = total.add(subtotal);

        }


        var paymentMethod2 = paymentMethodRepository.findByUserId_IdAndIsDefault(userId, true).orElseThrow(
                () -> new NotFoundException("User's default PaymentMethod not found. User id : " + userId)
        );
        var paymentMethodAdmin = paymentMethodRepository.findByUserId_IdAndIsDefault(9L, true).orElseThrow(
                () -> new NotFoundException("User's default PaymentMethod not found. User id : 9")
        );


        BigDecimal discountAmount = basket.getDiscountAmount() != null ? basket.getDiscountAmount() : BigDecimal.ZERO;
        BigDecimal finalAmoount = basket.getFinalAmount() != null ? basket.getFinalAmount() : BigDecimal.ZERO;


        if (paymentMethod2.getBalance().compareTo(finalAmoount) < 0) {

            var paymentTransaction = paymentTransactionService.createFailedPaymentTransaction(paymentMethod2, total, paymentMethod2.getCurrency());

            paymentTransactionService.savePaymentTransaction(paymentTransaction);
            throw new RuntimeException("PaymentMethod balance is not enough");

        } else {
            var amount = paymentMethod2.getBalance().subtract(finalAmoount);
            paymentMethod2.setBalance(amount);
            paymentMethodAdmin.setBalance(paymentMethodAdmin.getBalance().add(finalAmoount));
            paymentMethodRepository.save(paymentMethod2);
            paymentMethodRepository.save(paymentMethodAdmin);


            emailService.sendEmail(user.getEmail(), "Payment successful", "Amount of Order : " + finalAmoount);

            var order = orderMapper.toEntity(request);
            order.setTotalAmount(total);
            order.setUser(user);
            order.setShippingAddressId(shippingAdress);
            order.setBillingAddressId(billingAdress);
            order.setPaymentMethodId(paymentMethod2);
            order.setTrackingNumber(generateTrackingNumber());


            var savedOrder = orderRepository.save(order);
            shipmentService.createShipment(savedOrder);


            var paymentTransaction = paymentTransactionService.createSuccessPaymentTransaction(savedOrder);
            paymentTransactionRepository.save(paymentTransaction);


            for (BasketElement basketElement : basketElements) {
                var orderItem = orderItemMapper.toEntity(basketElement);
                orderItem.setOrderId(savedOrder);

                orderItem.setUnitPrice(basketElement.getProductId().getPrice());
                orderItemRepository.save(orderItem);
            }


            basketElementRepository.deleteAll(basketElements);

            basket.setDiscountAmount(BigDecimal.ZERO);
            basket.setFinalAmount(BigDecimal.ZERO);
            basketRepository.save(basket);


            var mapper = orderMapper.toResponse(savedOrder);

            basketRepository.save(basket);

            auditLogService.createAuditLog(user, "Order", "Order created successfully. Order id: " + savedOrder.getId());
            notificationService.sendNotification(user, "Order created successfully. Order id: " + savedOrder.getId(), NotificationType.ORDER_CREATED, savedOrder.getId());
            log.info("Actionlog.createOrder.end : userId={}", userId);
            return ApiResponse.<OrderResponse>builder()
                    .status(200)
                    .message("Order created successfully.")
                    .data(mapper)
                    .build();
        }


    }


    private String generateTrackingNumber() {
        var maxTrackingNumber = orderRepository.findMaxTrackingNumber();

        if (maxTrackingNumber == null) {
            return "000001";
        } else {

            return String.format("%06d", Integer.parseInt(maxTrackingNumber) + 1);
        }
    }


    public ApiResponse<String>  cancelOrder(Long orderId) {

        log.info("Actionlog.deleteOrder.end : orderId={}", orderId);
        Long userId = getCurrentUserId();

        var order = orderRepository.findById(orderId).orElseThrow(
                () -> new NotFoundException("Order not found with id: " + orderId));
        var own = order.getUser().getId().equals(userId);
        if (!own) {
            throw new RuntimeException("You can not cancel this order. Order id: " + orderId);
        }

        var status = order.getStatus();
        if (!(status.equals(Status.PENDING) || status.equals(Status.PROCESSING))) {
            throw new RuntimeException("Order can not be cancelled. Order id: " + orderId);
        }
        order.setStatus(Status.CANCELLED);
        orderRepository.save(order);

        //stoklara qaytarmagi yaz pulu da qaytarmagi yaz payment methoda

        List<OrderItem> orderItems = orderItemRepository.findByOrderId_Id(orderId);
        for (OrderItem orderItem : orderItems) {
            ProductVariant productVariant = orderItem.getProductVariantId();
            var product = orderItem.getProductId();

            if (productVariant == null) {
                throw new RuntimeException("Product variant is missing in order item with id: " + orderItem.getId());
            }

            int currentStock = productVariant.getStockQuantity();
            int updatedStock = currentStock + orderItem.getQuantity().intValue();
            productVariant.setStockQuantity(updatedStock);

            product.setStockQuantity(product.getStockQuantity() + orderItem.getQuantity().intValue());
            productRepository.save(product);
            productVariantRepository.save(productVariant);


        }
        orderItemRepository.deleteAll(orderItems);

        var paymentMethod = order.getPaymentMethodId();
        if (paymentMethod != null) {
            paymentMethod.setBalance(paymentMethod.getBalance().add(order.getTotalAmount()));
            paymentMethodRepository.save(paymentMethod);
        }


        auditLogService.createAuditLog(order.getUser(), "Order", "Order cancelled successfully. Order id: " + orderId);

        paymentTransactionService.returnedPaymentTransaction(order, orderId);


        return new ApiResponse<>(200, "Order cancelled successfully.", "Order id: " + orderId);

    }


    public ApiResponse<List<OrderResponse>> getOrders() {
        Long userId = getCurrentUserId();
        log.info("Actionlog.getOrdersByUserId.start : userId={}", userId);
        var orders = orderRepository.findByUserId_Id(userId);

        if (orders.isEmpty()) {
            throw new NotFoundException("Order not found");
        }

        var response = orderMapper.toResponseList(orders);
        log.info("Actionlog.getOrdersByUserId.end : userId={}", userId);
        auditLogService.createAuditLog(userRepository.findById(userId).orElseThrow(), "GET ALL ORDERS", "Orders get successfully.");
        return new ApiResponse<>(200, "Orders retrieved successfully.", response);

//orderin iceriyine baxmagi folan hamisina baxmagi get etmeyi yaz

        //cancel ordere de payment transactionu yaz pul daxil oldu kimi

    }

    public ApiResponse<List<OrderResponse>> getContinuedOrders() {

        Long userId = getCurrentUserId();
        log.info("Actionlog.getContinuedOrdersByUserId.start : userId={}", userId);

        var orders = orderRepository.findByUserId_Id(userId);
        var continuedOrders = orders.stream().filter(
                order -> order.getStatus() != Status.CANCELLED && order.getStatus() != Status.RETURNED && order.getStatus() != Status.DELIVERED).toList();
        var response = orderMapper.toResponseList(continuedOrders);
        log.info("Actionlog.getContinuedOrdersByUserId.end : userId={}", userId);
        auditLogService.createAuditLog(userRepository.findById(userId).orElseThrow(), "GET ALL CONTINUED ORDERS", "Continued orders get successfully.");
        return new ApiResponse<>(200, "Continued orders retrieved successfully.", response);
    }


    public ApiResponse<List<OrderResponse>> getCancelledOrders() {
        Long userId = getCurrentUserId();
        log.info("Actionlog.getCancelledOrdersByUserId.start : userId={}", userId);
        var orders = orderRepository.findByUserId_Id(userId);
        if (orders.isEmpty()) {
            throw new NotFoundException("Order not found with user id: " + userId);
        }
        var cancelledOrders = orders.stream().filter(
                order -> order.getStatus() == Status.CANCELLED).toList();
        var response = orderMapper.toResponseList(cancelledOrders);
        log.info("Actionlog.getCancelledOrdersByUserId.end : userId={}", userId);
        auditLogService.createAuditLog(userRepository.findById(userId).orElseThrow(), "GET ALL CANCELLED ORDERS", "Cancelled orders get successfully.");
        return new ApiResponse<>(200, "Cancelled orders retrieved successfully.", response);
    }


    public ApiResponse<List<OrderResponse>> searchProductInOrders(String productName) {

        Long userId = getCurrentUserId();

        log.info("Actionlog.searchProductInOrders.start : userId={}", userId);
        var orders = orderRepository.findByUserId_Id(userId);
        if (orders.isEmpty()) {
            throw new RuntimeException("Order not found with user id: " + userId);
        }
        var product = orderItemRepository.findOrdersByUserIdAndProductName(userId, productName);

        if (product.isEmpty()) {
            throw new NotFoundException("No orders found for userId: " + userId + " with product: " + productName);
        }
        var response = orderMapper.toResponseList(product);
        log.info("Actionlog.searchProductInOrders.end : userId={}", userId);
        auditLogService.createAuditLog(userRepository.findById(userId).orElseThrow(), "Search product in orders", "Product in Orders get successfully.");
        return new ApiResponse<>(200, "Product orders retrieved successfully.", response);

    }

    private Long getCurrentUserId() {
        return (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId");
    }

    public Map<Seller, BigDecimal> calculateSellerEarningsToday() {
        List<Order> deliveredOrders = orderRepository.findByStatus(Status.DELIVERED);

        LocalDate today = LocalDate.now();
//        LocalDateTime startOfDay = today.atStartOfDay();
//        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

      /*  if (sellerPaymentLogRepository.existsByPaymentDate(today)) {
            throw new RuntimeException("Today's earnings have already been calculated.");
        }*/

//        List<Order> todayOrders = orderRepository.findByOrderDateBetween(startOfDay, endOfDay);

        Map<Seller, BigDecimal> sellerEarnings = new HashMap<>();

        for (Order order : deliveredOrders) {

            List<OrderItem> orderItems = orderItemRepository.findByOrderId_Id(order.getId());

            for (OrderItem item : orderItems) {

                var productVariant = item.getProductVariantId();
                Seller seller = productVariant.getProduct().getSeller();


                if (sellerPaymentLogRepository.existsByOrderItemAndSeller(item, seller)) {
                    continue;
                }


                BigDecimal itemTotal = item.getUnitPrice().multiply(item.getQuantity());
                sellerEarnings.merge(seller, itemTotal, BigDecimal::add);

                SellerPaymentLog sellerPaymentLog = new SellerPaymentLog();
                sellerPaymentLog.setOrderItem(item);
                sellerPaymentLog.setSeller(seller);
                sellerPaymentLog.setPaymentDate(today);
                sellerPaymentLog.setAdminId(9L);
                sellerPaymentLogRepository.save(sellerPaymentLog);
            }
        }
        return sellerEarnings;
    }

    public ApiResponse<List<OrderResponse>> getOrdersBySeller(Long sellerId) {
        log.info("Actionlog.getOrdersBySeller.start : sellerId={}", sellerId);
        var seller = sellerRepository.findById(sellerId).orElseThrow(() -> new NotFoundException("Seller not found with id: " + sellerId));
        var orders = orderRepository.findOrdersBySellerId(sellerId);
        if (orders.isEmpty()) {
            throw new NotFoundException("No orders found for seller id: " + sellerId);
        }
        log.info("Actionlog.getOrdersBySeller.end : sellerId={}", sellerId);
        var list=orderMapper.toResponseList(orders);
        return ApiResponse.<List<OrderResponse>>builder()
                .status(200)
                .message("Orders retrieved successfully for seller id: " + sellerId)
                .data(list)
                .build();
    }


    public ApiResponse<SellerRevenueResponse> getSellerRevenueStats(Long sellerId) {
        log.info("Actionlog.getSellerRevenueStats.start : sellerId={}", sellerId);
        BigDecimal revenue = orderRepository.getTotalRevenueBySeller(sellerId);
        Long orderCount = orderRepository.getTotalOrdersBySeller(sellerId);

        var response = new SellerRevenueResponse(revenue != null ? revenue : BigDecimal.ZERO, orderCount != null ? orderCount : 0L);
        log.info("Actionlog.getSellerRevenueStats.end : sellerId={}", sellerId);
        return ApiResponse.<SellerRevenueResponse>builder()
                .status(200)
                .message("Seller revenue stats retrieved successfully for seller id: " + sellerId)
                .data(response)
                .build();
    }

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

    @Transactional
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

    private Integer generateTransactionId() {
        return (int) (Math.random() * 900000) + 100000;
    }



}


