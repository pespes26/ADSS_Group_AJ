package com.superli.deliveries.domain.core;

/**
 * Represents the status of a delivery destination.
 */
public enum DestinationStatus {
    /**
     * Destination is pending delivery.
     */
    PENDING,

    /**
     * Destination is currently being delivered to.
     */
    IN_DELIVERY,

    /**
     * Destination has been delivered to.
     */
    DELIVERED,

    /**
     * Delivery to destination has been cancelled.
     */
    CANCELLED
} 