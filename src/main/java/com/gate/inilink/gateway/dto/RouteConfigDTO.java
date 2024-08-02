package com.gate.inilink.gateway.dto;

import com.gate.inilink.gateway.handler.HandlerChain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteConfigDTO {
    private String path;
    private HandlerChain handlerChain;
}