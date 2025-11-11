package com.logilink.eureka.client.delivery.common.exception;

import lombok.Getter;

@Getter
public enum UserRole {

    MASTER("마스터"),
    HUB_MANAGER("허브 관리자"),
    COMPANY_MANAGER("업체 관리자"),
    HUB_DELIVERY_MANAGER("허브 배송 담당자"),
    COMPANY_DELIVERY_MANAGER("업체 배송 담당자");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }
}
