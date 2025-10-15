package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.ProductQuestion;
import org.example.trendyolfinalproject.dao.entity.ProductVariant;
import org.example.trendyolfinalproject.dao.entity.Product;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.dao.entity.Seller;
import org.example.trendyolfinalproject.model.Status;
import org.example.trendyolfinalproject.model.request.ProductQuestionRequest;
import org.example.trendyolfinalproject.model.response.ProductQuestionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductQuestionMapperTest {

    private ProductQuestionMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(ProductQuestionMapper.class);
    }

    @Test
    void testToEntityMapping() {
        ProductQuestionRequest request = new ProductQuestionRequest(1L, "What is the warranty?");
        ProductQuestion entity = mapper.toEntity(request);

        assertNotNull(entity);
        assertNull(entity.getId()); // ignore edilib
        assertEquals(request.getQuestion(), entity.getQuestion());
    }

    @Test
    void testToResponseMapping() {
        User customer = new User();
        customer.setName("Zari Aliyeva");

        Product product = new Product();
        product.setName("Laptop");

        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);

        Seller seller = new Seller();
        seller.setCompanyName("TechStore");

        ProductQuestion question = new ProductQuestion();
        question.setQuestion("What is the warranty?");
        question.setAnswer("2 years");
        question.setCustomer(customer);
        question.setProductVariant(variant);
        question.setSeller(seller);
        question.setCreatedAt(LocalDateTime.now());
        question.setAnsweredAt(LocalDateTime.now());
        question.setStatus(Status.PENDING);

        ProductQuestionResponse response = mapper.toResponse(question);

        assertNotNull(response);
        assertEquals(question.getQuestion(), response.getQuestion());
        assertEquals(question.getAnswer(), response.getAnswer());
        assertEquals(customer.getName(), response.getCustomerName());
        assertEquals(product.getName(), response.getProductName());
        assertEquals(seller.getCompanyName(), response.getSellerName());
        assertEquals(question.getCreatedAt(), response.getCreatedAt());
        assertEquals(question.getAnsweredAt(), response.getAnsweredAt());
    }

    @Test
    void testToResponseListMapping() {
        User customer = new User();
        customer.setName("Zari Aliyeva");

        Product product = new Product();
        product.setName("Laptop");

        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);

        Seller seller = new Seller();
        seller.setCompanyName("TechStore");

        ProductQuestion q1 = new ProductQuestion();
        q1.setQuestion("Q1?");
        q1.setCustomer(customer);
        q1.setProductVariant(variant);
        q1.setSeller(seller);

        ProductQuestion q2 = new ProductQuestion();
        q2.setQuestion("Q2?");
        q2.setCustomer(customer);
        q2.setProductVariant(variant);
        q2.setSeller(seller);

        List<ProductQuestionResponse> responses = mapper.toResponseList(List.of(q1, q2));

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Q1?", responses.get(0).getQuestion());
        assertEquals("Q2?", responses.get(1).getQuestion());
    }
}
