package com.logilink.eureka.client.delivery.domain.repository;

import com.logilink.eureka.client.delivery.domain.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {
}
