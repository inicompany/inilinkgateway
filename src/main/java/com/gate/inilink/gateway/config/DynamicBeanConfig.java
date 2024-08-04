package com.gate.inilink.gateway.config;

import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import com.gate.inilink.gateway.handler.DynamicRouteHandler;
import com.gate.inilink.gateway.model.HandlerSourceDocument;
import com.gate.inilink.gateway.repository.HandlerSourceRepository;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Configuration
public class DynamicBeanConfig {
	
	private final HandlerSourceRepository handlerSourceRepository;
    private final ConfigurableApplicationContext applicationContext;
    private final MetadataReaderFactory metadataReaderFactory;
    private final DynamicRouteHandler dynamicRouteHandler;

    @Autowired
    public DynamicBeanConfig(HandlerSourceRepository handlerSourceRepository,
                             ConfigurableApplicationContext applicationContext,
                             MetadataReaderFactory metadataReaderFactory,
                             DynamicRouteHandler dynamicRouteHandler) {
        this.handlerSourceRepository = handlerSourceRepository;
        this.applicationContext = applicationContext;
        this.metadataReaderFactory = metadataReaderFactory;
        this.dynamicRouteHandler = dynamicRouteHandler;
    }

    private final CountDownLatch latch = new CountDownLatch(1);

    @PostConstruct
    @DependsOn("mongoConfig")
    @Lazy
    @Primary
    public void loadHandlers() {
        System.out.println("Starting handler registration process...");
        
        handlerSourceRepository.findAll()
            .doOnNext(document -> System.out.println("---Found document: " + document.getClassName()))
            .filter(HandlerSourceDocument::isEnabled)
            .doOnNext(document -> System.out.println("---Enabled document: " + document.getClassName()))
            .flatMap(this::registerBean)
            .doOnNext(result -> System.out.println("---Handler registration result: " + result))
            .collectList() // 모든 등록 작업이 완료될 때까지 대기
            .doOnSuccess(beans -> {
                if (beans.stream().allMatch(Boolean::booleanValue)) {
                    initializeRoutes(); // 핸들러 등록이 완료된 후 라우트 초기화
                } else {
                    handleInitializationError(new RuntimeException("Some handlers failed to register."));
                }
            })
            .doOnError(throwable -> {
                handleInitializationError(throwable); // 오류 발생 시 처리
            })
            .doFinally(signalType -> {
                latch.countDown();
            }) // 스트림이 완료되면 latch 감소
            .subscribe(
        		beans -> System.out.println("---Handler registration process completed successfully with beans: " + beans),
                throwable -> {
                    latch.countDown();
                },
                () -> {
                    latch.countDown();
                }
            );
        awaitHandlerInitialization();
       
    }

    private Mono<Boolean> registerBean(HandlerSourceDocument document) {
        return Mono.fromCallable(() -> {
            try {
                DynamicClassLoader classLoader = new DynamicClassLoader(ClassUtils.getDefaultClassLoader());
                Class<?> clazz = classLoader.defineClass(document.getClassName(), document.getSourceCode());
                
                GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
                beanDefinition.setBeanClass(clazz);
                BeanDefinitionRegistry registry = (BeanDefinitionRegistry) applicationContext.getBeanFactory();
                registry.registerBeanDefinition(document.getBeanName(), beanDefinition);
                
                return true; // 성공 시 true 반환
            } catch (Exception e) {
                
                e.printStackTrace();
                return false; // 실패 시 false 반환
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }


    private void handleInitializationError(Throwable e) {
        e.printStackTrace();
        // 추가 오류 처리 로직이 필요하면 여기에 작성
    }

    private void initializeRoutes() {
        System.out.println("Initializing routes...");
        try {
        	//DynamicRouteHandler dynamicRouteHandler = applicationContext.getBean(DynamicRouteHandler.class);
            DynamicRouteHandler dynamicRouteHandler = new DynamicRouteHandler();
            applicationContext.getBean(DynamicRouteHandler.class);
            
            dynamicRouteHandler.onApplicationEvent(null); // 초기화를 위해 이벤트 호출
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Routes initialized.");
    }


    public void awaitHandlerInitialization() {
        try {
            latch.await(); // 모든 핸들러가 등록될 때까지 대기
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Handler initialization interrupted", e);
        }
    }
}
