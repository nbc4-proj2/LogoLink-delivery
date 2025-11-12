package com.logilink.eureka.client.delivery.domain.deliveryManager.service;

import com.logilink.eureka.client.delivery.common.constants.DeliveryUserType;
import com.logilink.eureka.client.delivery.common.exception.AppException;
import com.logilink.eureka.client.delivery.common.exception.DeliveryErrorCode;
import com.logilink.eureka.client.delivery.common.exception.UserRole;
import com.logilink.eureka.client.delivery.domain.delivery.model.entity.Delivery;
import com.logilink.eureka.client.delivery.domain.delivery.repository.DeliveryRepository;
import com.logilink.eureka.client.delivery.domain.deliveryManager.config.ManagerSequenceTracker;
import com.logilink.eureka.client.delivery.domain.deliveryManager.model.dto.requestDto.CreateRequestDto;
import com.logilink.eureka.client.delivery.domain.deliveryManager.model.dto.requestDto.ManagerUpdateRequestDto;
import com.logilink.eureka.client.delivery.domain.deliveryManager.model.dto.responseDto.DeleteResponseDto;
import com.logilink.eureka.client.delivery.domain.deliveryManager.model.dto.responseDto.ResponseDto;
import com.logilink.eureka.client.delivery.domain.deliveryManager.model.entity.DeliveryManager;
import com.logilink.eureka.client.delivery.domain.deliveryManager.repository.DeliveryManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryManagerService {

    private final DeliveryManagerRepository deliveryManagerRepository;
    private final DeliveryRepository deliveryRepository;

    // 허브 배송 매니저 생성
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
                .build();

        return deliveryManagerRepository.save(manager);
    }

    // 업체 배송 매니저 생성
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
                .build();

        return deliveryManagerRepository.save(manager);
    }

    // 배송 매니저 수정
    @Transactional
    public ResponseDto updateDeliveryManager(Long deliveryManagerId, ManagerUpdateRequestDto requestDto,
                                             String roleHeader, UUID hubId) {

        DeliveryManager deliveryManager = validAndFindDeliveryManager(deliveryManagerId);

        if(roleHeader.equals(UserRole.HUB_MANAGER.name())) { // 허브 관리자 권한
            if (hubId != null && hubId.equals(deliveryManager.getHubId())){
                deliveryManager.update(requestDto);
            }
        } else { // 마스터 권한
            deliveryManager.update(requestDto);
        }

        return new ResponseDto(deliveryManager);
    }

    // 베송 매니저 삭제
    @Transactional
    public DeleteResponseDto deleteDeliveryManager(Long deliveryManagerId, Long userId,
                                                   String roleHeader, UUID hubId) {
        DeliveryManager deliveryManager = validAndFindDeliveryManager(deliveryManagerId);

        if(roleHeader.equals(UserRole.HUB_MANAGER.name())) { // 허브 관리자 권한
            if(hubId.equals(deliveryManager.getHubId())) {
                deliveryManager.softDelete(LocalDateTime.now(), userId);
            }
        } else { // 마스터 권한
            deliveryManager.softDelete(LocalDateTime.now(), userId);
        }

        return new DeleteResponseDto(deliveryManager);
    }

    // 배송 매니저 단건 조회
    public ResponseDto getDeliveryManager(Long deliveryManagerId, String roleHeader, UUID hubId, Long userId) {
        DeliveryManager deliveryManager = validAndFindDeliveryManager(deliveryManagerId);

        if(deliveryManager.getDeletedAt() != null) {
            throw AppException.of(DeliveryErrorCode.DELIVERY_MANAGER_IS_NOT_EXISTING);
        }

        if (!roleHeader.equals(UserRole.MASTER.name())) {
            roleCheckHubManager(roleHeader, hubId, deliveryManager, deliveryManagerId, userId);
            roleCheckDeliveryManager(roleHeader, hubId, deliveryManager, deliveryManagerId, userId);
        }

        return new ResponseDto(deliveryManager);
    }

    // 배송 매니저 목록 조회
    @Transactional(readOnly = true)
    public Page<ResponseDto> getDeliveryManagerPage(Pageable pageable, String roleHeader, UUID hubId, Long userId) {

        // MASTER: 전체
        if (UserRole.MASTER.name().equals(roleHeader)) {
            Page<DeliveryManager> page = deliveryManagerRepository.findAllByDeletedAtIsNull(pageable);
            if (page.isEmpty()) throw AppException.of(DeliveryErrorCode.DATA_IS_NOT_EXISTING);
            return page.map(ResponseDto::new);
        }

        // HUB_MANAGER: 담당 허브만
        if (UserRole.HUB_MANAGER.name().equals(roleHeader)) {
            if (hubId == null) {
                // 허브 식별이 없으면 목록을 줄 수 없음
                throw AppException.of(DeliveryErrorCode.FAILED_GET_OR_SEARCH_DELIVERY_MANAGER);
                // 또는 return Page.empty(pageable);
            }

            Page<DeliveryManager> page = deliveryManagerRepository
                    .findAllByHubIdAndDeletedAtIsNull(hubId, pageable);

            // 데이터 없으면 빈 페이지 반환
            if (page.isEmpty()) return Page.empty(pageable);
            return page.map(ResponseDto::new);
        }

        // 배송 담당자(허브/업체): 본인 1명만
        if (UserRole.HUB_DELIVERY_MANAGER.name().equals(roleHeader)
                || UserRole.COMPANY_DELIVERY_MANAGER.name().equals(roleHeader)) {

            // 페이지 0 이외는 빈 페이지 반환
            if (pageable.getPageNumber() > 0) {
                return Page.empty(pageable);
            }

            DeliveryManager me = deliveryManagerRepository.findByIdAndDeletedAtIsNull(userId)
                    .orElseThrow(() -> AppException.of(DeliveryErrorCode.DELIVERY_MANAGER_IS_NOT_EXISTING));

            return new org.springframework.data.domain.PageImpl<>(
                    java.util.List.of(new ResponseDto(me)), pageable, 1
            );
        }

        // 그 외 권한: 금지
        throw AppException.of(DeliveryErrorCode.FAILED_GET_OR_SEARCH_DELIVERY_MANAGER);
    }

    // 허브 아이디로 검색
    @Transactional(readOnly = true)
    public Page<ResponseDto> searchByHubId(UUID searchHubId,
                                           Pageable pageable,
                                           String roleHeader,
                                           UUID hubId,
                                           Long userId) {

        // 마스터 → 어떤 허브든 조회 가능
        if (UserRole.MASTER.name().equals(roleHeader)) {
            Page<DeliveryManager> page =
                    deliveryManagerRepository.findAllByHubIdAndDeletedAtIsNull(searchHubId, pageable);
            return page.map(ResponseDto::new);
        }

        // 허브 관리자 → 자기 허브(hubId)와 검색 대상(searchHubId)이 같을 때만 조회 허용
        if (UserRole.HUB_MANAGER.name().equals(roleHeader)) {
            if (hubId == null) {
                throw AppException.of(DeliveryErrorCode.FAILED_GET_OR_SEARCH_DELIVERY_MANAGER);
            }

            // 다른 허브로 조회 시 권한 에러
            if (!hubId.equals(searchHubId)) {
                throw AppException.of(DeliveryErrorCode.FAILED_GET_OR_SEARCH_DELIVERY_MANAGER);
            }

            Page<DeliveryManager> page =
                    deliveryManagerRepository.findAllByHubIdAndDeletedAtIsNull(searchHubId, pageable);
            return page.map(ResponseDto::new);
        }

        // 배송 담당자(허브/업체) → 자기 자신만 반환
        if (UserRole.HUB_DELIVERY_MANAGER.name().equals(roleHeader)
                || UserRole.COMPANY_DELIVERY_MANAGER.name().equals(roleHeader)) {

            DeliveryManager me = deliveryManagerRepository.findByIdAndDeletedAtIsNull(userId)
                    .orElseThrow(() -> AppException.of(DeliveryErrorCode.DELIVERY_MANAGER_IS_NOT_EXISTING));

            // 검색 대상 허브가 본인 허브와 다르면 권한 에러
            if (!searchHubId.equals(me.getHubId())) {
                throw AppException.of(DeliveryErrorCode.FAILED_GET_OR_SEARCH_DELIVERY_MANAGER);
            }

            if (pageable.getPageNumber() > 0) {
                return Page.empty(pageable);
            }

            return new org.springframework.data.domain.PageImpl<>(
                    java.util.List.of(new ResponseDto(me)), pageable, 1
            );
        }

        // 그 외 역할 → 접근 금지
        throw AppException.of(DeliveryErrorCode.FAILED_GET_OR_SEARCH_DELIVERY_MANAGER);
    }


    // 타입(HUB / COMPANY)으로 검색
    @Transactional(readOnly = true)
    public Page<ResponseDto> searchByDeliveryType(DeliveryUserType deliveryUserType,
                                                  Pageable pageable,
                                                  String roleHeader,
                                                  UUID hubId,
                                                  Long userId) {

        // MASTER → 모든 허브, 모든 타입 검색 가능
        if (UserRole.MASTER.name().equals(roleHeader)) {
            Page<DeliveryManager> page =
                    deliveryManagerRepository.findAllByDeliveryTypeAndDeletedAtIsNull(deliveryUserType, pageable);
            return page.map(ResponseDto::new);
        }

        // 허브 관리자 → 자기 허브(hubId) + 해당 타입만 검색 가능
        if (UserRole.HUB_MANAGER.name().equals(roleHeader)) {
            if (hubId == null) {
                throw AppException.of(DeliveryErrorCode.FAILED_GET_OR_SEARCH_DELIVERY_MANAGER);
            }

            Page<DeliveryManager> page =
                    deliveryManagerRepository.findAllByDeliveryTypeAndHubIdAndDeletedAtIsNull(deliveryUserType, hubId, pageable);

            return page.map(ResponseDto::new);
        }

        // 배송 담당자 (허브 / 업체) → 자기 자신만 반환
        if (UserRole.HUB_DELIVERY_MANAGER.name().equals(roleHeader)
                || UserRole.COMPANY_DELIVERY_MANAGER.name().equals(roleHeader)) {

            DeliveryManager me = deliveryManagerRepository.findByIdAndDeletedAtIsNull(userId)
                    .orElseThrow(() -> AppException.of(DeliveryErrorCode.DELIVERY_MANAGER_IS_NOT_EXISTING));

            // 타입 불일치 시 접근 금지
            if (!me.getDeliveryType().equals(deliveryUserType)) {
                throw AppException.of(DeliveryErrorCode.FAILED_GET_OR_SEARCH_DELIVERY_MANAGER);
            }

            // 허브 배송 매니저인 경우 허브ID 불일치 시 접근 금지
            if (hubId != null && me.getHubId() != null && !hubId.equals(me.getHubId())) {
                throw AppException.of(DeliveryErrorCode.FAILED_GET_OR_SEARCH_DELIVERY_MANAGER);
            }

            // 자기 자신만 페이지로 반환
            if (pageable.getPageNumber() > 0) {
                return Page.empty(pageable);
            }

            return new org.springframework.data.domain.PageImpl<>(
                    java.util.List.of(new ResponseDto(me)), pageable, 1
            );
        }

        // 그 외 권한 → 접근 금지
        throw AppException.of(DeliveryErrorCode.FAILED_GET_OR_SEARCH_DELIVERY_MANAGER);
    }



    // 허브 배송 매니저를 라운드 로빈 방식으로 가져오기
    @Transactional
    public DeliveryManager getNextHubManager() {
        return getNextManagerByTypeAndHub(DeliveryUserType.HUB, null);
    }

    // 업체 배송 매니저를 라운드 로빈 방식으로 가져오기
    @Transactional
    public DeliveryManager getNextStoreManager(UUID hubId) {
        return getNextManagerByTypeAndHub(DeliveryUserType.COMPANY, hubId);
    }

    /**
     *  라운드 로빈 + 삭제 매니저 skip + 순번 저장 갱신 로직
     *  - deliveryType + (hubId) 기준으로 매니저 전체를 deliverySeq 오름차순으로 정렬
     *  - deletedAt / deletedBy 가 null 인 사람만 후보
     *  - 이전에 사용된 seq 를 기억하고 그 다음 seq 를 사용
     *  - 마지막 매니저까지 배정되면 다시 0번부터 순환
     */
    private DeliveryManager getNextManagerByTypeAndHub(DeliveryUserType type, UUID hubId) {

        //  모든 매니저를 seq 오름차순으로 조회 (삭제된 매니저는 제외)
        var allManagers = deliveryManagerRepository.findAllOrderBySeq(type, hubId).stream()
                .filter(m -> m.getDeletedAt() == null && m.getDeletedBy() == null)
                .toList();

        if (allManagers.isEmpty()) {
            throw AppException.of(DeliveryErrorCode.DELIVERY_MANAGER_IS_NOT_EXISTING);
        }

        //  가장 마지막으로 사용된 순번을 추적 (정적 변수 or DB에 저장 가능)
        //    여기서는 간단하게 static 변수 사용
        long lastUsedSeq = ManagerSequenceTracker.getLastUsedSeq(type, hubId);

        //  다음 순번 = (lastUsedSeq + 1) % 전체 매니저 수
        int nextIndex = (int) ((lastUsedSeq + 1) % allManagers.size());
        DeliveryManager nextManager = allManagers.get(nextIndex);

        //  현재 사용한 순번을 업데이트
        ManagerSequenceTracker.updateLastUsedSeq(type, hubId, nextManager.getDeliverySeq());

        return nextManager;
    }

    private DeliveryManager validAndFindDeliveryManager(Long id) {
        DeliveryManager deliveryManager = deliveryManagerRepository.findById(id).orElseThrow(
                () -> AppException.of(DeliveryErrorCode.DELIVERY_MANAGER_IS_NOT_EXISTING));
        return deliveryManager;
    }

    // 권한 체크
    // 마스터는 all, 허브 관리자는 담당허브만, 배송 담당자는 자기 배송만
    private void roleCheckHubManager(String roleHeader, UUID hubId, DeliveryManager deliveryManager, Long deliveryManagerId, Long userId) {
        if(roleHeader.equals(UserRole.HUB_MANAGER.name())) {    // 허브 관리자 권한
            if(hubId == null && !hubId.equals(deliveryManager.getHubId())) {
                throw AppException.of(DeliveryErrorCode.FAILED_GET_OR_SEARCH_DELIVERY_MANAGER);    // 담당 허브여야 함
            }
        }
    }

    // 권한 체크
    // 마스터는 all, 배송 담당자는 자기 배송만
    private void roleCheckDeliveryManager(String roleHeader, UUID hubId, DeliveryManager deliveryManager, Long deliveryManagerId, Long userId) {
         if(roleHeader.equals(UserRole.HUB_DELIVERY_MANAGER.name()) || roleHeader.equals(UserRole.COMPANY_DELIVERY_MANAGER.name())) {
            if(!deliveryManagerId.equals(userId)) {
                throw AppException.of(DeliveryErrorCode.FAILED_GET_OR_SEARCH_DELIVERY_MANAGER);    // 자기 배송이어야 함
            }
        }
    }
}
