package com.logilink.eureka.client.delivery.domain.service;

import com.logilink.eureka.client.delivery.domain.model.Delivery;
import com.logilink.eureka.client.delivery.domain.model.dto.CreateRequestDto;
import com.logilink.eureka.client.delivery.domain.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;

    public Delivery createDelivery(UUID orderId, CreateRequestDto createRequestDto) {
        // Todo. 권한 검증 : 마스터 관리자만 생성 가능, 여기서 검증 하는지 게이트웨이에서 하는지 둘 다 인지

        Delivery delivery = createRequestDto.toDelivery();
        delivery.setOrderId(orderId);
        deliveryRepository.save(delivery);
        return delivery;
    }
}
