package org.example.trendyolfinalproject.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.dao.entity.Book;
import org.example.trendyolfinalproject.dao.entity.BookOrder;
import org.example.trendyolfinalproject.dao.repository.BookOrderRepository;
import org.example.trendyolfinalproject.dao.repository.BookRepository;
import org.example.trendyolfinalproject.dao.repository.PaymentMethodRepository;
import org.example.trendyolfinalproject.dao.repository.UserRepository;
import org.example.trendyolfinalproject.model.enums.NotificationType;
import org.example.trendyolfinalproject.service.AuditLogService;
import org.example.trendyolfinalproject.service.BookOrderService;
import org.example.trendyolfinalproject.service.NotificationService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class BookOrderServiceImpl implements BookOrderService {

    private final BookRepository bookRepository;
    private final BookOrderRepository bookOrderRepository;
    private final UserRepository userRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    @Transactional
    @Override
    public Long createOrder(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        var userId = getCurrentUserId();
        var seller = book.getSeller().getUser().getId();
        var user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        var paymentMethod = paymentMethodRepository.findByUserId_IdAndIsDefault(user.getId(), true).orElseThrow(() -> new RuntimeException("Payment method not found"));

        if (paymentMethod.getBalance().compareTo(BigDecimal.valueOf(book.getPrice())) < 0) {
            throw new RuntimeException("Not enough balance");
        }

        paymentMethod.setBalance(paymentMethod.getBalance().subtract(BigDecimal.valueOf(book.getPrice())));

        var paymentMethodSeller = paymentMethodRepository.findByUserId_IdAndIsDefault(seller, true).orElseThrow(() -> new RuntimeException("Payment method not found"));
        paymentMethodSeller.setBalance(paymentMethodSeller.getBalance().add(BigDecimal.valueOf(book.getPrice())));
        paymentMethodRepository.save(paymentMethod);
        var lastBycount=book.getBuyCount()+1;
        BookOrder order = new BookOrder();
        order.setBook(book);
        order.setUser(user);
        order.setPaid(true);
        book.setBuyCount(lastBycount);
        bookOrderRepository.save(order);

        auditLogService.createAuditLog(user, "Book ordered", "Book ordered successfully. Book id: " + book.getId());
        notificationService.sendNotification(user, "Book ordered", NotificationType.BOOK_ORDER, book.getId());
        return order.getId();
    }

    @Override
    public ResponseEntity<Resource> readBook(Long orderId) {
        var userId = getCurrentUserId();
        var user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        BookOrder order = bookOrderRepository.findByIdAndUserIdAndIsPaidTrue(orderId, user.getId())
                .orElseThrow(() -> new RuntimeException("Order not paid or not found"));

        Path path = Paths.get(System.getProperty("user.dir") + order.getBook().getFilePath());
        Resource resource;
        try {
            resource = new UrlResource(path.toUri());
            if (!resource.exists()) throw new FileNotFoundException("Book file not found");
        } catch (MalformedURLException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        auditLogService.createAuditLog(user, "Book read", "Book read successfully. Book id: " + order.getBook().getId());
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + order.getBook().getTitle() + "\"");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }



    @Override
    public ResponseEntity<Resource> readBookUnpaid(Long orderId) {


        BookOrder order = bookOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not paid or not found"));

        Path path = Paths.get(System.getProperty("user.dir") + order.getBook().getFilePath());
        Resource resource;
        try {
            resource = new UrlResource(path.toUri());
            if (!resource.exists()) throw new FileNotFoundException("Book file not found");
        } catch (MalformedURLException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + order.getBook().getTitle() + "\"");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }


    private Long getCurrentUserId() {
        return (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId");
    }
}

