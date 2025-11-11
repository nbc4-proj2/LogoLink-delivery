package com.logilink.eureka.client.delivery.domain.deliveryManager.model.dto.requestDto;

import com.logilink.eureka.client.delivery.common.constants.DeliveryUserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ManagerUpdateRequestDto {

    private UUID hubId;

    private String slackId;

    private DeliveryUserType deliveryType;

    private Long deliverySeq;
}
