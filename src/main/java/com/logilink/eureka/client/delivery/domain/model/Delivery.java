package com.logilink.eureka.client.delivery.domain.model;

import com.logilink.eureka.client.delivery.common.BaseTimeEntity;
import com.logilink.eureka.client.delivery.common.constants.DeliveryStatus;
import com.logilink.eureka.client.delivery.domain.model.dto.RequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import reactor.util.annotation.Nullable;

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
    private UUID id;

    @Column(name = "is_hub_delivery", nullable = false)
    private boolean isHubDelivery;

    @Column(name = "delivery_status")
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
    private UUID routdId;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;
}
