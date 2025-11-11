package com.logilink.eureka.client.delivery.domain.deliveryManager.model.entity;

import com.logilink.eureka.client.delivery.common.BaseTimeEntity;
import com.logilink.eureka.client.delivery.common.constants.DeliveryUserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_delivery_managers")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryManager extends BaseTimeEntity {
    @Id
    @Column(name = "delivery_manager_id", nullable = false)
    private Long id;

    @Column(name= "hub_id")
    private UUID hubId;

    @Column(name = "slack_id", nullable = false)
    private String slackId;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_type", nullable = false)
    private DeliveryUserType deliveryType;

    @Column(name = "delivery_seq", nullable = false)
    private Long deliverySeq;

//    @Column(name = "is_delivery_available", nullable = false)
//    private Boolean isDeliveryAvailable;
}
