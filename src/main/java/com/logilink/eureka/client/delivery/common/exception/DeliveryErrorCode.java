package com.logilink.eureka.client.delivery.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum DeliveryErrorCode implements ErrorCode{
    DELIVERY_IS_NOT_EXISTING("DELIVERY0001", "해당 배송이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    DELIVERY_PAGEABLE_IS_NOT_EXISTING("DELIVERY0002", "배송 pageable이 존재하지 않습니다.", HttpStatus.NO_CONTENT),
    DELIVERY_BY_ORDERID_IS_NOT_EXISTING("DELIVERY0003", "해당 주문에 존재하는 배송이 없습니다.", HttpStatus.NOT_FOUND),
    DELIVERY_ALREADY_COMPLETED("DELIVERY0004", "이미 배송이 완료된 건은 수정할 수 없습니다.", HttpStatus.BAD_REQUEST),
    HUB_ROUTE_IS_NOT_EXISTING("DELIVERY0005", "허브 경로가 존재하지 않습니다.", HttpStatus.NO_CONTENT),
    DELIVERY_MANAGER_IS_NOT_EXISTING("DELIVERY0006", "배송 매니저가 존재하지 않습니다.", HttpStatus.NO_CONTENT),
    HUB_IS_NOT_EXISTING("DELIVERY0007", "해당 허브가 존재하지 않습니다", HttpStatus.NOT_FOUND),
    FAILED_GET_OR_SEARCH_DELIVERY_MANAGER("DELIVERY0008", "배송 매니저 조회 및 검색에 실패했습니다.",
            HttpStatus.FORBIDDEN),
    DATA_IS_NOT_EXISTING("DELIVERY0009", "데이터가 존재하지 않습니다.", HttpStatus.NO_CONTENT)
    ;

    private final String code;
    private final String message;
    private final HttpStatus status;

    DeliveryErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
