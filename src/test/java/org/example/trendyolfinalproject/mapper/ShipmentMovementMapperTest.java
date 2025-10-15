package org.example.trendyolfinalproject.mapper;

import org.example.trendyolfinalproject.dao.entity.Shipment;
import org.example.trendyolfinalproject.dao.entity.ShipmentMovement;
import org.example.trendyolfinalproject.model.request.ShipmentMovementCreateRequest;
import org.example.trendyolfinalproject.model.response.ShipmentMovementResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShipmentMovementMapperTest {

    private ShipmentMovementMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(ShipmentMovementMapper.class);
    }

    @Test
    void testToEntityMapping() {
        ShipmentMovementCreateRequest request = new ShipmentMovementCreateRequest();
        request.setShipmentId(1L);
        request.setActionNote("Package picked up");
        request.setLocation("Baku Warehouse");
        request.setUpdatedBy("System");

        ShipmentMovement entity = mapper.toEntity(request);

        assertNotNull(entity);
        assertNull(entity.getId()); // id ignore edilib
        assertNull(entity.getShipment()); // shipment ignore edilib
        assertEquals(request.getActionNote(), entity.getActionNote());
        assertEquals(request.getLocation(), entity.getLocation());
        assertEquals(request.getUpdatedBy(), entity.getUpdatedBy());
    }

    @Test
    void testToResponseMapping() {
        Shipment shipment = new Shipment();
        shipment.setId(5L);

        ShipmentMovement movement = new ShipmentMovement();
        movement.setId(10L);
        movement.setShipment(shipment);
        movement.setActionNote("Delivered");
        movement.setLocation("Baku Central");
        movement.setUpdatedBy("Admin");

        ShipmentMovementResponse response = mapper.toResponse(movement);

        assertNotNull(response);
        assertEquals(movement.getId(), response.getId());
        assertEquals(shipment.getId(), response.getShipmentId());
        assertEquals(movement.getActionNote(), response.getActionNote());
        assertEquals(movement.getLocation(), response.getLocation());
        assertEquals(movement.getUpdatedBy(), response.getUpdatedBy());
    }

    @Test
    void testToResponseListMapping() {
        Shipment shipment1 = new Shipment();
        shipment1.setId(1L);
        Shipment shipment2 = new Shipment();
        shipment2.setId(2L);

        ShipmentMovement movement1 = new ShipmentMovement(1L, shipment1, "Picked up", "Loc1", "User1", null);
        ShipmentMovement movement2 = new ShipmentMovement(2L, shipment2, "In Transit", "Loc2", "User2", null);

        List<ShipmentMovement> movements = Arrays.asList(movement1, movement2);
        List<ShipmentMovementResponse> responses = movements.stream()
                .map(mapper::toResponse)
                .toList();

        assertEquals(movements.size(), responses.size());
        assertEquals(movements.get(0).getId(), responses.get(0).getId());
        assertEquals(movements.get(1).getShipment().getId(), responses.get(1).getShipmentId());
    }
}
