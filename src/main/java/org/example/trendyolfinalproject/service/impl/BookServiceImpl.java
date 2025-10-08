package org.example.trendyolfinalproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.entity.Book;
import org.example.trendyolfinalproject.dao.repository.BookRepository;
import org.example.trendyolfinalproject.dao.repository.SellerRepository;
import org.example.trendyolfinalproject.dao.repository.UserRepository;
import org.example.trendyolfinalproject.service.BookService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;


    @Override
    public void uploadBook(MultipartFile bookFile, String title, String author, Double price) {
        log.info("Actionlog.uploadBook.start : ");
        var userId = getCurrentUserId();
        var seller = sellerRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("Seller not found with userId: " + userId));

        try {
            String fileName = UUID.randomUUID() + "_" + bookFile.getOriginalFilename();
            Path uploadPath = Paths.get("books");

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(bookFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            Book book = new Book();
            book.setTitle(title);
            book.setAuthor(author);
            book.setPrice(price);
            book.setFilePath("/books/" + fileName);
            book.setSeller(seller);

            bookRepository.save(book);

        } catch (IOException e) {
            throw new RuntimeException("Could not store book file", e);
        }
    }

    private Long getCurrentUserId() {
        return (Long) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getAttribute("userId");
    }

}

