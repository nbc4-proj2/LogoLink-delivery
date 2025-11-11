package com.logilink.eureka.client.delivery.domain.delivery.model.dto.responseDto;

import com.logilink.eureka.client.delivery.common.constants.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
public class SearchDeliveryResponseDto {
    private UUID deliveryId;
    private DeliveryStatus status;
    private UUID orderId;
}
