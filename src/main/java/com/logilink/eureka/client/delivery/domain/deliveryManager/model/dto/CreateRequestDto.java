package com.logilink.eureka.client.delivery.domain.deliveryManager.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.logilink.eureka.client.delivery.common.constants.DeliveryUserType;
import com.logilink.eureka.client.delivery.domain.deliveryManager.model.entity.DeliveryManager;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateRequestDto {
    @NotNull(message = "배송 매니저 아이디(유저 아이디)는 필수입니다.")
    private Long id;

    private UUID hubId;

    @NotBlank(message = "슬랙 아이디는 필수입니다.")
    private String slackId;

    @NotNull(message = "배송 타입은 필수입니다.")
    private DeliveryUserType deliveryType;

    @NotNull(message = "배송 순번은 필수입니다.")
    private Long deliverySeq;

//    @NotNull(message = "배송 가능 여부는 필수입니다.")
//    @JsonProperty("isDeliveryAvailable")
//    private Boolean isDeliveryAvailable;
}
