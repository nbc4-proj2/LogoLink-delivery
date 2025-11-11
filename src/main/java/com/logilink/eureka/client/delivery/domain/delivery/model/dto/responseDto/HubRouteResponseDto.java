package com.logilink.eureka.client.delivery.domain.delivery.model.dto.responseDto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class HubRouteResponseDto {
    private List<HubRouteNode> routeList;
}
