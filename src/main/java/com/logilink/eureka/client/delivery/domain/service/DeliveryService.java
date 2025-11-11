package com.logilink.eureka.client.delivery.domain.service;

import com.logilink.eureka.client.delivery.common.constants.DeliveryStatus;
import com.logilink.eureka.client.delivery.common.exception.AppException;
import com.logilink.eureka.client.delivery.common.exception.DeliveryErrorCode;
import com.logilink.eureka.client.delivery.domain.model.dto.responseDto.DeleteResponseDto;
import com.logilink.eureka.client.delivery.domain.model.dto.responseDto.ResponseDto;
import com.logilink.eureka.client.delivery.domain.model.entity.Delivery;
import com.logilink.eureka.client.delivery.domain.model.dto.requestDto.CreateRequestDto;
import com.logilink.eureka.client.delivery.domain.model.dto.responseDto.SearchDeliveryResponseDto;
import com.logilink.eureka.client.delivery.domain.model.dto.requestDto.UpdateRequestDto;
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
    public ResponseDto createDelivery(UUID orderId, CreateRequestDto createRequestDto) {
        // Todo. 권한 검증 : 마스터 관리자만 생성 가능, 여기서 검증 하는지 게이트웨이에서 하는지 둘 다 인지
        // Todo. orderId 검증

        Delivery delivery = createRequestDto.toDelivery();
        delivery.setOrderId(orderId);
        deliveryRepository.save(delivery);
        ResponseDto responseDto = new ResponseDto(delivery);
        return responseDto;
    }

    // 배송 수정
    @Transactional
    public ResponseDto updateDelivery(UUID deliveryId, UpdateRequestDto updateRequestDto) {
        // Todo. 권한 : 마스터, 허브관리자, 배송담당자

        Delivery delivery = validDeliveryId(deliveryId);

        if(updateRequestDto.getStatus() != null) {
            validateUpdatable(delivery);
            delivery.setStatus(updateRequestDto.getStatus());
        }
        if(updateRequestDto.getDeliveryManagerId() != null) {
            delivery.setDeliveryManagerId(updateRequestDto.getDeliveryManagerId());
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
        // Todo. orderId 검증

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
    public DeleteResponseDto deleteDelivery(UUID deliveryId, Long userId) {
        // Todo. 권한 : 마스터, 허브관리자 <- userId

        Delivery delivery = validDeliveryId(deliveryId);

        delivery.softDelete(LocalDateTime.now(), userId);

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
}
