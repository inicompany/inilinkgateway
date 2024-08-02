package com.gate.inilink.gateway.handler;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.stereotype.Component;

import java.util.List;

//@Component
public class DefaultHandlerChain implements HandlerChain {

    private final List<Handler> handlers;
    private int index = 0;

    public DefaultHandlerChain(List<Handler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        if (index < handlers.size()) {
            Handler handler = handlers.get(index++);
            return handler.handle(exchange, this);
        }
        return Mono.empty();
    }
}
