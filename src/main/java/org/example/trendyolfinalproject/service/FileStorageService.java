package org.example.trendyolfinalproject.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String storeFile(MultipartFile file);

    String storeFilee(MultipartFile file);
}
