package com.logilink.eureka.client.delivery.domain.controller;

import com.logilink.eureka.client.delivery.common.BaseResponse;
import com.logilink.eureka.client.delivery.domain.model.Delivery;
import com.logilink.eureka.client.delivery.domain.model.dto.RequestDto;
import com.logilink.eureka.client.delivery.domain.service.DeliveryService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1")
public class DeliveryController {
    private final DeliveryService deliveryService;

    // Todo. 게이트웨이 체인에 orderId 넣어주는지, 아님 프론트에서 넘겨주는걸로 가도 되는지 확인
    @PostMapping("/deliveries/{orderId}")
    public BaseResponse createDelivery(@PathVariable UUID orderId, @RequestBody RequestDto requestDto) {
        Delivery result = deliveryService.createDelivery(orderId, requestDto);
        return BaseResponse.success(result);
    }

}
