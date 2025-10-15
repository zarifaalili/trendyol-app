package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.Shipment;
import org.example.trendyolfinalproject.dao.entity.ShipmentHistory;
import org.example.trendyolfinalproject.model.Status;
import org.example.trendyolfinalproject.model.request.ShipmentHistoryCreateRequest;
import org.example.trendyolfinalproject.model.response.ShipmentHistoryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

class ShipmentHistoryMapperTest {

    private ShipmentHistoryMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(ShipmentHistoryMapper.class);
    }

    @Test
    void testToEntityMapping() {
        ShipmentHistoryCreateRequest request = new ShipmentHistoryCreateRequest(
                1L,
                Status.SHIPPED,
                "Baku Warehouse",
                LocalDateTime.now()
        );

        ShipmentHistory entity = mapper.toEntity(request);

        assertNotNull(entity);
        assertNull(entity.getId()); // id ignore edilib
        assertNull(entity.getShipment()); // shipment ignore edilib
        assertEquals(request.getStatus(), entity.getStatus());
        assertEquals(request.getLocation(), entity.getLocation());
        assertEquals(request.getTimestamp(), entity.getTimestamp());
    }

    @Test
    void testToResponseMapping() {
        Shipment shipment = new Shipment();
        shipment.setId(5L);

        ShipmentHistory history = new ShipmentHistory();
        history.setId(10L);
        history.setShipment(shipment);
        history.setStatus(Status.DELIVERED);
        history.setLocation("Baku Central");
        history.setTimestamp(LocalDateTime.now());

        ShipmentHistoryResponse response = mapper.toResponse(history);

        assertNotNull(response);
        assertEquals(history.getId(), response.getId());
        assertEquals(shipment.getId(), response.getShipmentId());
        assertEquals(history.getStatus(), response.getStatus());
        assertEquals(history.getLocation(), response.getLocation());
        assertEquals(history.getTimestamp(), response.getTimestamp());
    }

    @Test
    void testToResponseListMapping() {
        Shipment shipment1 = new Shipment();
        shipment1.setId(1L);
        Shipment shipment2 = new Shipment();
        shipment2.setId(2L);

        ShipmentHistory history1 = new ShipmentHistory(1L, shipment1, Status.SHIPPED, "Loc1", LocalDateTime.now());
        ShipmentHistory history2 = new ShipmentHistory(2L, shipment2, Status.DELIVERED, "Loc2", LocalDateTime.now());

        List<ShipmentHistory> histories = Arrays.asList(history1, history2);
        List<ShipmentHistoryResponse> responses = mapper.toResponseList(histories);

        assertEquals(histories.size(), responses.size());
        assertEquals(histories.get(0).getId(), responses.get(0).getId());
        assertEquals(histories.get(1).getShipment().getId(), responses.get(1).getShipmentId());
    }
}
