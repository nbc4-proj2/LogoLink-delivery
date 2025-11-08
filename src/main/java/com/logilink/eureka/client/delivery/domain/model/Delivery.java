package com.logilink.eureka.client.delivery.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.logilink.eureka.client.delivery.common.BaseTimeEntity;
import com.logilink.eureka.client.delivery.common.constants.DeliveryStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "p_deliveries")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Delivery extends BaseTimeEntity {
    @Id
    //@Column(name = "delivery_id", columnDefinition = "uuid")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "is_hub_delivery", nullable = false)
    @JsonProperty("isHubDelivery")
    private boolean isHubDelivery;

    @Column(name = "delivery_status")
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    @Column(name = "origin_hub_id", nullable = false)
    private UUID originHubId;

    @Column(name = "destination_id", nullable = false)
    private UUID destinationId;

    @Column(name = "destination_address", nullable = false)
    private String destinationAddress;

    @Column(name = "deliery_manager_id")
    private Long deliveryManagerId;

    @Column(name = "route_id", nullable = false)
    private UUID routeId;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;
}
