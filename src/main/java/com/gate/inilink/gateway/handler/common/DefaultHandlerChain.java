package com.gate.inilink.gateway.handler.common;

import com.gate.inilink.gateway.handler.Handler;
import com.gate.inilink.gateway.handler.HandlerChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

public class DefaultHandlerChain implements HandlerChain {
    private final List<Handler> handlers;
    private int currentHandlerIndex;

    public DefaultHandlerChain(List<Handler> handlers) {
        this.handlers = handlers;
        this.currentHandlerIndex = 0;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        if (currentHandlerIndex < handlers.size()) {
            Handler currentHandler = handlers.get(currentHandlerIndex);
            currentHandlerIndex++;
            return currentHandler.handle(exchange, this);
        } else {
            return Mono.empty();
        }
    }

}
