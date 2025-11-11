package com.logilink.eureka.client.delivery.domain.deliveryManager.controller;

import com.logilink.eureka.client.delivery.common.BaseResponse;
import com.logilink.eureka.client.delivery.common.constants.DeliveryUserType;
import com.logilink.eureka.client.delivery.common.exception.ApiErrorCode;
import com.logilink.eureka.client.delivery.common.exception.AppException;
import com.logilink.eureka.client.delivery.common.exception.DeliveryErrorCode;
import com.logilink.eureka.client.delivery.common.exception.UserRole;
import com.logilink.eureka.client.delivery.domain.deliveryManager.model.dto.requestDto.CreateRequestDto;
import com.logilink.eureka.client.delivery.domain.deliveryManager.model.dto.requestDto.ManagerUpdateRequestDto;
import com.logilink.eureka.client.delivery.domain.deliveryManager.model.entity.DeliveryManager;
import com.logilink.eureka.client.delivery.domain.deliveryManager.service.DeliveryManagerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DeliveryManagerController {
    private final DeliveryManagerService deliveryManagerService;

    // 배송 매니저 등록 : 유저 도메인에서 배송 매니저로 회원가입이 되면 feign client로 요청
    @PostMapping
    public BaseResponse createDeliveryManager(@Valid @RequestBody CreateRequestDto requestDto, HttpServletRequest request) {
        DeliveryManager manager;

        // 헤더 꺼내기
        String roleHeader = request.getHeader("X-User-Role");

        if(roleHeader.equals(UserRole.MASTER.name())) {
            // 허브 배송
            if (requestDto.getDeliveryType() == DeliveryUserType.HUB) {
                // 허브 배송 매니저는 hubId 없으니까 service 쪽에서 null로 넣는 버전 호출
                manager = deliveryManagerService.createHubManager(requestDto);
            }
            // 업체 배송
            else  {
                // 업체 배송 매니저는 허브에 소속이니까 hubId 필요
                if (requestDto.getHubId() == null) {
                    throw AppException.of(DeliveryErrorCode.HUB_IS_NOT_EXISTING);
                }
                manager = deliveryManagerService.createStoreManager(requestDto);
            }

            return BaseResponse.success(manager);
        } else {
            return BaseResponse.fail(AppException.of(ApiErrorCode.FORBIDDEN));
        }

    }

    // 배송 매니저 수정
    @PatchMapping("/{deliveryManagerId}")
    public BaseResponse<?> updateDeliveryManager(@PathVariable Long deliveryManagerId,
                                                 @Valid @RequestBody ManagerUpdateRequestDto requestDto,
                                                 HttpServletRequest request) {
        // 헤더 꺼내기
        String userIdHeader = request.getHeader("X-User-Id");
        String roleHeader = request.getHeader("X-User-Role");
        String hubIdHeader = request.getHeader("X-Hub-Id");

        // 캐스팅
        UUID hubId = hubIdHeader != null ? UUID.fromString(hubIdHeader) : null;

        if(roleHeader.equals(UserRole.MASTER.name()) || roleHeader.equals(UserRole.HUB_MANAGER.name())) {
            return BaseResponse.success(deliveryManagerService.updateDeliveryManager(deliveryManagerId, requestDto,roleHeader, hubId));
        } else {
            return BaseResponse.fail(AppException.of(ApiErrorCode.FORBIDDEN));
        }
    }

    // 배송 매니저 삭제
    @DeleteMapping("/{deliveryManagerId}")
    public BaseResponse<?> deleteDeliveryManager(@PathVariable Long deliveryManagerId,
                                                 HttpServletRequest request) {
        // 헤더 꺼내기
        String userIdHeader = request.getHeader("X-User-Id");
        String roleHeader = request.getHeader("X-User-Role");
        String hubIdHeader = request.getHeader("X-Hub-Id");

        // 캐스팅
        Long userId = Long.parseLong(userIdHeader);
        UUID hubId = hubIdHeader != null ? UUID.fromString(hubIdHeader) : null;



        if(roleHeader.equals(UserRole.MASTER.name()) || roleHeader.equals(UserRole.HUB_MANAGER.name())) {
            return BaseResponse.success(deliveryManagerService.deleteDeliveryManager(deliveryManagerId, userId, roleHeader, hubId));
        } else {
            return BaseResponse.fail(AppException.of(ApiErrorCode.FORBIDDEN));
        }
    }

    // 배송 매니저 단건 조회
    @GetMapping("/{deliveryManagerId}")
    public BaseResponse getDeliveryManager(@PathVariable Long deliveryManagerId,
                                           HttpServletRequest request) {

        // 헤더 꺼내기
        String userIdHeader = request.getHeader("X-User-Id");
        String roleHeader = request.getHeader("X-User-Role");
        String hubIdHeader = request.getHeader("X-Hub-Id");

        // 캐스팅
        Long userId = Long.parseLong(userIdHeader);
        UUID hubId = hubIdHeader != null ? UUID.fromString(hubIdHeader) : null;

        if(roleHeader.equals(UserRole.COMPANY_MANAGER.name())) {
            return BaseResponse.fail(AppException.of(ApiErrorCode.FORBIDDEN));
        } else {
            return BaseResponse.success(deliveryManagerService.getDeliveryManager(deliveryManagerId,roleHeader, hubId, userId));
        }
    }

    // 배송 매니저 목록 조회 (pageable)
    @GetMapping
    public BaseResponse getDeliveryManagerPage(Pageable pageable, HttpServletRequest request) {
        // 헤더 꺼내기
        String userIdHeader = request.getHeader("X-User-Id");
        String roleHeader = request.getHeader("X-User-Role");
        String hubIdHeader = request.getHeader("X-Hub-Id");

        // 캐스팅
        Long userId = Long.parseLong(userIdHeader);
        UUID hubId = hubIdHeader != null ? UUID.fromString(hubIdHeader) : null;


        return BaseResponse.success(deliveryManagerService.getDeliveryManagerPage(pageable, roleHeader, hubId, userId));
    }

    // 허브 아이디로 검색
    @GetMapping("/search/by-hub")
    public BaseResponse<?> searchByHub(@RequestParam UUID searchHubId, Pageable pageable, HttpServletRequest request) {
        // 헤더 꺼내기
        String userIdHeader = request.getHeader("X-User-Id");
        String roleHeader = request.getHeader("X-User-Role");
        String hubIdHeader = request.getHeader("X-Hub-Id");

        // 캐스팅
        Long userId = Long.parseLong(userIdHeader);
        UUID hubId = hubIdHeader != null ? UUID.fromString(hubIdHeader) : null;


        return BaseResponse.success(deliveryManagerService.searchByHubId(searchHubId, pageable, roleHeader, hubId, userId));
    }

    // 타입(HUB/COMPANY)으로 검색
    @GetMapping("/search/by-type")
    public BaseResponse<?> searchByType(@RequestParam DeliveryUserType deliveryType, Pageable pageable,
                                        HttpServletRequest request) {
        // 헤더 꺼내기
        String userIdHeader = request.getHeader("X-User-Id");
        String roleHeader = request.getHeader("X-User-Role");
        String hubIdHeader = request.getHeader("X-Hub-Id");

        // 캐스팅
        Long userId = Long.parseLong(userIdHeader);
        UUID hubId = hubIdHeader != null ? UUID.fromString(hubIdHeader) : null;

        return BaseResponse.success(deliveryManagerService.searchByDeliveryType(deliveryType, pageable , roleHeader, hubId, userId));
    }
}
