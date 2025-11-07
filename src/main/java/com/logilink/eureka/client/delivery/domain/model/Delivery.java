package com.logilink.eureka.client.delivery.domain.model;

import com.logilink.eureka.client.delivery.common.BaseTimeEntity;
import com.logilink.eureka.client.delivery.common.constants.DeliveryStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "p_deliveries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Delivery extends BaseTimeEntity {
    @Id
    private UUID id;

    @Column(name = "is_hub_delivery")
    private boolean isHubDelivery;

    @Column(name = "delivery_status")
    private DeliveryStatus status;

    @Column(name = "origin_hub_id")
    private UUID origiHubId;

    @Column(name = "destination_id")
    private UUID destinationId;

    @Column(name = "destination_address")
    private String destinationAddress;

    @Column(name = "deliery_manager_id")
    private Long deliveryManagerId;

    @Column(name = "route_id")
    private UUID routdId;

    @Column(name = "order_id")
    private UUID orderId;

}
