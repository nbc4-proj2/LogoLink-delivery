package com.logilink.eureka.client.delivery.domain.delivery.model.dto.responseDto;

import com.logilink.eureka.client.delivery.common.constants.DeliveryStatus;
import com.logilink.eureka.client.delivery.domain.delivery.model.entity.Delivery;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class DeleteResponseDto {
    private UUID deliveryId;

    private boolean isHubDelivery;

    private DeliveryStatus status;

    private UUID originHubId;

    private UUID destinationId;

    private String destinationAddress;

    private Long deliveryManagerId;

    private UUID routeId;

    private UUID orderId;

    private LocalDateTime deletedAt;

    private Long deletedBy;

    public DeleteResponseDto(Delivery delivery) {
        this.deliveryId = delivery.getId();
        this.isHubDelivery = delivery.isHubDelivery();
        this.status = delivery.getStatus();
        this.originHubId = delivery.getOriginHubId();
        this.destinationId = delivery.getDestinationId();
        this.destinationAddress = delivery.getDestinationAddress();
        this.deliveryManagerId = delivery.getDeliveryManagerId();
        this.routeId = delivery.getRouteId();
        this.orderId = delivery.getOrderId();
        this.deletedAt = delivery.getDeletedAt();
        this.deletedBy = delivery.getDeletedBy();
    }

}
