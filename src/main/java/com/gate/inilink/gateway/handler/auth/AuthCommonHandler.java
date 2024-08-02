package com.gate.inilink.gateway.handler.auth;

import com.gate.inilink.gateway.handler.Handler;
import com.gate.inilink.gateway.handler.HandlerChain;
import com.gate.inilink.gateway.handler.request.RequestHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component("authHandler")
public class AuthCommonHandler implements Handler {
	private static final Logger logger = LoggerFactory.getLogger(AuthCommonHandler.class);
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, HandlerChain chain) {
        // 인증 로직 구현
    	logger.info("Handling authHandler for path1: {}", exchange.getRequest().getURI().getPath());
    	System.out.println("######### AuthCommonHandler...");
        // 다음 핸들러로 체인 넘김
        return chain.handle(exchange);
    }
}
