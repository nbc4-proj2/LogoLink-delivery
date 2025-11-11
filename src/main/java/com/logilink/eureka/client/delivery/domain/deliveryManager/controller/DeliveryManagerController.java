package com.logilink.eureka.client.delivery.domain.deliveryManager.controller;

import com.logilink.eureka.client.delivery.common.BaseResponse;
import com.logilink.eureka.client.delivery.common.constants.DeliveryUserType;
import com.logilink.eureka.client.delivery.domain.deliveryManager.model.dto.CreateRequestDto;
import com.logilink.eureka.client.delivery.domain.deliveryManager.model.entity.DeliveryManager;
import com.logilink.eureka.client.delivery.domain.deliveryManager.service.DeliveryManagerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DeliveryManagerController {
    private final DeliveryManagerService deliveryManagerService;

    @PostMapping
    public BaseResponse<DeliveryManager> createDeliveryManager(@Valid @RequestBody CreateRequestDto requestDto) {
        DeliveryManager manager;

        // 허브 배송
        if (requestDto.getDeliveryType() == DeliveryUserType.HUB) {
            // 허브 배송 매니저는 hubId 없으니까 service 쪽에서 null로 넣는 버전 호출
            manager = deliveryManagerService.createHubManager(requestDto);
        }
        // 업체 배송
        else  {
            // 업체 배송 매니저는 허브에 소속이니까 hubId 필요
//            if (requestDto.getHubId() == null) {
//                throw AppException.of(DeliveryErrorCode.HUB_IS_NOT_EXISTING);
//            }
            manager = deliveryManagerService.createStoreManager(requestDto);
        }

        return BaseResponse.success(manager);
    }
}
