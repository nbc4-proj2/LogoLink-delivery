package com.logilink.eureka.client.delivery.common.constants;

import lombok.Getter;

/**
 * 배송 상태
 */
@Getter
public enum DeliveryStatus {

    HUB_PENDING("허브 대기중"),
    MIDDLE_HUB_PENDING("중간 허브 대기중"),
    TO_HUB("허브 이동중"),
    DESTINATION_HUB_ARRIVED("목적지 허브 도착"),
    MIDDLE_HUB_ARRIVED("중간 허브 도착"),
    TO_COMPANY("업체 이동중"),
    DONE("배송완료");

    private final String status;

    DeliveryStatus(String status) {
        this.status = status;
    }
}
