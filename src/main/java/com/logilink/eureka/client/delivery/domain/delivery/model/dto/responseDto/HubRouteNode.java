package com.logilink.eureka.client.delivery.domain.delivery.model.dto.responseDto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
public class HubRouteNode {
    private UUID routeId;
    private String originHubName;
    private UUID originHubId;
    private String originHubAddress;
    private String destinationHubName;
    private UUID destinationHubId;
    private String destinationHubAddress;
}