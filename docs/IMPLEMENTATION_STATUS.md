# Pori 백엔드 구현 현황

> 마지막 업데이트: 2026-05-10

---

## 목차

1. [전체 요약](#1-전체-요약)
2. [인증 (Auth)](#2-인증-auth)
3. [사용자 (User)](#3-사용자-user)
4. [포트폴리오 (Portfolio)](#4-포트폴리오-portfolio)
5. [피드 (Feed)](#5-피드-feed)
6. [댓글 (Comment)](#6-댓글-comment)
7. [1:1 메시지 (Chat)](#7-11-메시지-chat)
8. [기술스택 (TechStack)](#8-기술스택-techstack)
9. [공통 인프라](#9-공통-인프라)
10. [전체 API 목록](#10-전체-api-목록)
11. [데이터베이스 테이블](#11-데이터베이스-테이블)
12. [핵심 비즈니스 로직](#12-핵심-비즈니스-로직)

---

## 1. 전체 요약

| 모듈 | 엔드포인트 수 | 구현 상태 |
|------|:---:|:---:|
| 인증 (Auth) | 4 | ✅ 완료 |
| 사용자 (User) | 4 | ✅ 완료 |
| 포트폴리오 (Portfolio) | 6 | ✅ 완료 |
| 피드 (Feed) | 2 | ✅ 완료 |
| 댓글 (Comment) | 4 | ✅ 완료 |
| 1:1 메시지 (Chat) | 4 | ✅ 완료 |
| 기술스택 (TechStack) | 1 | ✅ 완료 |
| 헬스체크 | 1 | ✅ 완료 |
| **합계** | **26** | |

---

## 2. 인증 (Auth)

### 엔드포인트

| 메서드 | 경로 | 인증 필요 | 설명 |
|--------|------|:---:|------|
| POST | `/auth/register` | ❌ | 회원가입 |
| POST | `/auth/login` | ❌ | 로그인 |
| POST | `/auth/refresh` | ❌ | 액세스 토큰 갱신 |
| GET | `/auth/me` | ✅ | 현재 로그인 사용자 정보 조회 |

### 회원가입 요청 (`POST /auth/register`)

```json
{
  "email": "user@example.com",
  "password": "password123",   // 최소 8자
  "handle": "my_handle"        // 3~20자, 중복 불가
}
```

### 로그인 응답 (`POST /auth/login`)

```json
{
  "accessToken": "eyJ...",    // 유효기간 15분
  "refreshToken": "eyJ..."    // 유효기간 14일
}
```

### 주요 특징

- 비밀번호 BCrypt 암호화
- JWT 토큰 기반 무상태(stateless) 인증
- 이메일 / handle 중복 시 409 Conflict 응답
- 토큰에는 `userId`, `handle` 정보 포함

---

## 3. 사용자 (User)

### 엔드포인트

| 메서드 | 경로 | 인증 필요 | 설명 |
|--------|------|:---:|------|
| GET | `/users/me` | ✅ | 내 프로필 조회 |
| PATCH | `/users/me` | ✅ | 내 프로필 수정 |
| DELETE | `/users/me` | ✅ | 계정 탈퇴 |
| GET | `/users/handles/check?handle=xxx` | ✅ | handle 중복 확인 |

### 사용자 프로필 필드

| 필드 | 타입 | 설명 |
|------|------|------|
| `handle` | String | 고유 닉네임 |
| `displayName` | String | 표시 이름 |
| `jobCategory` | Enum | 직무 분야 |
| `year` | Short | 학년 |
| `schoolGroup` | String | 학교 계열 |
| `schoolName` | String | 학교 이름 |
| `intro` | String | 자기소개 |
| `privacyLevel` | short | 프로필 공개 범위 (기본값 1) |

### 직무 분야 (JobCategory)

`FRONTEND` · `BACKEND` · `FULLSTACK` · `MOBILE` · `DATA_AI` · `OTHER`

### 주요 특징

- 계정 탈퇴는 소프트 삭제 (`deleted = true`) 처리
- PATCH는 null이 아닌 필드만 선택적으로 업데이트

---

## 4. 포트폴리오 (Portfolio)

### 엔드포인트

| 메서드 | 경로 | 인증 필요 | 설명 |
|--------|------|:---:|------|
| POST | `/portfolios` | ✅ | 포트폴리오 생성 |
| GET | `/portfolios/me` | ✅ | 내 포트폴리오 목록 |
| GET | `/portfolios/{id}` | ✅ | 포트폴리오 상세 조회 |
| PATCH | `/portfolios/{id}` | ✅ | 포트폴리오 수정 |
| DELETE | `/portfolios/{id}` | ✅ | 포트폴리오 삭제 |
| POST | `/portfolios/{id}/republish` | ✅ | 포트폴리오 재배포 |

### 생성 요청 (`POST /portfolios`)

```json
{
  "title": "포트폴리오 제목",
  "intro": "자세한 소개 내용...",
  "techStack": ["react", "spring-boot", "postgresql"],
  "privacyLevel": 1
}
```

### 포트폴리오 상태 흐름

```
PENDING → EVALUATING → PUBLISHED
                    ↘ FAILED
PUBLISHED → ARCHIVED (재배포 시 이전 버전)
```

### AI 평가 점수 산출 방식

| 항목 | 최대 점수 | 계산 방식 |
|------|:---:|------|
| 기본 점수 | 55점 | 고정 |
| 제목 길이 | 15점 | 제목 길이 비례 |
| 소개 글 길이 | 15점 | 소개 길이 비례 |
| 기술스택 수 | 10점 | 스택 개수 비례 |
| **최대 합계** | **95점** | |

### 재배포 (`POST /portfolios/{id}/republish`)

- 기존 카드/평가 데이터 삭제 후 새로 생성
- 버전 번호 1 증가
- 기존 포트폴리오는 `ARCHIVED` 상태로 변경

### 익명 카드 생성 조건

- 평가 점수 60점 이상인 경우에만 피드에 공개
- 90점 이상이면 `is_elite = true` 처리

---

## 5. 피드 (Feed)

### 엔드포인트

| 메서드 | 경로 | 인증 필요 | 설명 |
|--------|------|:---:|------|
| GET | `/portfolios/feed` | ✅ | 익명 포트폴리오 피드 조회 |
| GET | `/portfolios/feed/{cardId}` | ✅ | 익명 카드 상세 조회 |

### 피드 조회 파라미터

| 파라미터 | 타입 | 설명 |
|----------|------|------|
| `jobCategory` | String | 직무 분야 필터 |
| `yearLevel` | Integer | 학년 필터 |
| `scoreMin` | Integer | 최소 점수 |
| `scoreMax` | Integer | 최대 점수 |
| `page` | Integer | 페이지 번호 (기본 0) |
| `size` | Integer | 페이지 크기 (기본 12) |

### 접근 제한 (Give-to-Get 원칙)

피드 조회 조건 (둘 다 충족 필요):
1. 공개(PUBLISHED) 포트폴리오 **1개 이상**
2. 최고 평가 점수 **60점 이상**

→ 조건 미충족 시 `403 Forbidden` 반환

### 정렬 기준

높은 점수 순 (`totalScore DESC`)

---

## 6. 댓글 (Comment)

### 엔드포인트

| 메서드 | 경로 | 인증 필요 | 설명 |
|--------|------|:---:|------|
| GET | `/portfolios/{portfolioId}/comments` | ✅ | 댓글 목록 조회 |
| POST | `/portfolios/{portfolioId}/comments` | ✅ | 댓글 작성 |
| DELETE | `/comments/{commentId}` | ✅ | 댓글 삭제 |
| POST | `/comments/{commentId}/report` | ✅ | 댓글 신고 |

### 댓글 유형 (CommentType)

| 값 | 의미 |
|----|------|
| `PRAISE` | 칭찬 |
| `SUGGEST` | 제안 |
| `QUESTION` | 질문 |

### 댓글 작성 요청

```json
{
  "type": "PRAISE",
  "body": "포트폴리오 구성이 정말 깔끔하네요!"
}
```

### 주요 특징

- 포트폴리오 소유자는 자신의 포트폴리오에 댓글 작성 불가
- 댓글 삭제는 작성자 본인만 가능
- 신고 3회 누적 시 자동 숨김 처리
- 조회 시 유형(`type`) 필터 지원

---

## 7. 1:1 메시지 (Chat)

### 엔드포인트

| 메서드 | 경로 | 인증 필요 | 설명 |
|--------|------|:---:|------|
| GET | `/chats` | ✅ | 대화 목록 조회 |
| GET | `/chats/{peerHandle}/messages` | ✅ | 메시지 목록 조회 |
| POST | `/chats/{peerHandle}/messages` | ✅ | 메시지 전송 |
| POST | `/chats/{peerHandle}/read` | ✅ | 읽음 처리 |

### 메시지 전송 요청

```json
{
  "body": "안녕하세요! 포트폴리오 인상깊게 봤어요.",
  "attachedPortfolioId": "uuid-optional"  // 포트폴리오 첨부 (선택)
}
```

### 접근 제한 (Feed와 동일)

피드와 동일한 Give-to-Get 조건 적용:
1. 공개 포트폴리오 1개 이상
2. 최고 평가 점수 60점 이상

### 주요 특징

- 두 사용자 ID를 정렬하여 저장 → 대화방 중복 방지 (`userA < userB`)
- 읽음/안읽음 카운트를 Conversation 엔티티에서 관리
- 포트폴리오 첨부 가능

---

## 8. 기술스택 (TechStack)

### 엔드포인트

| 메서드 | 경로 | 인증 필요 | 설명 |
|--------|------|:---:|------|
| GET | `/tech-stack/options` | ❌ | 기술스택 옵션 전체 조회 |

### 제공 기술스택 (45개)

| 카테고리 | 항목 |
|----------|------|
| Frontend | React, Next.js, Vue, TypeScript, Tailwind CSS, Zustand, Redux, Svelte, Storybook |
| Backend | Spring Boot, Node.js, NestJS, Express, FastAPI, Django, Flask, Go, Rust, GraphQL, Java, C#, .NET |
| Database | PostgreSQL, MySQL, MongoDB, Redis, Elasticsearch |
| Infra | Docker, Kubernetes, Kafka, AWS, GCP, Terraform, Nginx, GitHub Actions |
| Data/AI | Python, PyTorch, TensorFlow, Pandas, LangChain |
| Mobile | React Native, Flutter, Swift, Kotlin |
| Design | Figma |

---

## 9. 공통 인프라

### 인증 방식

- Bearer 토큰 (Authorization 헤더)
- `JwtAuthFilter`가 모든 요청에서 토큰 검증
- 유효하지 않은 토큰 → 401 Unauthorized

### 공개 엔드포인트 (인증 불필요)

```
POST /auth/register
POST /auth/login
POST /auth/refresh
GET  /tech-stack/options
GET  /health
GET  /swagger-ui/**
GET  /v3/api-docs/**
```

### 통합 응답 형식

```json
{
  "isSuccess": true,
  "code": 200,
  "message": "요청이 성공적으로 처리되었습니다.",
  "results": { ... }
}
```

### 에러 코드 목록

| 상황 | HTTP 상태 |
|------|:---:|
| 잘못된 요청 | 400 |
| 인증 실패 | 401 |
| 권한 없음 | 403 |
| 리소스 없음 | 404 |
| 중복 데이터 | 409 |
| 서버 오류 | 500 |

### 비동기 처리

- AI 평가는 `@Async`로 별도 스레드에서 처리
- 포트폴리오 생성 API는 즉시 응답 후 백그라운드에서 평가 진행

### 초기 데이터 (DataInitializer)

앱 시작 시 자동으로 시드 데이터 생성:
- 기술스택 옵션 45개
- 샘플 유저 3명 (`alice_fe`, `bob_be`, `carol_fs` / 비밀번호: `password123`)
- 샘플 포트폴리오 3개 (AI 평가 자동 실행)

---

## 10. 전체 API 목록

```
[인증]
POST   /auth/register
POST   /auth/login
POST   /auth/refresh
GET    /auth/me

[사용자]
GET    /users/me
PATCH  /users/me
DELETE /users/me
GET    /users/handles/check

[포트폴리오]
POST   /portfolios
GET    /portfolios/me
GET    /portfolios/{id}
PATCH  /portfolios/{id}
DELETE /portfolios/{id}
POST   /portfolios/{id}/republish

[피드]
GET    /portfolios/feed
GET    /portfolios/feed/{cardId}

[댓글]
GET    /portfolios/{portfolioId}/comments
POST   /portfolios/{portfolioId}/comments
DELETE /comments/{commentId}
POST   /comments/{commentId}/report

[메시지]
GET    /chats
GET    /chats/{peerHandle}/messages
POST   /chats/{peerHandle}/messages
POST   /chats/{peerHandle}/read

[기술스택]
GET    /tech-stack/options

[헬스체크]
GET    /health
```

---

## 11. 데이터베이스 테이블

| 테이블 | 주요 컬럼 |
|--------|----------|
| `users` | id(UUID), email, password_hash, handle, display_name, job_category, school_year, school_group, school_name, intro, avatar_url, privacy_level, deleted, created_at |
| `portfolios` | id(UUID), user_id, version, parent_portfolio_id, title, intro, tech_stack(JSON), privacy_level, status, created_at, published_at |
| `evaluations` | id(UUID), portfolio_id, total_score, scores_json, weakness_categories(JSON), improvements_json, first_impression, evaluator_type, ai_model_version, created_at |
| `anonymous_cards` | id(UUID), portfolio_id, summary, role_summary, metrics(JSON), tech_stack(JSON), job_category, year_level, school_group_label, total_score, created_at |
| `comments` | id(UUID), portfolio_id, author_id, type, body, report_count, hidden, created_at |
| `conversations` | id(UUID), user_a(UUID), user_b(UUID), last_message_at, unread_a, unread_b, created_at |
| `messages` | id(UUID), conversation_id, sender_id, body, attached_portfolio_id, created_at |
| `tech_stack_options` | stack_key(PK), label, category |

---

## 12. 핵심 비즈니스 로직

### Give-to-Get 접근 제한

피드 조회 및 메시지 기능 사용 시 아래 두 조건 모두 충족 필요:

```
조건 1: 공개(PUBLISHED) 포트폴리오 수 >= 1
조건 2: 나의 최고 평가 점수 >= 60점
```

### 재배포 처리

```
1. 기존 AnonymousCard, Evaluation 삭제
2. Portfolio 상태를 ARCHIVED로 변경
3. 새 Portfolio 생성 (version + 1, parentPortfolioId = 기존 id)
4. AI 평가 비동기 재실행
```

### 댓글 신고 자동 숨김

```
reportCount >= 3 → hidden = true → 조회에서 제외
```

### 대화방 중복 방지

```
두 사용자 ID를 비교하여 항상 작은 UUID를 userA에 저장
→ (A↔B)와 (B↔A)를 동일한 대화방으로 처리
```
