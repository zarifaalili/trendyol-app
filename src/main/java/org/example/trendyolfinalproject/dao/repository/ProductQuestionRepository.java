package org.example.trendyolfinalproject.dao.repository;

import jdk.dynalink.linker.LinkerServices;
import org.example.trendyolfinalproject.dao.entity.ProductQuestion;
import org.example.trendyolfinalproject.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductQuestionRepository extends JpaRepository<ProductQuestion, Long> {
    List<ProductQuestion> findAllByProductVariantId(Long productVariantId);
    List<ProductQuestion> findAllByProductVariantIdAndStatus(Long productVariantId, Status status);
}
