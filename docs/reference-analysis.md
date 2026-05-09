# Reference Project Analysis

기준 레포: `https://github.com/kusitms-dear-k/DEAR.K_BE.git`

분석 일자: 2026-05-06

## 분석 요약

DEAR.K_BE는 Spring Boot 3.3.x, Java 17, Gradle 기반의 API 서버다. 전체 구조는 `com.deark.be` 루트 패키지 아래에 도메인 패키지를 두고, 각 도메인 안에서 `controller`, `service`, `repository`, `domain`, `dto`, `exception`, `util` 계층을 다시 나누는 방식이다. 공통 인프라는 `global` 패키지로 모으며, 인증은 Spring Security의 stateless 설정과 JWT 기반 필터를 사용한다.

뼈대 프로젝트로 가져갈 만한 강점은 다음과 같다.

- 도메인별 수직 패키지 구조
- `global` 패키지에 공통 설정과 공통 예외/응답을 모으는 방식
- `ResponseTemplate<T>` 기반의 일관된 API 응답
- `ErrorCode` 인터페이스와 enum 기반 에러 코드
- `@RestControllerAdvice` 기반 글로벌 예외 처리
- JWT 인증 필터, entry point, access denied handler 분리
- Swagger에 bearer 인증 스키마를 등록하는 방식
- `local`, `dev`, `prod` profile을 전제로 한 로깅/설정 전략
- Docker와 GitHub Actions 기반 배포 흐름

뼈대 프로젝트에서는 케이크 주문, 매장, 이벤트, 알림 등 도메인 비즈니스 로직은 제거하고, 위 공통 구조만 일반화해서 유지하는 것이 좋다.

## 전체 폴더 구조

최상위 구조:

```text
.
├── .github/
│   ├── ISSUE_TEMPLATE/
│   └── workflows/docker-deploy.yml
├── docs/
│   └── pull_request_template.md
├── gradle/wrapper/
├── secret/                     # git submodule, application yml 보관 의도
├── src/
│   ├── main/
│   │   ├── java/com/deark/be/
│   │   └── resources/
│   └── test/java/com/deark/be/
├── build.gradle
├── Dockerfile
├── settings.gradle
└── README.md
```

주요 Java 패키지:

```text
com.deark.be
├── alarm
├── auth
├── design
├── event
├── global
├── order
├── store
└── user
```

도메인 패키지는 대체로 다음 하위 구조를 따른다.

```text
<domain>
├── controller
├── domain
│   └── type
├── dto
│   ├── request
│   └── response
├── exception
│   └── errorcode
├── repository
│   └── init
├── service
└── util
```

`global` 패키지는 다음처럼 공통 관심사를 담당한다.

```text
global
├── config
├── domain
├── dto
├── exception
│   ├── errorcode
│   ├── handler
│   └── response
├── service
└── util
```

## 패키지 네이밍 규칙

README에 명시된 컨벤션과 실제 코드가 대부분 일치한다.

- 패키지명: 소문자 단어 사용 (`auth`, `global`, `store`)
- 클래스명: PascalCase (`JwtTokenProvider`, `SecurityConfig`)
- 메서드명: camelCase, 동사로 시작 (`createAccessToken`, `findUser`)
- 변수명: camelCase (`jwtProperties`)
- 상수명: 대문자 snake case (`USER_ROLE`)
- DB 컬럼명: snake case (`user_id`, `profile_image_url`)

기준 레포는 `com.deark.be`를 base package로 사용한다. 뼈대 프로젝트에서는 프로젝트명과 무관하게 바꾸기 쉬운 `com.backbone` 또는 `com.example.skeleton` 계열이 적합하다.

## Controller / Service / Repository / DTO / Entity 분리

Controller:

- `@RestController`, `@RequestMapping`, `@Tag`, `@Operation`을 함께 사용한다.
- 응답은 `ResponseEntity<ResponseTemplate<T>>` 형태로 감싼다.
- 인증 사용자는 `@AuthenticationPrincipal Long userId`로 받는다.
- Multipart 요청은 `@RequestPart`와 `MediaType.MULTIPART_FORM_DATA_VALUE`를 사용한다.

Service:

- `@Service`, `@RequiredArgsConstructor`를 사용한다.
- 비즈니스 로직과 repository 호출을 담당한다.
- 인증 서비스는 token 발급, refresh token 재발급, OAuth provider 연동을 담당한다.

Repository:

- Spring Data JPA repository를 기본으로 사용한다.
- 복잡한 조회는 `RepositoryCustom`, `RepositoryImpl` 패턴과 QueryDSL을 사용한다.
- 테스트/초기 데이터는 `repository/init` 아래 initializer 클래스로 분리한다.

DTO:

- `dto/request`, `dto/response`로 요청/응답을 분리한다.
- response DTO는 정적 팩토리 메서드 `from(...)`을 자주 사용한다.
- Java record와 Lombok class가 혼재되어 있다.

Entity:

- `domain` 아래 JPA entity를 둔다.
- enum은 `domain/type`에 둔다.
- `BaseTimeEntity`를 상속해 `createdAt`, `modifiedAt` 감사 필드를 사용한다.

## 예외 처리 방식

공통 예외 구조:

- `global.exception.errorcode.ErrorCode`: 모든 에러 코드 enum이 구현하는 인터페이스
- `global.exception.errorcode.GlobalErrorCode`: 공통/인증/파일 에러 코드
- `global.exception.GlobalException`: `ErrorCode`를 포함하는 공통 런타임 예외
- `<domain>.exception.<Domain>Exception`: 도메인별 런타임 예외
- `<domain>.exception.errorcode.<Domain>ErrorCode`: 도메인별 에러 코드 enum
- `global.exception.handler.GlobalExceptionHandler`: 모든 예외를 응답 포맷으로 변환

`GlobalExceptionHandler`는 `ResponseEntityExceptionHandler`를 상속하고 다음 예외를 처리한다.

- `MethodArgumentNotValidException`
- `IllegalArgumentException`
- `FeignException`
- `GlobalException`
- 도메인별 예외: `UserException`, `StoreException`, `OrderException`, `EventException`, `DesignException`, `AlarmException`
- fallback `Exception`

validation 실패는 필드 에러 목록을 `ErrorResponse.ValidationErrors`에 담는다.

## 공통 응답 포맷

성공 응답은 `global.dto.ResponseTemplate<T>`를 사용한다.

```json
{
  "isSuccess": true,
  "code": "REQUEST_OK",
  "message": "요청이 승인되었습니다.",
  "results": {}
}
```

필드:

- `isSuccess`: 성공 여부
- `code`: 성공 또는 실패 코드
- `message`: 사용자/클라이언트용 메시지
- `results`: 실제 응답 데이터

비어 있는 성공 응답은 `EMPTY_RESPONSE` 상수를 사용한다.

오류 응답은 `global.exception.response.ErrorResponse`를 사용하며, 성공 응답과 유사하게 `isSuccess`, `code`, `message`, `results`를 가진다. validation 오류의 경우 `results.validationErrors`에 필드별 오류가 담긴다.

## 인증/인가 구조

인증은 JWT 기반이다.

주요 클래스:

- `auth.controller.AuthController`
- `auth.service.AuthService`
- `auth.util.JwtTokenProvider`
- `auth.util.AuthenticationUtil`
- `auth.util.UserAuthentication`
- `auth.controller.filter.JwtAuthenticationFilter`
- `auth.controller.filter.JwtAuthenticationEntryPoint`
- `auth.controller.filter.JwtAccessDeniedHandler`
- `auth.service.type.JwtProperties`
- `auth.service.type.JwtUserDetails`

흐름:

1. `/auth/login`에서 OAuth access token을 받는다.
2. provider별 client가 사용자 정보를 조회한다.
3. 사용자 존재 여부에 따라 기존 사용자 조회 또는 신규 사용자 생성이 일어난다.
4. access token은 응답 header `Authorization: Bearer <token>`에 담는다.
5. refresh token은 `REFRESH_TOKEN` httpOnly secure cookie로 내려준다.
6. 이후 요청은 `Authorization` header의 bearer token으로 인증한다.
7. filter에서 token 검증 후 `SecurityContextHolder`에 인증 객체를 설정한다.
8. Controller는 `@AuthenticationPrincipal Long userId`로 사용자 id를 주입받는다.

특이점:

- 유효하지 않은 token이면 dummy authentication을 설정하는 로직이 있다.
- `WHITE_LIST` 외의 모든 요청은 인증이 필요하다.
- 일부 경로는 authority 기반 권한 검사(`OWNER`, `CUSTOMER`)를 한다.

뼈대 프로젝트에서는 외부 OAuth 의존성은 제거하고, mock login/register API와 JWT 발급/검증 흐름만 유지하는 편이 적합하다.

## Security 설정

`global.config.SecurityConfig`에서 설정한다.

주요 설정:

- `formLogin` 비활성화
- `httpBasic` 비활성화
- `csrf` 비활성화
- `cors` 활성화
- session policy: `STATELESS`
- whitelist:
  - `/error`
  - `/swagger-ui/**`
  - `/v3/api-docs/**`
  - `/swagger-resources/**`
  - `/webjars/**`
  - `/auth/**`
  - `/global/health-check`
- whitelist 외 요청은 인증 필요
- JWT 인증 실패는 `JwtAuthenticationEntryPoint`
- 권한 부족은 `JwtAccessDeniedHandler`
- `JwtAuthenticationFilter`를 `UsernamePasswordAuthenticationFilter` 앞에 추가
- `BCryptPasswordEncoder` bean 제공

## JWT 또는 Session 사용 방식

세션은 사용하지 않는다. JWT access token과 refresh token을 사용한다.

- access token: response header와 이후 request header에서 사용
- refresh token: secure, httpOnly, SameSite=None cookie로 전달
- JWT subject: user id
- JWT claim: `role`
- signing algorithm: HS512
- secret key는 `jwt.secret-key` 설정에서 base64 decode
- token property는 `@ConfigurationProperties(prefix = "jwt")` record로 관리

## Swagger/OpenAPI 설정

`global.config.SwaggerConfig`에서 설정한다.

- `@OpenAPIDefinition`으로 title, description, version, server 설정
- `OpenAPI` bean에서 bearer JWT security scheme 등록
- security scheme name: `bearerAuth`
- Swagger UI 경로는 Springdoc 기본값(`/swagger-ui/index.html`) 사용
- Controller 단위로 `@Tag`, API 단위로 `@Operation`을 사용

뼈대 프로젝트에서는 title/description을 generic하게 바꾸고, auth가 필요한 API에 bearer 인증을 테스트할 수 있게 유지한다.

## Global Config 구성

`global.config`의 주요 설정:

- `SecurityConfig`: Security filter chain, password encoder
- `CorsConfig`: CORS origin/method/header 설정
- `SwaggerConfig`: OpenAPI 문서 및 JWT 인증 스키마
- `JpaAuditingConfig`: JPA auditing
- `PropertiesConfig`: `JwtProperties`, `S3Properties` binding
- `QueryDslConfig`: QueryDSL JPAQueryFactory
- `OpenFeignConfig`: Feign 설정
- `S3Config`: AWS S3 설정

뼈대 프로젝트에서는 QueryDSL, Feign, S3는 선택 모듈로 문서화하고 기본 코드에서는 제거하는 것이 좋다. 사이드 프로젝트 초기 골격에는 Web, Security, Validation, JPA, H2, Swagger, Actuator, JWT 정도가 적절하다.

## CORS 설정

`global.config.CorsConfig`에서 `CorsConfigurationSource` bean을 제공한다.

- origins: `cors.allowed.origins` property로 주입
- methods: `POST`, `GET`, `PUT`, `DELETE`, `PATCH`, `OPTIONS`
- headers: `*`
- exposed headers: `Authorization`, `Set-Cookie`, `REFRESH_TOKEN`
- credentials: `true`
- pattern: `/**`

뼈대 프로젝트에서도 property 기반 origins 전략을 유지한다.

## Mock 데이터 또는 테스트용 구현 방식

초기 데이터는 `repository/init` 아래 initializer로 나뉘어 있다. 공통 annotation `DummyDataInit`은 다음 특성을 가진다.

- `@Component`
- `@Transactional`
- `@Profile({"local", "dev", "prod"})`
- class-level annotation으로 initializer에 붙이는 방식

기준 레포는 실제 도메인 데이터를 많이 초기화하는 구조다. 뼈대 프로젝트에서는 DB seed 대신 `mock` profile에서 인메모리 mock repository/service를 제공하거나, `CommandLineRunner`로 예제 사용자/샘플 데이터를 넣는 정도가 적합하다.

## 배포 파이프라인

`.github/workflows/docker-deploy.yml`은 `main` push 시 다음을 수행한다.

1. checkout with submodules
2. JDK 17 setup
3. `chmod +x ./gradlew`
4. `./gradlew bootJar -x test`
5. Docker Buildx setup
6. DockerHub login
7. Docker image build and push
8. SSH로 서버 접속
9. 기존 container stop/remove
10. 새 image pull 후 `docker run -d -p 8080:8080`

뼈대 프로젝트에서는 기본 CI는 test/build까지만 두고, CD는 주석 또는 별도 workflow 예시로 제공하는 편이 안전하다.

## Docker / docker-compose / GitHub Actions 구성

Dockerfile:

- base image: `openjdk:17-jdk`
- jar: `build/libs/be-0.0.1-SNAPSHOT.jar`를 `app.jar`로 복사
- timezone: `Asia/Seoul`
- `SPRING_PROFILES_ACTIVE`, `ENV` build arg/env 사용
- entrypoint에서 active profile 지정

docker-compose 파일은 기준 레포에 포함되어 있지 않다. README에는 Docker Compose 사용 기술 스택이 언급되어 있으나 실제 파일은 확인되지 않았다.

GitHub Actions는 배포 중심이며 테스트를 제외하고 `bootJar -x test`로 빌드한다. 뼈대 프로젝트에서는 PR/Push 기본 CI에서 테스트를 반드시 실행하는 쪽이 더 적합하다.

## application.yml 또는 profile 분리 방식

application yml은 `secret` git submodule에 두고, Gradle `copyConfig` task가 `secret/*.yml`을 `src/main/resources`로 복사하는 방식이다.

`build.gradle` 관련 설정:

```gradle
tasks.register('copyConfig', Copy) {
    copy {
        from 'secret'
        include '*.yml'
        into 'src/main/resources'
    }
}

tasks.named('processResources') {
    dependsOn 'copyConfig'
}
```

README에는 `application-dev.yml`, `application-prod.yml`이 resources에 위치한다고 설명되어 있다. 로깅 설정은 `local`, `dev`, `prod` spring profile별로 분리되어 있다.

뼈대 프로젝트에서는 private submodule 없이 다음 파일을 직접 제공하는 것이 좋다.

- `application.yml`
- `application-local.yml`
- `application-dev.yml`
- `application-prod.yml`

민감 정보는 환경 변수로 주입한다.

## 테스트 코드 스타일

현재 확인된 테스트는 기본 context load 테스트만 있다.

```java
@SpringBootTest(classes = BeApplication.class)
class BeApplicationTests {
    @Test
    void contextLoads() {
    }
}
```

뼈대 프로젝트에서는 최소한 다음 테스트를 추가하는 것이 좋다.

- context load
- public health API
- auth mock login
- protected API unauthorized
- sample CRUD happy path
- global exception validation error

## 코드 컨벤션

기준 레포에서 관찰한 코드 스타일:

- Lombok 적극 사용: `@Getter`, `@Builder`, `@RequiredArgsConstructor`, `@NoArgsConstructor`, `@Slf4j`
- 생성자 주입
- Controller는 `ResponseEntity`를 명시적으로 반환
- DTO 변환은 `from(...)` 정적 팩토리 선호
- enum 기반 type과 error code 사용
- `@Valid`로 request validation
- `@ConfigurationProperties` record 사용
- JPA entity는 protected no-args constructor 사용
- 공통 auditing entity 사용
- 도메인별 custom exception과 error code 분리

## 뼈대 프로젝트에 유지할 것

- 도메인별 수직 패키지 구조
- `global` 공통 패키지
- `ResponseTemplate` 계열의 공통 응답 포맷
- `ErrorCode` 인터페이스와 enum 기반 에러 코드
- 글로벌 예외 처리
- JWT stateless 인증 구조
- Swagger bearer auth 설정
- CORS property 분리
- profile별 application yml
- Dockerfile, docker-compose, GitHub Actions 기본 골격
- health check API

## 뼈대 프로젝트에서 제거할 것

- 카카오 OAuth 실제 연동
- S3, Feign, QueryDSL, Hibernate Spatial 기본 의존성
- 케이크 디자인, 매장, 주문, 이벤트, 알림 도메인 로직
- secret git submodule 의존성
- 운영 서버 직접 배포 workflow
- 실제 서비스명/브랜드/이미지/ERD

