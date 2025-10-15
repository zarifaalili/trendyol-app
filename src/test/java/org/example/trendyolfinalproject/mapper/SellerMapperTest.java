package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.Seller;
import org.example.trendyolfinalproject.dao.entity.User;
import org.example.trendyolfinalproject.model.request.SellerCreateRequest;
import org.example.trendyolfinalproject.model.request.SellerUpdateRequest;
import org.example.trendyolfinalproject.model.response.SellerResponse;
import org.example.trendyolfinalproject.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class SellerMapperTest {

    private SellerMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(SellerMapper.class);
    }

    @Test
    void testToEntity() {
        SellerCreateRequest request = new SellerCreateRequest();
        request.setUserId(1L);
        request.setCompanyName("TechStore");
        request.setTaxId(123456);

        Seller entity = mapper.toEntity(request);

        assertNotNull(entity);
        assertNull(entity.getId());
        assertEquals("TechStore", entity.getCompanyName());
        assertEquals(123456, entity.getTaxId());
        assertNull(entity.getUser());
    }

    @Test
    void testUpdateEntity() {
        Seller seller = new Seller();
        seller.setCompanyName("OldName");
        seller.setTaxId(111111);

        SellerUpdateRequest updateRequest = new SellerUpdateRequest();


        mapper.updateEntity(seller, updateRequest);

        assertEquals("OldName", seller.getCompanyName());
        assertEquals(111111, seller.getTaxId());
    }

    @Test
    void testToResponseMapping() {
        User user = new User();
        user.setId(1L);
        user.setName("Zari");
        user.setSurname("Aliyeva");
        user.setEmail("zari@example.com");

        Seller seller = new Seller();
        seller.setId(10L);
        seller.setUser(user);
        seller.setCompanyName("TechStore");
        seller.setTaxId(123456);
        seller.setContactEmail("contact@techstore.com");
        seller.setStatus(Status.ACTIVE);

        SellerResponse response = mapper.toResponse(seller);

        assertNotNull(response);
        assertEquals(seller.getId(), response.getId());
        assertEquals(user.getId(), response.getUserId());
        assertEquals("Zari Aliyeva", response.getUserName());
        assertEquals(user.getEmail(), response.getUserEmail());
        assertEquals("TechStore", response.getCompanyName());
        assertEquals(123456, response.getTaxId());
        assertEquals("contact@techstore.com", response.getContactEmail());
        assertEquals(Status.ACTIVE, response.getStatus());
    }
}
