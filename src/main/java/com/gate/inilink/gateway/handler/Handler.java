package com.gate.inilink.gateway.handler;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface Handler {
    Mono<Void> handle(ServerWebExchange exchange, HandlerChain chain);
}