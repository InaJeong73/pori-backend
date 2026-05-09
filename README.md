# Spring Boot Backbone

새로운 사이드 프로젝트, 해커톤, 프로토타입을 빠르게 시작하기 위한 Spring Boot 기반 뼈대 프로젝트입니다. 기준 프로젝트 `DEAR.K_BE`의 도메인별 패키지 구조, 공통 응답, 글로벌 예외 처리, JWT Security, Swagger 문서화 방식을 일반화해 재사용하기 쉽게 구성합니다.

## 빠른 시작

```bash
./gradlew bootRun
```

Windows PowerShell:

```powershell
.\gradlew.bat bootRun
```

기본 profile은 `local`입니다.

이미 jar를 빌드한 뒤 Windows에서 바로 실행하려면:

```powershell
.\scripts\run-local.ps1
```

## 로컬 실행

```bash
SPRING_PROFILES_ACTIVE=local ./gradlew bootRun
```

로컬 profile은 H2 in-memory DB를 사용합니다. 별도 DB 없이 실행하는 것을 기본 목표로 합니다.

주요 URL:

- API 서버: `http://localhost:8080`
- Health check: `http://localhost:8080/health`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- H2 Console: `http://localhost:8080/h2-console`

## Docker 실행

이미지 빌드:

```bash
docker build -t spring-boot-backbone .
```

컨테이너 실행:

```bash
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=local spring-boot-backbone
```

docker-compose:

```bash
docker compose up --build
```

## Swagger 접속

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

로그인 API로 access token을 받은 뒤 Swagger 우측 상단 `Authorize` 버튼에 bearer token을 입력하면 인증 API를 테스트할 수 있습니다.

입력 형식:

```text
Bearer <accessToken>
```

## Profile 설정

기본 profile 파일:

- `application.yml`: 공통 설정
- `application-local.yml`: 로컬 개발, H2 DB, Swagger 활성화
- `application-dev.yml`: 개발 서버 예시
- `application-prod.yml`: 운영 서버 예시

환경 변수 예시:

```bash
SPRING_PROFILES_ACTIVE=local
JWT_SECRET_KEY=<base64-secret>
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173
```

민감 정보는 yml에 직접 커밋하지 않고 환경 변수로 주입합니다.

## 인증 테스트 방법

회원가입:

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password1234","nickname":"tester"}'
```

로그인:

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password1234"}'
```

내 정보 조회:

```bash
curl http://localhost:8080/users/me \
  -H "Authorization: Bearer <accessToken>"
```

## 새 프로젝트 생성 시 수정할 항목

복제 직후:

- `settings.gradle`의 root project name
- `build.gradle`의 `group`, `version`
- base package `com.backbone`
- main class `BackboneApplication`
- Swagger title, description
- Docker image name
- README 프로젝트명과 설명

서비스 도메인 추가 시:

- `sample` 패키지를 실제 도메인으로 교체하거나 참고용으로 남깁니다.
- 도메인별 `controller`, `service`, `repository`, `domain`, `dto`, `exception` 구조를 유지합니다.
- 도메인별 error code enum을 추가합니다.
- mock initializer 데이터를 실제 seed 또는 테스트 fixture로 교체합니다.

운영 전:

- `JWT_SECRET_KEY`를 충분히 긴 값으로 교체합니다.
- prod CORS origin을 실제 프론트 도메인으로 제한합니다.
- prod DB 연결 정보를 환경 변수 또는 secret manager로 주입합니다.
- Swagger 운영 노출 여부를 결정합니다.
- GitHub Actions secret과 Docker image tag 정책을 정합니다.

## 패키지 구조

```text
src/main/java/com/backbone/
├── global/
│   ├── config/
│   ├── exception/
│   ├── response/
│   ├── security/
│   └── swagger/
├── auth/
│   ├── controller/
│   ├── dto/
│   └── service/
├── user/
│   ├── controller/
│   ├── domain/
│   ├── dto/
│   ├── repository/
│   └── service/
├── sample/
│   ├── controller/
│   ├── dto/
│   └── service/
├── mock/
└── common/
```

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

모든 Controller는 공통 응답 객체로 결과를 감쌉니다. body가 필요 없는 성공 응답은 empty 응답을 사용합니다.

## 예외 처리 규칙

- 비즈니스 실패는 `BusinessException`을 던집니다.
- 에러 코드는 `ErrorCode` 인터페이스를 구현한 enum으로 관리합니다.
- validation 실패는 `@Valid`와 `GlobalExceptionHandler`에서 공통 응답으로 변환합니다.
- Controller에서 개별 try-catch로 응답을 만들지 않습니다.
- 서버 오류는 공통 `COMMON500`으로 응답하고 로그를 남깁니다.

## 배포 파이프라인

기본 GitHub Actions CI는 다음을 실행합니다.

1. checkout
2. JDK 17 setup
3. Gradle wrapper 실행 권한 부여
4. `./gradlew test`
5. `./gradlew bootJar`

기본 레포에는 운영 서버 자동 배포를 넣지 않습니다. DockerHub push, SSH 배포, cloud deploy는 프로젝트별 인프라가 정해진 뒤 별도 workflow로 확장합니다.

## 참고 문서

- 기준 레포 분석: `docs/reference-analysis.md`
- 뼈대 설계: `docs/skeleton-design.md`
