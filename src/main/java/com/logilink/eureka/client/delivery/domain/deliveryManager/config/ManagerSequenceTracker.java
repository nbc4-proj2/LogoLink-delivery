package com.logilink.eureka.client.delivery.domain.deliveryManager.config;

import com.logilink.eureka.client.delivery.common.constants.DeliveryUserType;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ✅ 배송 매니저 라운드 로빈 순번 관리용 유틸 - in-memory 시퀀스 저장소
 *  - 서비스 재시작 시 초기화됨
 *  - 각 deliveryType / hubId 별로 마지막 사용된 deliverySeq 저장
 *  - 다음 호출 시 해당 값 기준으로 +1 한 매니저를 선택
 */
public class ManagerSequenceTracker {

    // Key: deliveryType + hubId 조합, Value: 마지막 사용된 seq
    private static final Map<String, Long> lastUsedSeqMap = new ConcurrentHashMap<>();

    private static String buildKey(DeliveryUserType type, UUID hubId) {
        return type.name() + ":" + (hubId != null ? hubId.toString() : "GLOBAL");
    }

    public static long getLastUsedSeq(DeliveryUserType type, UUID hubId) {
        return lastUsedSeqMap.getOrDefault(buildKey(type, hubId), -1L);
    }

    public static void updateLastUsedSeq(DeliveryUserType type, UUID hubId, Long seq) {
        lastUsedSeqMap.put(buildKey(type, hubId), seq);
    }
}