package com.logilink.eureka.client.delivery.domain.delivery.controller;

import com.logilink.eureka.client.delivery.common.BaseResponse;
import com.logilink.eureka.client.delivery.common.exception.ApiErrorCode;
import com.logilink.eureka.client.delivery.common.exception.AppException;
import com.logilink.eureka.client.delivery.common.exception.UserRole;
import com.logilink.eureka.client.delivery.domain.delivery.model.dto.responseDto.DeleteResponseDto;
import com.logilink.eureka.client.delivery.domain.delivery.model.dto.responseDto.ResponseDto;
import com.logilink.eureka.client.delivery.domain.delivery.model.dto.requestDto.DeliveryCreateRequestDto;
import com.logilink.eureka.client.delivery.domain.delivery.model.dto.responseDto.SearchDeliveryResponseDto;
import com.logilink.eureka.client.delivery.domain.delivery.model.dto.requestDto.UpdateRequestDto;
import com.logilink.eureka.client.delivery.domain.delivery.service.DeliveryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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

    // Todo. 권한 확인 ok
    // 배송 생성
    @PostMapping("/deliveries/{orderId}")
    public BaseResponse createDeliverieList(@PathVariable UUID orderId, @RequestBody DeliveryCreateRequestDto deliveryCreateRequestDto, HttpServletRequest request) {
        // 헤더 꺼내기
        String roleHeader = request.getHeader("X-User-Role");

        if(roleHeader.equals(UserRole.MASTER.name())) {
            List<ResponseDto> responseDtoList = deliveryService.createDeliverieList(deliveryCreateRequestDto);
            return BaseResponse.success(responseDtoList);
        } else {
            return BaseResponse.fail(AppException.of(ApiErrorCode.FORBIDDEN));
        }
    }

    // Todo. 권한 확인
    // 배송 수정
    @PatchMapping("/deliveries/{deliveryId}")
    public BaseResponse updateDelivery(@PathVariable UUID deliveryId, @Valid @RequestBody UpdateRequestDto updateRequestDto, HttpServletRequest request) {
        // 헤더 꺼내기
        String userIdHeader = request.getHeader("X-User-Id");
        String roleHeader = request.getHeader("X-User-Role");
        String hubIdHeader = request.getHeader("X-Hub-Id");

        // 캐스팅
        Long userId = Long.parseLong(userIdHeader);
        UUID hubId = hubIdHeader != null ? UUID.fromString(hubIdHeader) : null;

        // 권한 검증
        if(roleHeader.equals(UserRole.MASTER.name()) || roleHeader.equals(UserRole.HUB_MANAGER.name())
                || roleHeader.equals((UserRole.COMPANY_DELIVERY_MANAGER.name()))
                || roleHeader.equals((UserRole.HUB_DELIVERY_MANAGER.name()))) {
            ResponseDto responseDto = deliveryService.updateDelivery(deliveryId, updateRequestDto, roleHeader, hubId, userId);
            return BaseResponse.success(responseDto);
        } else {
            return BaseResponse.fail(AppException.of(ApiErrorCode.FORBIDDEN));
        }
    }

    // 배송 단건 조회
    @GetMapping("/deliveries/{deliveryId}")
    public BaseResponse getDelivery(@PathVariable UUID deliveryId) {
        ResponseDto responseDto = deliveryService.getDelivery(deliveryId);
        return BaseResponse.success(responseDto);
    }

    // 배송 목록 조회
    @GetMapping("/deliveries")
    public BaseResponse getDeliveryPage(@RequestParam(required = false) String direction,
                                        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
                                              Pageable pageable) {
        if (direction != null) {
            Sort.Direction sortDirection = Sort.Direction.fromString(direction);
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(sortDirection, "createdAt")
            );
        }

        Page<ResponseDto> deliveryPage = deliveryService.getDeliveryPage(pageable);
        return BaseResponse.success(deliveryPage);
    }

    //배송 현황 검색
    @GetMapping("search-deliveries/{orderId}")
    public BaseResponse searchDeliveryStatusList(@PathVariable UUID orderId){
        List<SearchDeliveryResponseDto> responseDtoList = deliveryService.searchDeliveryStatusList(orderId);
        return BaseResponse.success(responseDtoList);

    }

    // Todo. 권한 확인 ok
    // 배송 삭제
    @DeleteMapping("/deliveries/{deliveryId}")
    public BaseResponse deleteDelivery(@PathVariable UUID deliveryId, HttpServletRequest request) {

        // 헤더 꺼내기
        String userIdHeader = request.getHeader("X-User-Id");
        String roleHeader = request.getHeader("X-User-Role");
        String hubIdHeader = request.getHeader("X-Hub-Id");

        // 캐스팅
        Long userId = Long.parseLong(userIdHeader);
        UUID hubId = hubIdHeader != null ? UUID.fromString(hubIdHeader) : null;

        // 권한 검증
        if(roleHeader.equals(UserRole.MASTER.name()) || roleHeader.equals(UserRole.HUB_MANAGER.name())) {
            DeleteResponseDto responseDto = deliveryService.deleteDelivery(deliveryId, userId, roleHeader, hubId);
            return BaseResponse.success(responseDto);
        } else {
            return BaseResponse.fail(AppException.of(ApiErrorCode.FORBIDDEN));
        }
    }
}
