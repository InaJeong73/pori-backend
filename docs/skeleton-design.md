# Spring Boot Skeleton Design

## 뼈대 프로젝트의 목표

이 프로젝트는 사이드 프로젝트, 해커톤, 프로토타입을 시작할 때 바로 복제해서 사용할 수 있는 Spring Boot 기반 API 서버 골격이다. 기준 레포 DEAR.K_BE의 좋은 구조와 컨벤션은 유지하되, 특정 서비스에 묶인 도메인 비즈니스 로직은 제거한다.

목표는 다음과 같다.

- 새 프로젝트 시작 후 10분 안에 로컬 실행 가능
- 공통 응답, 예외 처리, Swagger, Security, JWT 흐름을 즉시 사용 가능
- mock 로그인/회원가입/사용자 조회 API로 인증 테스트 가능
- sample CRUD API로 Controller-Service-Repository-DTO 흐름 참고 가능
- local/dev/prod profile을 기본 제공
- Docker, docker-compose, GitHub Actions CI를 기본 제공
- 새 프로젝트에서 바꿔야 할 항목이 명확한 체크리스트 제공

## 유지할 구조

기준 레포의 핵심 구조 중 다음은 유지한다.

- 도메인별 수직 패키지 구조
- `global` 패키지에 공통 설정과 공통 모듈 집중
- `controller`, `service`, `repository`, `domain`, `dto`, `exception` 계층 분리
- `dto/request`, `dto/response` 분리
- enum 기반 error code
- `ResponseTemplate<T>` 스타일의 공통 응답
- `@RestControllerAdvice` 기반 글로벌 예외 처리
- Spring Security stateless + JWT filter
- Swagger bearer auth 문서화
- profile별 yml 분리
- health check API

## 제거할 도메인 로직

다음 기준 레포의 도메인 로직은 뼈대 프로젝트에 포함하지 않는다.

- 케이크 디자인 검색/추천/상세 조회
- 매장 등록/검색/상세 조회
- 주문서, 질문, 마이페이지 주문 관리
- 알림 읽기/삭제/목록
- 이벤트와 매장/디자인 매핑
- 카카오 OAuth 실제 호출
- AWS S3 파일 업로드
- OpenFeign 외부 API client
- QueryDSL custom repository
- 운영 서버 직접 배포 스크립트

대신 다음 예제 도메인을 제공한다.

- `auth`: mock 회원가입/로그인, JWT 발급
- `user`: 현재 사용자 조회, mock 사용자 저장소
- `sample`: CRUD 예제
- `health`: 테스트용 상태 확인

## 공통 모듈 목록

기본 포함 모듈:

- `global.response`: 공통 성공 응답
- `global.exception`: 공통 예외, 에러 코드, validation error 응답
- `global.security`: JWT provider/filter, 인증 principal, entry point, access denied handler
- `global.config`: Security, CORS, JPA auditing, properties 설정
- `global.swagger`: OpenAPI 설정
- `common`: 공통 enum, marker, small utility
- `mock`: local/mock profile용 seed 또는 mock helper

선택 모듈로 문서화만 할 항목:

- S3
- OpenFeign
- QueryDSL
- Redis
- 운영 CD workflow

## 기본 패키지 구조

기본 base package는 `com.backbone`으로 둔다. 새 프로젝트에서 이 값은 가장 먼저 변경해야 한다.

```text
src/main/java/com/backbone/
├── BackboneApplication.java
├── global/
│   ├── config/
│   ├── exception/
│   │   ├── errorcode/
│   │   ├── handler/
│   │   └── response/
│   ├── response/
│   ├── security/
│   │   ├── filter/
│   │   └── jwt/
│   └── swagger/
├── auth/
│   ├── controller/
│   ├── dto/
│   │   ├── request/
│   │   └── response/
│   └── service/
├── user/
│   ├── controller/
│   ├── domain/
│   │   └── type/
│   ├── dto/
│   │   └── response/
│   ├── repository/
│   └── service/
├── sample/
│   ├── controller/
│   ├── dto/
│   │   ├── request/
│   │   └── response/
│   └── service/
├── mock/
└── common/
```

테스트 구조:

```text
src/test/java/com/backbone/
├── BackboneApplicationTests.java
├── auth/
├── global/
└── sample/
```

## 인증/인가 흐름

뼈대 프로젝트는 실제 OAuth provider 없이 mock 인증을 제공한다.

회원가입:

1. `POST /auth/register`로 email, password, nickname을 받는다.
2. password는 BCrypt로 암호화한다.
3. mock/in-memory 또는 JPA repository에 사용자를 저장한다.
4. 사용자 응답을 반환한다.

로그인:

1. `POST /auth/login`으로 email, password를 받는다.
2. 사용자를 조회하고 password를 검증한다.
3. access token과 refresh token을 발급한다.
4. access token은 응답 body와 `Authorization` header에 담는다.
5. refresh token은 응답 body에 포함한다. 실제 서비스 전환 시 httpOnly cookie 방식으로 바꿀 수 있게 README에 안내한다.

인증 요청:

1. client가 `Authorization: Bearer <accessToken>` header를 보낸다.
2. `JwtAuthenticationFilter`가 token을 추출한다.
3. `JwtTokenProvider`가 token을 검증하고 user id, role claim을 읽는다.
4. `SecurityContextHolder`에 인증 객체를 넣는다.
5. Controller는 `@AuthenticationPrincipal AuthPrincipal principal`로 사용자 정보를 받는다.

인가:

- 기본 role은 `USER`, 관리자 예시는 `ADMIN`을 둔다.
- public API: `/auth/**`, `/health`, `/swagger-ui/**`, `/v3/api-docs/**`
- 그 외 API는 인증 필요
- 관리자 예시가 필요하면 `/admin/**`에 `ADMIN` 권한을 요구하는 형태로 확장한다.

## API 응답 규칙

성공 응답:

```json
{
  "isSuccess": true,
  "code": "REQUEST_OK",
  "message": "요청이 성공했습니다.",
  "results": {}
}
```

실패 응답:

```json
{
  "isSuccess": false,
  "code": "COMMON400",
  "message": "잘못된 요청입니다.",
  "results": null
}
```

validation 실패 응답:

```json
{
  "isSuccess": false,
  "code": "COMMON400",
  "message": "요청 값이 올바르지 않습니다.",
  "results": {
    "validationErrors": [
      {
        "field": "email",
        "message": "이메일 형식이어야 합니다."
      }
    ]
  }
}
```

규칙:

- 모든 Controller 응답은 `ApiResponse<T>`로 감싼다.
- 성공 code는 기본 `REQUEST_OK`를 사용한다.
- 생성 성공은 HTTP 201과 `REQUEST_CREATED` 사용을 허용한다.
- 삭제/수정 후 별도 body가 없으면 `ApiResponse.empty()`를 사용한다.
- 예외 응답도 동일한 envelope을 사용한다.

## 예외 처리 규칙

공통 타입:

- `ErrorCode`: `HttpStatus`, code, message 제공
- `GlobalErrorCode`: 공통/인증 에러
- `BusinessException`: ErrorCode 기반 runtime exception
- `GlobalExceptionHandler`: 예외를 API 응답으로 변환

기본 에러 코드:

- `COMMON400`: 잘못된 요청
- `COMMON401`: 인증 필요
- `COMMON403`: 권한 없음
- `COMMON404`: 리소스 없음
- `COMMON409`: 충돌
- `COMMON500`: 서버 오류
- `AUTH401`: 로그인 실패 또는 token 오류
- `AUTH403`: 권한 부족
- `USER404`: 사용자 없음
- `SAMPLE404`: 샘플 리소스 없음

규칙:

- 서비스 계층에서 비즈니스 실패는 `BusinessException`을 던진다.
- 도메인이 커지면 `<Domain>ErrorCode` enum을 추가한다.
- Controller에서 try-catch로 에러 응답을 직접 만들지 않는다.
- validation은 `@Valid`와 request DTO annotation으로 처리한다.
- 로그는 서버 오류는 error, 비즈니스 예외는 warn 또는 info 수준으로 남긴다.

## Swagger 문서화 규칙

기본 경로:

- Swagger UI: `/swagger-ui/index.html`
- OpenAPI JSON: `/v3/api-docs`

규칙:

- Controller에는 `@Tag`를 붙인다.
- API 메서드에는 `@Operation(summary = ..., description = ...)`를 붙인다.
- request/response DTO에는 `@Schema`를 붙인다.
- 인증이 필요한 API는 Swagger bearer token으로 테스트 가능해야 한다.
- health/auth/sample API는 초기 문서 확인용 예제로 유지한다.

## local/dev/prod profile 전략

파일:

- `application.yml`: 공통 설정, default active profile
- `application-local.yml`: H2, 상세 로그, Swagger 활성화
- `application-dev.yml`: 개발 DB, 개발 CORS origin
- `application-prod.yml`: 운영 DB, 운영 CORS origin, Swagger 비활성화 옵션

원칙:

- 민감 정보는 환경 변수로 주입한다.
- `secret` submodule은 사용하지 않는다.
- local은 H2 in-memory DB를 사용한다.
- dev/prod는 PostgreSQL을 기본 예시로 둔다.
- JWT secret은 환경 변수 `JWT_SECRET_KEY`를 우선한다.
- CORS origin은 `CORS_ALLOWED_ORIGINS` 환경 변수로 덮어쓸 수 있게 한다.

## mock 구현 전략

mock API는 새 프로젝트가 인증과 API 문서 흐름을 바로 테스트할 수 있도록 제공한다.

제공 API:

- `POST /auth/register`
- `POST /auth/login`
- `POST /auth/reissue`
- `GET /users/me`
- `GET /samples`
- `POST /samples`
- `GET /samples/{id}`
- `PUT /samples/{id}`
- `DELETE /samples/{id}`
- `GET /health`

저장 전략:

- 기본 local은 H2 + JPA repository를 사용한다.
- sample 데이터는 `mock` 패키지의 `DataInitializer`가 local profile에서 생성한다.
- 더 가벼운 해커톤용으로 바꾸고 싶으면 repository 구현만 in-memory로 교체할 수 있게 구조를 단순하게 유지한다.

## 배포 전략

기본 CI:

- push 또는 pull request 시 JDK 17 설정
- Gradle wrapper 실행 권한 부여
- `./gradlew test`
- `./gradlew bootJar`

Docker:

- multi-stage build를 사용한다.
- runtime image는 JRE 기반을 사용한다.
- active profile은 `SPRING_PROFILES_ACTIVE` 환경 변수로 지정한다.
- `/health`를 container healthcheck에 사용할 수 있게 둔다.

docker-compose:

- app 단독 실행 예시
- PostgreSQL 포함 실행 예시
- local profile은 H2라 DB 없이도 실행 가능

CD:

- 기본 레포에는 자동 운영 배포를 넣지 않는다.
- DockerHub push와 SSH 배포는 `.github/workflows/deploy.example.yml` 같은 예시로 확장 가능하게 README에 설명한다.

## 새 프로젝트에서 수정해야 할 체크리스트

복제 직후 변경:

- `settings.gradle`의 root project name
- `build.gradle`의 group/version
- base package `com.backbone`
- main class `BackboneApplication`
- README의 프로젝트명과 설명
- Swagger title/description
- Docker image name
- GitHub Actions workflow name

환경별 변경:

- `JWT_SECRET_KEY`
- `CORS_ALLOWED_ORIGINS`
- dev/prod DB URL, username, password
- 운영 profile의 Swagger 노출 여부

도메인 추가 시:

- 새 도메인 패키지 생성
- `domain`, `dto/request`, `dto/response`, `repository`, `service`, `controller` 순서로 추가
- 도메인 error code enum 추가
- sample API 제거 또는 별도 example로 유지
- mock initializer 데이터 교체

보안 전환 시:

- mock login을 실제 OAuth 또는 email/password 인증으로 교체
- refresh token 저장소 도입 여부 결정
- refresh token을 httpOnly cookie로 전환할지 결정
- `/admin/**` 등 role 기반 정책 확정

배포 전:

- prod DB 연결 확인
- CORS origin 최소화
- JWT secret rotation 전략 설정
- actuator 노출 endpoint 제한
- Docker image tag 정책 결정
- GitHub Actions secret 등록

## 현재 레포 구조 제안

현재 폴더가 비어 있으므로 기준 레포와 호환되는 Gradle Spring Boot 프로젝트를 새로 구성한다.

우선 생성할 파일:

```text
build.gradle
settings.gradle
gradlew / gradlew.bat / gradle/wrapper/*
src/main/java/com/backbone/**
src/main/resources/application*.yml
src/test/java/com/backbone/**
.github/workflows/ci.yml
Dockerfile
docker-compose.yml
.gitignore
README.md
```

구현 순서:

1. Gradle 프로젝트와 기본 의존성 구성
2. 공통 응답/예외 처리
3. Security/JWT/CORS/Swagger 설정
4. auth/user mock API
5. sample CRUD API
6. health check API
7. profile yml, Docker, GitHub Actions
8. 테스트 추가 및 `./gradlew test` 검증

## 변경 계획

작은 커밋 단위로 나누면 다음이 적합하다.

1. `docs`: 기준 레포 분석과 뼈대 설계 문서 추가
2. `set`: Gradle Spring Boot 프로젝트 기본 설정 추가
3. `feat`: 공통 응답과 글로벌 예외 처리 추가
4. `feat`: JWT Security, CORS, Swagger 설정 추가
5. `feat`: mock auth/user API 추가
6. `feat`: sample CRUD와 health check API 추가
7. `cicd`: Docker, docker-compose, GitHub Actions CI 추가
8. `docs`: README 사용법 정리
9. `test`: 핵심 API와 context 테스트 추가

