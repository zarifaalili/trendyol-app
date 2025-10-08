package org.example.trendyolfinalproject.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

public interface BookOrderService {

    Long createOrder(Long bookId);

    ResponseEntity<Resource> readBook(Long orderId);

    ResponseEntity<Resource> readBookUnpaid(Long orderId);




}
