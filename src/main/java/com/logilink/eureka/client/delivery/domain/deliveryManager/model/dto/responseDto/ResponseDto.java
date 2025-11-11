package com.logilink.eureka.client.delivery.domain.deliveryManager.model.dto.responseDto;

import com.logilink.eureka.client.delivery.common.constants.DeliveryUserType;
import com.logilink.eureka.client.delivery.domain.deliveryManager.model.entity.DeliveryManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ResponseDto {
    private Long id;

    private UUID hubId;

    private String slackId;

    private DeliveryUserType deliveryType;

    private Long deliverySeq;

    public ResponseDto(DeliveryManager deliveryManager) {
        this.id = deliveryManager.getId();
        this.hubId = deliveryManager.getHubId();
        this.slackId = deliveryManager.getSlackId();
        this.deliveryType = deliveryManager.getDeliveryType();
        this.deliverySeq = deliveryManager.getDeliverySeq();
    }
}
