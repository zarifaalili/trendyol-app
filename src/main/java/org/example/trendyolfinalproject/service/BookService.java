package org.example.trendyolfinalproject.service;

import org.springframework.web.multipart.MultipartFile;

public interface BookService {

    void uploadBook(MultipartFile bookFile, String title, String author, Double price);
}
