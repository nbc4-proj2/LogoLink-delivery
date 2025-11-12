package com.logilink.eureka.client.delivery.domain.delivery.service;

import com.logilink.eureka.client.delivery.common.constants.DeliveryStatus;
import com.logilink.eureka.client.delivery.common.exception.AppException;
import com.logilink.eureka.client.delivery.common.exception.DeliveryErrorCode;
import com.logilink.eureka.client.delivery.common.exception.UserRole;
import com.logilink.eureka.client.delivery.domain.delivery.feignClient.HubRouteClient;
import com.logilink.eureka.client.delivery.domain.delivery.model.dto.responseDto.*;
import com.logilink.eureka.client.delivery.domain.delivery.model.entity.Delivery;
import com.logilink.eureka.client.delivery.domain.delivery.model.dto.requestDto.DeliveryCreateRequestDto;
import com.logilink.eureka.client.delivery.domain.delivery.model.dto.requestDto.UpdateRequestDto;
import com.logilink.eureka.client.delivery.domain.delivery.repository.DeliveryRepository;
import com.logilink.eureka.client.delivery.domain.deliveryManager.model.entity.DeliveryManager;
import com.logilink.eureka.client.delivery.domain.deliveryManager.service.DeliveryManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final HubRouteClient hubRouteClient;
    private final DeliveryManagerService deliveryManagerService;

    // 배송 생성
    @Transactional
    public List<ResponseDto> createDeliverieList(DeliveryCreateRequestDto requestDto) {
        // 1) 허브 경로 먼저 조회
        HubRouteResponseDto routeResponse =
                hubRouteClient.getHubRoutes(requestDto.getOriginHubId(), requestDto.getDestinationId());

        List<HubRouteNode> routeList = routeResponse.getRouteList();
        if (routeList == null || routeList.isEmpty()) {
            // 허브 경로가 없으면 생성 불가
            throw AppException.of(DeliveryErrorCode.HUB_ROUTE_IS_NOT_EXISTING);
        }

        List<Delivery> result = new ArrayList<>();

        // 2) 허브 → 허브 배송들 만들기
        // routes 가 [서울, 대전, 부산] 이면
        // (서울→대전), (대전→부산) 이렇게 2건 만들어야 함
        for (HubRouteNode node : routeList) {

            DeliveryManager hubManager = deliveryManagerService.getNextHubManager();

            Delivery hubDelivery = Delivery.of(
                    true,                                  // isHubDelivery
                    requestDto.getOrderId(),
                    node.getOriginHubId(),
                    node.getDestinationHubId(),
                    node.getDestinationHubAddress(),
                    node.getRouteId(),
                    hubManager.getId(),
                    DeliveryStatus.HUB_PENDING
            );

            deliveryRepository.save(hubDelivery);
            result.add(hubDelivery);
        }

        // 3) 마지막 허브 → 업체 배송 만들기
        HubRouteNode lastNode = routeList.get(routeList.size() - 1);

        // 업체 배송은 “그 허브에 속한 업체 매니저” 중에서 순번대로
        DeliveryManager storeManager =
                deliveryManagerService.getNextStoreManager(lastNode.getDestinationHubId());

        Delivery companyDelivery = Delivery.of(
                false,
                requestDto.getOrderId(),
                lastNode.getDestinationHubId(),          // 업체 배송의 출발 허브
                requestDto.getDestinationId(),
                requestDto.getDestinationAddress(),
                lastNode.getRouteId(),
                storeManager.getId(),
                DeliveryStatus.HUB_PENDING
        );

        deliveryRepository.save(companyDelivery);
        result.add(companyDelivery);

        List<ResponseDto> responseDtoList = new ArrayList<>();
        // responseDto로 변환
        for(Delivery d : result) {
            responseDtoList.add(new ResponseDto(d));
        }

        return responseDtoList;
    }


    // 배송 수정
    @Transactional
    public ResponseDto updateDelivery(UUID deliveryId, UpdateRequestDto updateRequestDto, String roleHeader, UUID hubId, Long userId) {
        // Todo. 권한 : 마스터, 허브관리자, 배송담당자 ok

        Delivery delivery = validDeliveryId(deliveryId);
        if(roleHeader.equals(UserRole.MASTER.name())) {    // 마스터 권한
            updateDelivery(updateRequestDto, delivery);
        } else if (roleHeader.equals(UserRole.HUB_MANAGER.name())) {    // 허브 관리자 권한
            if(hubId != null && hubId.equals(delivery.getOriginHubId())) {
                updateDelivery(updateRequestDto, delivery);
            } else {
                throw AppException.of(DeliveryErrorCode.FORBIDDEN);
            }
        } else if(roleHeader.equals((UserRole.COMPANY_DELIVERY_MANAGER.name()))
                    || roleHeader.equals((UserRole.HUB_DELIVERY_MANAGER.name()))) {    // 배송 매니저 권한
            if(userId.equals(delivery.getDeliveryManagerId())) {
                updateDelivery(updateRequestDto, delivery);
            }
        }

        return new ResponseDto(delivery);
    }

    // 배송 단건 조회
    @Transactional(readOnly = true)
    public ResponseDto getDelivery(UUID deliveryId) {
        Delivery delivery = validDeliveryId(deliveryId);

        if(delivery.getDeletedAt() != null) {
            throw AppException.of(DeliveryErrorCode.DELIVERY_IS_NOT_EXISTING);
        }

        return new ResponseDto(delivery);
    }

    // 배송 목록 조회
    @Transactional(readOnly = true)
    public Page<ResponseDto> getDeliveryPage(Pageable pageable) {
        Page<Delivery> deliveryPage = vaildDeliveryPage(pageable);
        return deliveryPage.map(ResponseDto::new);
    }

    // 배송 현황 검색
    @Transactional(readOnly = true)
    public List<SearchDeliveryResponseDto> searchDeliveryStatusList(UUID orderId) {

        List<Delivery> deliveryList = deliveryRepository.findAllByOrderIdAndDeletedAtIsNullOrderByCreatedAtAsc(orderId);
        if (deliveryList.isEmpty()) {
            throw AppException.of(DeliveryErrorCode.DELIVERY_BY_ORDERID_IS_NOT_EXISTING);
        }

        List<SearchDeliveryResponseDto> responseDtoList = new ArrayList<>();

        for(Delivery delivery : deliveryList) {
            UUID deliveryId = delivery.getId();
            DeliveryStatus status = delivery.getStatus();

            responseDtoList.add(new SearchDeliveryResponseDto(deliveryId, status, orderId));
        }
        return responseDtoList;
    }

    // 배송 삭제
    @Transactional
    public DeleteResponseDto deleteDelivery(UUID deliveryId, Long userId, String roleHeader, UUID hubId) {
        // Todo. 권한 : 마스터, 허브관리자 ok
        Delivery delivery = validDeliveryId(deliveryId);

        if(roleHeader.equals(UserRole.MASTER.name())) {    // 마스터 권한인 경우
            delivery.softDelete(LocalDateTime.now(), userId);
        } else if(roleHeader.equals(UserRole.HUB_MANAGER.name())) {    // 허브 관리자 권한인 경우
            if(hubId.equals(delivery.getOriginHubId())) {    // 담당 허브의 배송만 삭제 가능
                delivery.softDelete(LocalDateTime.now(), userId);
            }
        }

        return new DeleteResponseDto(delivery);
    }

    // 배송 상태 변경 전 검증 메서드 :  배송완료 상태이면 변경 불가
    private void validateUpdatable(Delivery delivery) {
        if (delivery.getStatus() == DeliveryStatus.DONE) {
            throw AppException.of(DeliveryErrorCode.DELIVERY_ALREADY_COMPLETED);
        }
    }

    // 배송 아이디 검증 메서드
    private Delivery validDeliveryId(UUID deliveryId) {
        if(deliveryId == null) {
            throw AppException.of(DeliveryErrorCode.DELIVERY_IS_NOT_EXISTING);
        }

        return deliveryRepository.findById(deliveryId)
                .orElseThrow(()->AppException.of(DeliveryErrorCode.DELIVERY_IS_NOT_EXISTING));
    }

    // pageable 검증 메서드
    private Page<Delivery> vaildDeliveryPage(Pageable pageable) {
        if(pageable == null) {
            throw AppException.of(DeliveryErrorCode.DELIVERY_PAGEABLE_IS_NOT_EXISTING);
        }
        return deliveryRepository.findAllByDeletedAtIsNull(pageable);
    }

    // 배송 업데이트 메서드
    private void updateDelivery(UpdateRequestDto updateRequestDto, Delivery delivery) {
        if(updateRequestDto.getStatus() != null) {
            validateUpdatable(delivery);
            delivery.setStatus(updateRequestDto.getStatus());    // 배송 상태 변경
        }
        if(updateRequestDto.getDeliveryManagerId() != null) {
            validateUpdatable(delivery);
            delivery.setDeliveryManagerId(updateRequestDto.getDeliveryManagerId());    // 배송 담당자 변경
        }
    }
}
