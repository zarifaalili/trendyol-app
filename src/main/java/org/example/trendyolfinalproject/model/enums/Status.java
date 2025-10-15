package org.example.trendyolfinalproject.model.enums;

public enum Status {
    ACTIVE,
    BLOCKED,
    DRAFT,
    OUT_OF_STOCK,
    RETURNED,
    SUCCESS,
    INACTIVE,

// shipment ucun
    PENDING,
    PROCESSING,
    SHIPPED,
    IN_TRANSIT,
    OUT_FOR_DELIVERY,
    DELIVERED,
    FAILED,
    CANCELLED,
    AT_DELIVERY_HUB,
    IN_CUSTOMS,


//shipment history ucun
    ORDER_PLACED,
    PACKAGE_PREPARED,
    DISPATCHED,
    ARRIVED_AT_CENTER,
    FAILED_DELIVERY,


//question ucun
    ANSWERED,
    REJECTED,
    COMPLETED
}
