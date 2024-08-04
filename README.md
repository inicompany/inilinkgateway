# inilinkgateway

비동기 방식의 API Gateway로, MongoDB에서 라우팅 설정을 읽어와 동적으로 라우트를 구성합니다. 또한, 자동화된 CI/CD 파이프라인을 통해 API의 배포와 관리를 간편하게 처리할 수 있도록 설계되었습니다.

## 주요 기능

- **비동기 방식의 라우팅**: MongoDB에서 라우팅 설정을 비동기적으로 로드하여 API 게이트웨이를 동적으로 구성합니다.
- **자동 CI/CD 통합**: CI/CD 파이프라인을 통해 API의 빌드, 테스트, 배포를 자동화합니다.
- **Reactive Programming**: Spring WebFlux와 같은 리액티브 프로그래밍 모델을 활용하여 고성능의 비동기 API 처리를 지원합니다.
- **확장 가능한 구조**: 새로운 라우트와 핸들러를 쉽게 추가하고 관리할 수 있도록 유연한 구조로 설계되었습니다.

## 기술 스택

- **Java 21**
- **Spring Boot 3.3.2**
- **Spring WebFlux**
- **MongoDB**
- **Gradle**
- **Git**
- **CI/CD 툴 (Jenkins, GitHub Actions 등)**

## 주요 구성 요소

- **DynamicBeanConfig**: MongoDB에서 핸들러 정보를 읽어와 동적으로 빈을 등록하는 역할을 합니다.
- **DynamicClassLoader**: 주어진 소스 코드를 동적으로 컴파일하여 클래스를 로드합니다.
- **RouteConfigLoader**: MongoDB에서 API 라우트 정보를 로드합니다.
- **DynamicRouteHandler**: 로드된 라우트 정보를 기반으로 요청을 처리합니다.
- **HandlerSourceRepository**: MongoDB에서 핸들러 정보를 가져오는 리포지토리입니다.
- **RouteRepository**: MongoDB에서 라우트 정보를 가져오는 리포지토리입니다.
- **GatewayRouter**: 실제 라우트 처리를 수행합니다.

## 동작 원리

### DynamicBeanConfig 클래스

- `@Configuration`으로 설정된 이 클래스는 애플리케이션이 시작될 때 MongoDB에서 핸들러 정보를 읽어와 동적으로 빈을 등록합니다.
- `@PostConstruct` 메서드 `loadHandlers`는 애플리케이션 초기화 시 실행됩니다.
- `handlerSourceRepository`를 사용해 모든 핸들러 문서를 MongoDB에서 읽어오고, `registerBean` 메서드를 통해 동적으로 빈을 등록합니다.
- `DynamicClassLoader`를 사용해 소스 코드를 컴파일하고 클래스를 로드하여 빈으로 등록합니다.

### DynamicClassLoader 클래스

- 주어진 소스 코드를 동적으로 컴파일하여 JVM 내에서 클래스를 로드합니다.
- `defineClass` 메서드는 Java 컴파일러를 사용해 소스 코드를 컴파일하고, 컴파일된 바이트 코드를 로드하여 클래스로 정의합니다.

### RouteConfigLoader 클래스

- `RouteRepository`를 사용해 MongoDB에서 라우트 정보를 읽어옵니다.
- `loadRoutes` 메서드는 라우트 정보를 Flux로 반환합니다.

### DynamicRouteHandler 클래스

- `ApplicationListener<ContextRefreshedEvent>`를 구현하여 애플리케이션 컨텍스트가 초기화된 후 라우트 정보를 로드합니다.
- `loadRoutes` 메서드는 `RouteConfigLoader`를 통해 라우트 정보를 로드하고, 각 라우트에 대한 핸들러를 설정합니다.
- `handle` 메서드는 요청을 받아 라우트에 맞는 핸들러 체인을 실행합니다.

## 예제 동작 흐름

### 애플리케이션 시작

1. `DynamicBeanConfig`의 `loadHandlers` 메서드가 호출됩니다.
2. `handlerSourceRepository`를 통해 MongoDB에서 핸들러 정보를 읽어옵니다.
3. 각 핸들러 문서를 `registerBean` 메서드를 통해 동적으로 빈으로 등록합니다.
4. 모든 핸들러가 등록된 후 `initializeRoutes` 메서드를 호출하여 라우트를 초기화합니다.

### 라우트 초기화

1. `DynamicRouteHandler`의 `onApplicationEvent` 메서드가 호출됩니다.
2. `RouteConfigLoader`를 통해 라우트 정보를 MongoDB에서 읽어옵니다.
3. 각 라우트에 대해 핸들러를 설정하고 `routes` 리스트에 추가합니다.

### 요청 처리

1. 클라이언트 요청이 들어오면 `DynamicRouteHandler`의 `handle` 메서드가 호출됩니다.
2. 요청 경로에 맞는 핸들러 체인을 찾아 실행합니다.

## 코드 예제 설명

- `DynamicBeanConfig` 클래스의 `loadHandlers` 메서드는 핸들러 정보를 읽어와 빈을 동적으로 등록하는 메서드입니다. 이 메서드는 핸들러 문서를 MongoDB에서 읽어와 동적으로 빈을 생성하고 등록합니다.
- `DynamicClassLoader`는 주어진 소스 코드를 컴파일하여 클래스를 정의하는 역할을 합니다.
- `RouteConfigLoader`는 MongoDB에서 라우트 정보를 읽어오는 역할을 합니다.
- `DynamicRouteHandler`는 애플리케이션 초기화 후 라우트 정보를 로드하고, 각 라우트에 맞는 핸들러 체인을 설정하는 역할을 합니다.
- `handle` 메서드는 요청을 받아 라우트에 맞는 핸들러 체인을 실행합니다.

## 설치 및 실행 방법

### 1. 프로젝트 클론

```sh
git clone https://github.com/your-repo/inilinkgateway.git
cd inilinkgateway
