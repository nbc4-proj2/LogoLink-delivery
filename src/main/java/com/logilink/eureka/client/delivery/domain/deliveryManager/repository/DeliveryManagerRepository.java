package com.logilink.eureka.client.delivery.domain.deliveryManager.repository;

import com.logilink.eureka.client.delivery.common.constants.DeliveryUserType;
import com.logilink.eureka.client.delivery.domain.deliveryManager.model.entity.DeliveryManager;
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


}
