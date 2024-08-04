package com.gate.inilink.gateway.config;

import com.gate.inilink.gateway.handler.Handler;
import com.gate.inilink.gateway.handler.HandlerChain;
import com.gate.inilink.gateway.handler.DefaultHandlerChain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class HandlerConfiguration {
	
	/*/
	@Bean
    public AuthCommonHandler authHandler() {
        return new AuthCommonHandler();
    }

    @Bean
    public RequestHandler requestHandler() {
        return new RequestHandler();
    }
    
    @Bean
    public ResponseHandler responseHandler() {
        return new ResponseHandler();
    }

    @Bean
    public ErrorHandler errorHandler() {
        return new ErrorHandler();
    }*/
}
