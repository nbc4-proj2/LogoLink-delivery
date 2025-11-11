package com.logilink.eureka.client.delivery.domain.deliveryManager.repository;

import com.logilink.eureka.client.delivery.common.constants.DeliveryUserType;
import com.logilink.eureka.client.delivery.domain.deliveryManager.model.entity.DeliveryManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryManagerRepository extends JpaRepository<DeliveryManager, Long> {

    // 허브배송 매니저 전체 중 최대 순번
    @Query("select coalesce(max(m.deliverySeq), -1) from DeliveryManager m where m.deliveryType = :deliveryType")
    Long findMaxSeqByType(@Param("deliveryType") DeliveryUserType deliveryType);

    // 업체배송 매니저: 허브별로 따로 최대값
    @Query("select coalesce(max(m.deliverySeq), -1) from DeliveryManager m where m.deliveryType = :deliveryType and m.hubId = :hubId")
    Long findMaxSeqByTypeAndHub(@Param("deliveryType") DeliveryUserType deliveryType,
                                @Param("hubId") UUID hubId);

    @Query("""
    select m
    from DeliveryManager m
    where m.deliveryType = :deliveryType
      and (:hubId is null or m.hubId = :hubId)
    order by m.deliverySeq asc
""")
    List<DeliveryManager> findAllOrderBySeq(@Param("deliveryType") DeliveryUserType deliveryType,
                                            @Param("hubId") UUID hubId);

    // 전체 배송 매니저 페이징
    Page<DeliveryManager> findAllByDeletedAtIsNull(Pageable pageable);

    // 허브 아이디로 검색
    Page<DeliveryManager> findAllByHubIdAndDeletedAtIsNull(UUID hubId, Pageable pageable);

    // 타입(HUB/COMPANY)으로 검색
    Page<DeliveryManager> findAllByDeliveryTypeAndDeletedAtIsNull(DeliveryUserType deliveryUserType, Pageable pageable);
}
