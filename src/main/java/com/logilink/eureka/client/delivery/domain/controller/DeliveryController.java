package com.logilink.eureka.client.delivery.domain.controller;

import com.logilink.eureka.client.delivery.domain.service.DeliveryService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@NoArgsConstructor
@RequiredArgsConstructor
@Slf4j
public class DeliveryController {
    private final DeliveryService deliveryService;

}
