package org.example.trendyolfinalproject.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trendyolfinalproject.dao.entity.Product;
import org.example.trendyolfinalproject.dao.repository.ProductRepository;
import org.example.trendyolfinalproject.model.enums.Status;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductScheduler {
    private final ProductRepository productRepository;

    @Transactional
    @Scheduled(cron = "*/30 * * * * *")
    public void updateOutOfStockProducts() {
        log.info("ProductScheduler: updateOutOfStockProducts started at {}", LocalDateTime.now());
        List<Product> outOfStockProduct = productRepository.findAllOutOfStockProducts();

        List<Product> updatedProducts = new ArrayList<>();
        for (Product product : outOfStockProduct) {
            if (Status.ACTIVE.equals(product.getStatus())) {
                product.setStatus(Status.INACTIVE);
                product.setUpdatedAt(LocalDateTime.now());
                updatedProducts.add(product);

            }
        }
        productRepository.saveAll(updatedProducts);
        log.info("ProductScheduler: updateOutOfStockProducts ended at {}", LocalDateTime.now());

    }
}
