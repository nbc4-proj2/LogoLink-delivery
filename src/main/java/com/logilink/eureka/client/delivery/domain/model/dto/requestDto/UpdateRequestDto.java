package com.logilink.eureka.client.delivery.domain.model.dto.requestDto;

import com.logilink.eureka.client.delivery.common.constants.DeliveryStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRequestDto {

    private DeliveryStatus status;
    private Long deliveryManagerId;
}
