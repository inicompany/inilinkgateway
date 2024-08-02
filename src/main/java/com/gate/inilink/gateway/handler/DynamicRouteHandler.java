package com.gate.inilink.gateway.handler;

import com.gate.inilink.gateway.config.RouteConfigLoader;
import com.gate.inilink.gateway.model.Routes;
import com.gate.inilink.gateway.dto.RouteConfigDTO;
import java.time.Duration;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MongoDB에서 동적 라우트를 로드하고 처리하는 핸들러입니다.
 * 모든 빈이 초기화된 후에 라우트 정보를 로드하도록 수정
 * <p>이전 초기화 방식:</p>
 * <pre>{@code
 * @PostConstruct
 * public void init() {
 *     try {
 *         log.info("Attempting to load routes from MongoDB...");
 *
 *         List<Routes> routes = routeConfigLoader.loadRoutes()
 *             .doOnSubscribe(subscription -> log.info("Subscribed to loadRoutes"))
 *             .doOnNext(route -> log.info("Loaded route: {}", route))
 *             .collectList()
 *             .doOnNext(this::processRoutes)
 *             .doOnError(error -> log.error("Error loading routes: ", error))
 *             .doOnTerminate(() -> log.info("Finished loading routes"))
 *             .block(Duration.ofSeconds(5));
 *
 *         log.info("Routes successfully loaded: {}", routes);
 *     } catch (Exception e) {
 *         log.error("Failed to load routes", e);
 *     }
 * }
 * }</pre>
 *
 * <p>수정된 초기화 방식:</p>
 * <pre>{@code
 * @Override
 * public void onApplicationEvent(ContextRefreshedEvent event) {
 *     try {
 *         log.info("Attempting to load routes from MongoDB...");
 *
 *         List<Routes> routes = routeConfigLoader.loadRoutes()
 *             .doOnSubscribe(subscription -> log.info("Subscribed to loadRoutes"))
 *             .doOnNext(route -> log.info("Loaded route: {}", route))
 *             .collectList()
 *             .doOnNext(this::processRoutes)
 *             .doOnError(error -> log.error("Error loading routes: ", error))
 *             .doOnTerminate(() -> log.info("Finished loading routes"))
 *             .block(Duration.ofSeconds(5));
 *
 *         log.info("Routes successfully loaded: {}", routes);
 *     } catch (Exception e) {
 *         log.error("Failed to load routes", e);
 *     }
 * }
 * }</pre>
 *
 * <p>비동기 초기화 방식:</p>
 * <pre>{@code
 * @PostConstruct
 * public void init() {
 *     log.info("Attempting to load routes from MongoDB...");
 *     routeConfigLoader.loadRoutes()
 *         .doOnSubscribe(subscription -> log.info("Subscribed to loadRoutes"))
 *         .doOnNext(route -> log.info("Loaded route: {}", route))
 *         .collectList()
 *         .doOnNext(this::processRoutes)
 *         .doOnError(error -> log.error("Error loading routes: ", error))
 *         .doOnTerminate(() -> log.info("Finished loading routes"))
 *         .subscribe(
 *             routes -> log.info("Routes successfully loaded: {}", routes),
 *             error -> log.error("Failed to load routes", error)
 *         );
 * }
 * }</pre>
 */
@Component
public class DynamicRouteHandler implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger log = LoggerFactory.getLogger(DynamicRouteHandler.class);

    @Autowired
    private RouteConfigLoader routeConfigLoader;

    @Autowired
    private ApplicationContext applicationContext;

    private List<RouteConfigDTO> routes = new ArrayList<>();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            log.info("Attempting to load routes from MongoDB...");

            // MongoDB에서 라우트 정보를 불러옴
            List<Routes> routes = routeConfigLoader.loadRoutes()
                .doOnSubscribe(subscription -> log.info("Subscribed to loadRoutes")) // 구독 시 로그 출력
                .doOnNext(route -> log.info("Loaded route: {}", route)) // 각 라우트를 로드할 때마다 로그 출력
                .collectList() // 모든 라우트를 리스트로 수집
                .doOnNext(this::processRoutes) // 수집된 라우트를 처리
                .doOnError(error -> log.error("Error loading routes: ", error)) // 오류 발생 시 로그 출력
                .doOnTerminate(() -> log.info("Finished loading routes")) // 완료 시 로그 출력
                .block(Duration.ofSeconds(5)); // 블로킹 호출로 타임아웃을 5초으로 설정

            log.info("Routes successfully loaded: {}", routes);
        } catch (Exception e) {
            log.error("Failed to load routes", e); // 예외 발생 시 로그 출력
        }
    }

    private void processRoutes(List<Routes> routeConfigs) {
        log.info("processRoutes start ::");

        for (Routes routeConfig : routeConfigs) {
            String path = routeConfig.getPath();
            log.info("Processing path: {}", path);

            List<String> handlerNames = routeConfig.getHandlers();
            log.info("Handlers for path {}: {}", path, handlerNames);

            List<Handler> handlers = new ArrayList<>();
            for (String handlerName : handlerNames) {
                log.info("Looking up handler: {}", handlerName);
                try {
                    Handler handler = (Handler) applicationContext.getBean(handlerName);
                    handlers.add(handler);
                    log.info("Handler {} added.", handlerName);
                } catch (Exception e) {
                    log.error("Failed to get bean for handler: {}", handlerName, e);
                }
            }

            if (handlers.isEmpty()) {
                log.warn("No handlers found for path: {}", path);
            }

            HandlerChain handlerChain = new DefaultHandlerChain(handlers);
            routes.add(new RouteConfigDTO(path, handlerChain));
            log.info("Route for path {} added with handlers: {}", path, handlers);
        }

        log.info("processRoutes completed.");
    }

    public Mono<Void> handle(ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();
        for (RouteConfigDTO route : routes) {
            if (route.getPath().equals(path)) {
                HandlerChain handlerChain = route.getHandlerChain();
                return handlerChain.handle(exchange);
            }
        }
        return Mono.empty();
    }
}
