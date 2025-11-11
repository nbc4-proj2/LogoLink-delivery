package com.logilink.eureka.client.delivery.domain.service;

import com.logilink.eureka.client.delivery.common.constants.DeliveryStatus;
import com.logilink.eureka.client.delivery.domain.model.Delivery;
import com.logilink.eureka.client.delivery.domain.model.dto.CreateRequestDto;
import com.logilink.eureka.client.delivery.domain.model.dto.SearchDeliveryResponseDto;
import com.logilink.eureka.client.delivery.domain.model.dto.UpdateRequestDto;
import com.logilink.eureka.client.delivery.domain.repository.DeliveryRepository;
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

    // 배송 생성
    @Transactional
    public Delivery createDelivery(UUID orderId, CreateRequestDto createRequestDto) {
        // Todo. 권한 검증 : 마스터 관리자만 생성 가능, 여기서 검증 하는지 게이트웨이에서 하는지 둘 다 인지
        // Todo. orderId 검증

        Delivery delivery = createRequestDto.toDelivery();
        delivery.setOrderId(orderId);
        deliveryRepository.save(delivery);
        return delivery;
    }

    // 배송 수정
    @Transactional
    public Delivery updateDelivery(UUID deliveryId, UpdateRequestDto updateRequestDto) {
        // Todo. 권한 : 마스터, 허브관리자, 배송담당자

        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow();

        if(updateRequestDto.getStatus() != null) {
            delivery.setStatus(updateRequestDto.getStatus());
        }
        if(updateRequestDto.getDeliveryManagerId() != null) {
            delivery.setDeliveryManagerId(updateRequestDto.getDeliveryManagerId());
        }

        return  delivery;
    }

    // 배송 삭제
    @Transactional
    public Delivery deleteDelivery(UUID deliveryId, Long userId) {
        // Todo. 권한 : 마스터, 허브관리자 <- userId

        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow();

        delivery.softDelete(LocalDateTime.now(), userId);

        return delivery;
    }

    // 배송 단건 조회
    @Transactional(readOnly = true)
    public Delivery getDelivery(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow();

        // Todo. 예외처리 : 배송 삭제 된 경우

        return delivery;
    }

    // 배송 목록 조회
    @Transactional(readOnly = true)
    public Page<Delivery> getDeliveryList(Pageable pageable) {
        return deliveryRepository.findAllByDeletedAtIsNull(pageable);
    }

    // 배송 현황 검색
    @Transactional(readOnly = true)
    public List<SearchDeliveryResponseDto> searchDeliveryStatusList(UUID orderId) {
        // Todo. orderId 검증

        List<Delivery> deliveryList = deliveryRepository.findAllByOrderIdAndDeletedAtIsNullOrderByCreatedAtAsc(orderId);
        List<SearchDeliveryResponseDto> responseDtoList = new ArrayList<>();

        for(Delivery delivery : deliveryList) {
            UUID deliveryId = delivery.getId();
            DeliveryStatus status = delivery.getStatus();

            responseDtoList.add(new SearchDeliveryResponseDto(deliveryId, status, orderId));
        }
        return responseDtoList;
    }
}
