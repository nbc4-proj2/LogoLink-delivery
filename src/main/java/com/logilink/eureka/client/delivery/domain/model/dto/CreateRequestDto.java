package com.logilink.eureka.client.delivery.domain.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.logilink.eureka.client.delivery.common.constants.DeliveryStatus;
import com.logilink.eureka.client.delivery.domain.model.Delivery;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateRequestDto {
    @NotNull(message = "베송 타입은 필수입니다.")
    @JsonProperty("isHubDelivery")
    private boolean isHubDelivery;

    private DeliveryStatus status;

    @NotNull(message = "출발 허브는 필수입니다.")
    private UUID originHubId;

    @NotNull(message = "도착지는 필수입니다.")
    private UUID destinationId;

    @NotBlank(message = "도착지 주소는 필수입니다.")
    private String destinationAddress;

    private Long deliveryManagerId;

    @NotNull(message = "경로 아이디는 필수입니다.")
    private UUID routeId;

    public Delivery toDelivery() {
        return Delivery.builder()
                .isHubDelivery(isHubDelivery)
                .status(status)
                .originHubId(originHubId)
                .destinationId(destinationId)
                .destinationAddress(destinationAddress)
                .deliveryManagerId(deliveryManagerId)
                .routeId(routeId).build();
    }

}
