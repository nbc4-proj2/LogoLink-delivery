package com.logilink.eureka.client.delivery.domain.delivery.model.dto.requestDto;

import com.logilink.eureka.client.delivery.domain.delivery.model.entity.Delivery;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeliveryCreateRequestDto {
    @NotNull(message = "주문 아이디는 필수입니다.")
    private UUID orderId;

    @NotNull(message = "출발 허브는 필수입니다.")
    private UUID originHubId;

    @NotNull(message = "도착지는 필수입니다.")
    private UUID destinationId;

    @NotBlank(message = "도착지 주소는 필수입니다.")
    @Size(max = 255)
    private String destinationAddress;



    public Delivery toDelivery() {
        return Delivery.builder()
                .originHubId(originHubId)
                .destinationId(destinationId)
                .destinationAddress(destinationAddress)
                .build();

    }

}
