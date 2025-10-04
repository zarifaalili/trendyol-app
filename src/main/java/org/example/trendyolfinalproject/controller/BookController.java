package org.example.trendyolfinalproject.controller;
import lombok.RequiredArgsConstructor;
import org.example.trendyolfinalproject.service.BookOrderService;
import org.example.trendyolfinalproject.service.BookService;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType; // <-- Import MediaType
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final BookOrderService bookOrderService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<String> uploadBook(
            @RequestParam("bookFile") MultipartFile bookFile,
            @RequestParam("title") String title,
            @RequestParam(value = "author", required = false) String author,
            @RequestParam(value = "price", required = false) Double price) {

        bookService.uploadBook(bookFile, title, author, price);
        return ResponseEntity.ok("Book uploaded successfully");
    }

    @PostMapping("/{bookId}/orders")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Long> orderBook(@PathVariable Long bookId) {
        Long orderId = bookOrderService.createOrder(bookId);
        return ResponseEntity.ok(orderId);
    }

    @GetMapping("/orders/{orderId}/file")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Resource> readBook(@PathVariable Long orderId) {
        return bookOrderService.readBook(orderId);
    }

    @GetMapping("/orders/{orderId}/file-unpaid")
    public ResponseEntity<Resource> readBookUnpaid(@PathVariable Long orderId) {
        return bookOrderService.readBookUnpaid(orderId);
    }
}