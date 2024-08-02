package com.gate.inilink.gateway.handler.request;

import com.gate.inilink.gateway.handler.Handler;
import com.gate.inilink.gateway.handler.HandlerChain;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component("requestHandler")
public class RequestHandler implements Handler {
	private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    
	@Override
    public Mono<Void> handle(ServerWebExchange exchange, HandlerChain chain) {
        // 요청 처리 로직 구현
        logger.info("Handling request for path1: {}", exchange.getRequest().getURI().getPath());
        System.out.println("######### RequestHandler...");
        // 다음 핸들러로 체인 넘김
        return chain.handle(exchange);
    }
}