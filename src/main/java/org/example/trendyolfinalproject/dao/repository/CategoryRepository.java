package org.example.trendyolfinalproject.dao.repository;

import org.example.trendyolfinalproject.dao.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;


public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("select c from Category c where c.parentCategory is null")
    List<Category> findAllParentCategories();
}
