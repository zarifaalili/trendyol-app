package org.example.trendyolfinalproject.dao.repository;

import org.example.trendyolfinalproject.dao.entity.Product;
import org.example.trendyolfinalproject.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long> {

    @Query("select p from Product p where lower(p.name) like lower(concat('%', :name, '%') ) ")
    List<Product> findbyName(String name);

    @Query("select p from Product p left join p.variants v" +
            " GROUP BY p.id having sum(v.stockQuantity) = 0")
    List<Product> findAllOutOfStockProducts();

    List<Product> findBySellerId(Long sellerId);

    Optional<Product> findByIdAndStatus(Long id, Status status);
}
