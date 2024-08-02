package com.gate.inilink.gateway.handler.error;

import com.gate.inilink.gateway.handler.Handler;
import com.gate.inilink.gateway.handler.HandlerChain;
import com.gate.inilink.gateway.handler.auth.AuthCommonHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component("errorHandler")
public class ErrorHandler implements Handler {
	private static final Logger logger = LoggerFactory.getLogger(AuthCommonHandler.class);
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, HandlerChain chain) {
        // 오류 처리 로직 구현
    	logger.info("Handling errorHandler for path1: {}", exchange.getRequest().getURI().getPath());
    	System.out.println("######### ErrorHandler...");
        // 다음 핸들러로 체인 넘김
        return chain.handle(exchange);
    }
}
