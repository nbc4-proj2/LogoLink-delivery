package com.logilink.eureka.client.delivery.domain.delivery.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.logilink.eureka.client.delivery.common.BaseTimeEntity;
import com.logilink.eureka.client.delivery.common.constants.DeliveryStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "p_deliveries")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Delivery extends BaseTimeEntity {
    @Id
    @Column(name = "delivery_id", columnDefinition = "uuid")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "is_hub_delivery", nullable = false)
    @JsonProperty("isHubDelivery")
    private boolean isHubDelivery;

    @Setter
    @Column(name = "delivery_status")
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    @Column(name = "origin_hub_id", nullable = false)
    private UUID originHubId;

    @Column(name = "destination_id", nullable = false)
    private UUID destinationId;

    @Column(name = "destination_address", length = 255, nullable = false)
    private String destinationAddress;

    @Setter
    @Column(name = "delivery_manager_id")
    private Long deliveryManagerId;

    @Column(name = "route_id", nullable = false)
    private UUID routeId;

    @Setter
    @Column(name = "order_id", nullable = false)
    private UUID orderId;
}
