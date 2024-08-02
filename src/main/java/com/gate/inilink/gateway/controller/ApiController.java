package com.gate.inilink.gateway.controller;

import com.gate.inilink.gateway.dto.RouteHandlerDTO;
import com.gate.inilink.gateway.handler.DynamicRouteHandler;
import com.gate.inilink.gateway.handler.HandlerChain;
import com.gate.inilink.gateway.handler.request.RequestHandler;
import com.gate.inilink.gateway.router.GatewayRouter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class ApiController {

	private static final Logger log = LoggerFactory.getLogger(ApiController.class);

    @Autowired
    private DynamicRouteHandler dynamicRouteHandler;


    // 와일드카드 경로 처리
    @GetMapping("/**")
    public Mono<ResponseEntity<Object>> getRoute(ServerWebExchange exchange) {
        log.info("API call received for path: {}", exchange.getRequest().getURI().getPath());
        return dynamicRouteHandler.handle(exchange)
            .then(Mono.fromSupplier(() -> ResponseEntity.ok().build()))
            .onErrorResume(e -> {
                log.error("Error handling request for path: {}", exchange.getRequest().getURI().getPath(), e);
                return Mono.fromSupplier(() -> ResponseEntity.status(500).build());
            });
    }
}
