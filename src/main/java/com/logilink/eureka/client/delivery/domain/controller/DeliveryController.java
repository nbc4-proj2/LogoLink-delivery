package com.logilink.eureka.client.delivery.domain.controller;

import com.logilink.eureka.client.delivery.common.BaseResponse;
import com.logilink.eureka.client.delivery.domain.model.entity.Delivery;
import com.logilink.eureka.client.delivery.domain.model.dto.requestDto.CreateRequestDto;
import com.logilink.eureka.client.delivery.domain.model.dto.responseDto.SearchDeliveryResponseDto;
import com.logilink.eureka.client.delivery.domain.model.dto.requestDto.UpdateRequestDto;
import com.logilink.eureka.client.delivery.domain.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1")
public class DeliveryController {
    private final DeliveryService deliveryService;

    // Todo. 게이트웨이 체인에 orderId 넣어주는지, 아님 프론트에서 넘겨주는걸로 가도 되는지 확인
    // 배송 생성
    @PostMapping("/deliveries/{orderId}")
    public BaseResponse createDelivery(@PathVariable UUID orderId, @RequestBody CreateRequestDto createRequestDto) {
        Delivery delivery = deliveryService.createDelivery(orderId, createRequestDto);
        return BaseResponse.success(delivery);
    }

    // 배송 수정
    @PatchMapping("/deliveries/{deliveryId}")
    public BaseResponse updateDelivery(@PathVariable UUID deliveryId, @RequestBody UpdateRequestDto updateRequestDto) {
        Delivery delivery = deliveryService.updateDelivery(deliveryId, updateRequestDto);
        return BaseResponse.success(delivery);
    }

    // 배송 단건 조회
    @GetMapping("/deliveries/{deliveryId}")
    public BaseResponse getDelivery(@PathVariable UUID deliveryId) {
        Delivery delivery = deliveryService.getDelivery(deliveryId);
        return BaseResponse.success(delivery);
    }

    // 배송 목록 조회
    @GetMapping("/deliveries")
    public BaseResponse getDeliveryPage(@RequestParam(required = false) String direction,
                                        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
                                              Pageable pageable) {
        if (direction != null) {
            Sort.Direction sortDirection = Sort.Direction.fromString(direction);
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(sortDirection, "createdAt")
            );
        }

        Page<Delivery> deliveryList = deliveryService.getDeliveryList(pageable);
        return BaseResponse.success(deliveryList);
    }

    //배송 현황 검색
    @GetMapping("search-deliveries/{orderId}")
    public BaseResponse searchDeliveryStatusList(@PathVariable UUID orderId){
        List<SearchDeliveryResponseDto> responseDtoList = deliveryService.searchDeliveryStatusList(orderId);
        return BaseResponse.success(responseDtoList);

    }

    // 배송 삭제
    @DeleteMapping("/deliveries/{deliveryId}")
    public BaseResponse deleteDelivery(@PathVariable UUID deliveryId) {
        // Todo. 토큰/체인에서 유저 아이디 빼와서 넘겨주기 (권한 : 마스터, 허브 관리자)
        Long userId = 1111L;

        Delivery delivery = deliveryService.deleteDelivery(deliveryId, userId);
        return BaseResponse.success(delivery);
    }

}
