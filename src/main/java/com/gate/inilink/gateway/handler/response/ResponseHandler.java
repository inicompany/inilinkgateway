package com.gate.inilink.gateway.handler.response;

import com.gate.inilink.gateway.handler.Handler;
import com.gate.inilink.gateway.handler.HandlerChain;
import com.gate.inilink.gateway.handler.request.RequestHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component("responseHandler")
public class ResponseHandler implements Handler {
	private static final Logger logger = LoggerFactory.getLogger(ResponseHandler.class);
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, HandlerChain chain) {
        // 응답 처리 로직 구현
    	logger.info("Handling responseHandler for path1: {}", exchange.getRequest().getURI().getPath());
    	System.out.println("######### ResponseHandler...");
        // 체인 끝
        return chain.handle(exchange);
    }
}
