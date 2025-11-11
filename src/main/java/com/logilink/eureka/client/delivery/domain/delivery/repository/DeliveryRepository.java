package com.logilink.eureka.client.delivery.domain.delivery.repository;

import com.logilink.eureka.client.delivery.domain.delivery.model.entity.Delivery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {
    Page<Delivery> findAllByDeletedAtIsNull(Pageable pageable);
    List<Delivery> findAllByOrderIdAndDeletedAtIsNullOrderByCreatedAtAsc(UUID orderId);
}
