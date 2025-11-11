package com.logilink.eureka.client.delivery.domain.delivery.feignClient;

import com.logilink.eureka.client.delivery.domain.delivery.model.dto.responseDto.HubRouteResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "hub-service")
public interface HubRouteClient {

    @GetMapping("/api/v1/hub-routes")
    HubRouteResponseDto getHubRoutes(
            @RequestParam("originHubId") UUID originHubId,
            @RequestParam("destinationId") UUID destinationId    // 업체 아이디
    );
}
