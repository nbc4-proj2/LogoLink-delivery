package com.logilink.eureka.client.delivery.domain.model.dto;

import com.logilink.eureka.client.delivery.common.constants.DeliveryStatus;
import com.logilink.eureka.client.delivery.domain.model.Delivery;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public class RequestDto {
    @NotBlank(message = "베송 타입은 필수입니다.")
    private boolean isHubDelivery;

    private DeliveryStatus status;

    @NotBlank(message = "출발 허브는 필수입니다.")
    private UUID originHubId;

    @NotBlank(message = "도착지는 필수입니다.")
    private UUID destinationId;

    @NotBlank(message = "도착지 주소는 필수입니다.")
    private String destinationAddress;

    private Long deliveryManagerId;

    @NotBlank(message = "경로 아이디는 필수입니다.")
    private UUID routeId;

    public Delivery toDelivery() {
        return Delivery.builder()
                .isHubDelivery(isHubDelivery)
                .status(status)
                .originHubId(originHubId)
                .destinationId(destinationId)
                .destinationAddress(destinationAddress)
                .deliveryManagerId(deliveryManagerId)
                .routdId(routeId).build();
    }

}
