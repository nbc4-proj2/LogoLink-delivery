package com.logilink.eureka.client.delivery.domain.deliveryManager.service;

import com.logilink.eureka.client.delivery.common.constants.DeliveryUserType;
import com.logilink.eureka.client.delivery.domain.deliveryManager.model.dto.CreateRequestDto;
import com.logilink.eureka.client.delivery.domain.deliveryManager.model.entity.DeliveryManager;
import com.logilink.eureka.client.delivery.domain.deliveryManager.repository.DeliveryManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryManagerService {

    private final DeliveryManagerRepository deliveryManagerRepository;

    // 허브 배송 매니저
    @Transactional
    public DeliveryManager createHubManager(CreateRequestDto requestDto) {
        Long maxSeq = deliveryManagerRepository.findMaxSeqByType(DeliveryUserType.HUB);
        long nextSeq = maxSeq + 1;   // 가장 마지막 순번으로

        DeliveryManager manager = DeliveryManager.builder()
                .id(requestDto.getId())
                .hubId(null)
                .slackId(requestDto.getSlackId())
                .deliveryType(requestDto.getDeliveryType())
                .deliverySeq(nextSeq)
//                .isDeliveryAvailable(true)
                .build();

        return deliveryManagerRepository.save(manager);
    }

    // 업체 배송 매니저
    @Transactional
    public DeliveryManager createStoreManager(CreateRequestDto requestDto) {
        Long maxSeq = deliveryManagerRepository.findMaxSeqByTypeAndHub(DeliveryUserType.COMPANY, requestDto.getHubId());
        long nextSeq = maxSeq + 1;

        DeliveryManager manager = DeliveryManager.builder()
                .id(requestDto.getId())
                .hubId(requestDto.getHubId())
                .slackId(requestDto.getSlackId())
                .deliveryType(requestDto.getDeliveryType())
                .deliverySeq(nextSeq)
//                .isDeliveryAvailable(true)
                .build();

        return deliveryManagerRepository.save(manager);
    }

}
