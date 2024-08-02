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

## 설치 및 실행 방법

### 1. 프로젝트 클론

```sh
git clone https://github.com/your-repo/inilinkgateway.git
cd inilinkgateway
