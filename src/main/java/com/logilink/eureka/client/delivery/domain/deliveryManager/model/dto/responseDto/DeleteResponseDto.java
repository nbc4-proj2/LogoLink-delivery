package com.logilink.eureka.client.delivery.domain.deliveryManager.model.dto.responseDto;

import com.logilink.eureka.client.delivery.common.constants.DeliveryUserType;
import com.logilink.eureka.client.delivery.domain.deliveryManager.model.entity.DeliveryManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class DeleteResponseDto {
    private Long id;

    private UUID hubId;

    private String slackId;

    private DeliveryUserType deliveryType;

    private Long deliverySeq;

    private LocalDateTime deletedAt;

    private Long deletedBy;

    public DeleteResponseDto(DeliveryManager deliveryManager) {
        this.id = deliveryManager.getId();
        this.hubId = deliveryManager.getHubId();
        this.slackId = deliveryManager.getSlackId();
        this.deliveryType = deliveryManager.getDeliveryType();
        this.deliverySeq = deliveryManager.getDeliverySeq();
        this.deletedAt = deliveryManager.getDeletedAt();
        this.deletedBy = deliveryManager.getDeletedBy();
    }
}
