# PORI · Backend Tech Spec (v1)

> 작성: 2026-05-10
> 대상: 백엔드 개발자
> 프론트엔드 레포: [Aingthon/Propofol_FE](https://github.com/Aingthon/Propofol_FE)
> 백엔드 레포: [Aingthon/Propofol_BE](https://github.com/Aingthon/Propofol_BE) (Spring Boot)

---

## 0. 한 줄 요약

수도권 대학생 익명 포트폴리오 커뮤니티 **PORI** — 사용자가 PDF 포폴을 업로드하면 AI가 5축으로 평가하고, 익명 카드 + 핸들로 또래끼리 공유하며, 1:1 DM까지 가능한 서비스. 백엔드는 인증·포폴 CRUD·AI 평가 파이프라인·익명화·DM 채팅을 담당.

---

## 1. 시스템 구조

```
[Web (Next.js)]
      │ HTTPS · JWT
      ▼
[API Server (Spring Boot)] ──┬──► PostgreSQL
                              ├──► Object Storage (PDF) — S3/GCS
                              ├──► Vertex AI Gemini (×3 호출)
                              └──► WebSocket (DM)
```

- **인증**: JWT Access (15m) + Refresh (14d) · Google OAuth + Email Magic Link
- **DB**: PostgreSQL 15+ (JPA/Hibernate 또는 jOOQ)
- **파일**: PDF 원본은 비공개 객체 스토리지 (서명 URL로만 본인 접근)
- **AI**: Vertex AI Gemini 1.5 Flash (속도) / 1.5 Pro (검증) — Service Account
- **실시간**: WebSocket STOMP (Spring) 또는 SSE 폴리필

---

## 2. 데이터 모델 (PostgreSQL)

### 2.1 `users`

| 컬럼 | 타입 | 설명 |
|---|---|---|
| `id` | UUID PK | |
| `email` | TEXT UNIQUE | OAuth/Magic Link 이메일 |
| `handle` | TEXT UNIQUE | `@byulpyo` 형태, 소문자+숫자+언더스코어, 3~20자 |
| `display_name` | TEXT | 본명 (L3 공개에서만 노출) |
| `job_category` | TEXT NOT NULL | `frontend` / `backend` / `fullstack` / `mobile` / `data_ai` / `other` |
| `year` | SMALLINT | 1~4 |
| `school_group` | TEXT | `수도권 IT 4년제` 등 (L2 공개에서만 노출) |
| `school_name` | TEXT | (L3 공개에서만 노출) |
| `privacy_level` | SMALLINT DEFAULT 1 | 1=익명+핸들, 2=+학교/전공, 3=+본명/연락처 |
| `intro` | TEXT | 한 줄 자기소개 |
| `avatar_url` | TEXT NULL | 프로필 사진 (없으면 핸들 첫 글자 + 그라디언트) |
| `oauth_provider` | TEXT | `google` / `email` |
| `oauth_sub` | TEXT | provider별 user id (idempotent 로그인) |
| `email_verified_at` | TIMESTAMPTZ | |
| `created_at` | TIMESTAMPTZ | |

**인덱스**: `email`, `handle`, `job_category, privacy_level`

### 2.2 `portfolios`

| 컬럼 | 타입 | 설명 |
|---|---|---|
| `id` | UUID PK | |
| `user_id` | UUID FK → users | |
| `version` | INT DEFAULT 1 | v1, v2, ... |
| `parent_portfolio_id` | UUID NULL | v1을 복제해 v2 만들 때 참조 |
| `title` | TEXT NOT NULL | 카드 메인 타이틀 |
| `intro` | TEXT | 역할/임팩트 본문 |
| `tech_stack` | TEXT[] | 정해진 옵션에서 선택 (최대 20개) |
| `pdf_storage_key` | TEXT | 원본 PDF storage key (서명 URL 생성용, 본인만 접근) |
| `pdf_size_bytes` | BIGINT | |
| `pdf_pages` | INT | 페이지 수 |
| `privacy_level` | SMALLINT DEFAULT 1 | 1/2/3 |
| `status` | TEXT | `pending` → `anonymizing` → `evaluating` → `published` / `archived` / `failed` |
| `created_at` | TIMESTAMPTZ | |
| `published_at` | TIMESTAMPTZ NULL | published 시점 |

**인덱스**: `user_id`, `status`, `created_at`

### 2.3 `anonymous_cards` (공개)

| 컬럼 | 타입 | 설명 |
|---|---|---|
| `id` | UUID PK | |
| `portfolio_id` | UUID FK → portfolios UNIQUE | 1:1 |
| `summary` | TEXT | "30만 MAU 어드민 React 리빌드..." |
| `role_summary` | TEXT | 익명화된 역할 본문 |
| `metrics` | TEXT[] | `["LCP -42%", "번들 -36%"]` |
| `tech_stack` | TEXT[] | (포폴에서 복사) |
| `job_category` | TEXT | |
| `year_level` | TEXT | `4학년` |
| `school_group_label` | TEXT | (privacy_level 따라) |
| `created_at` | TIMESTAMPTZ | |

> 원본 portfolios는 본인만, anonymous_cards는 인증된 모든 사용자 SELECT 가능.

### 2.4 `evaluations`

| 컬럼 | 타입 | 설명 |
|---|---|---|
| `id` | UUID PK | |
| `portfolio_id` | UUID FK | |
| `total_score` | SMALLINT | 0~100 |
| `scores` | JSONB | `{summaryClarity, roleClarity, resultMetrics, techFit, messageConsistency}` 각 0~100 |
| `weakness_categories` | TEXT[] | enum 8종 — `data_metrics`, `tech_depth`, `message_consistency`, `role_clarity`, `summary_clarity`, `tech_fit`, `result_specificity`, `narrative` |
| `improvements` | JSONB | `[{title, detail, priority(1~3), category}]` |
| `first_impression` | TEXT | 면접관 멘트 2~3문장 |
| `evaluator_type` | TEXT | `ai` / `mentor` (Phase2+) |
| `ai_model_version` | TEXT | `gemini-1.5-flash-002` |
| `created_at` | TIMESTAMPTZ | |

### 2.5 `comments`

| 컬럼 | 타입 | 설명 |
|---|---|---|
| `id` | UUID PK | |
| `portfolio_id` | UUID FK | |
| `author_id` | UUID FK → users | (작성자 핸들만 노출) |
| `type` | TEXT | `praise` / `suggest` / `question` |
| `body` | TEXT NOT NULL | |
| `report_count` | INT DEFAULT 0 | 신고 누적 |
| `created_at` | TIMESTAMPTZ | |

### 2.6 `conversations` + `messages` (DM)

```sql
conversations (
  id UUID PRIMARY KEY,
  user_a UUID NOT NULL,  -- 항상 user_a < user_b 정렬
  user_b UUID NOT NULL,
  last_message_at TIMESTAMPTZ,
  unread_a INT DEFAULT 0,
  unread_b INT DEFAULT 0,
  created_at TIMESTAMPTZ,
  UNIQUE(user_a, user_b)
)

messages (
  id UUID PRIMARY KEY,
  conversation_id UUID NOT NULL,
  sender_id UUID NOT NULL,
  body TEXT NOT NULL,
  attached_portfolio_id UUID NULL,
  created_at TIMESTAMPTZ
)
```

**인덱스**: `messages(conversation_id, created_at DESC)` — 최근 메시지 페이징용

### 2.7 `tech_stack_options` (lookup)

`React`, `Next.js`, `Vue`, `TypeScript`, `Python`, `FastAPI`, ... (FE는 45개 사용. 백엔드에서 single source of truth로 관리하면 좋음)

### 2.8 `activity_logs` (선택)

```sql
activity_logs (
  id UUID PRIMARY KEY,
  user_id UUID FK,
  type TEXT,  -- evaluation_done | comment_created | dm_sent | bookmark | ...
  ref_id UUID NULL,
  meta JSONB,
  created_at TIMESTAMPTZ
)
```

---

## 3. 인증 (Auth)

### 3.1 흐름

**Google OAuth (1순위)**
1. FE가 Google OAuth 시작 → ID Token 획득
2. `POST /auth/google { id_token }`
3. BE: ID Token 검증 → `oauth_sub`로 user upsert → JWT 발급

**Email Magic Link (대안)**
1. `POST /auth/email/request { email }` → 1회용 토큰 메일 발송
2. 사용자가 메일 링크 클릭 → `POST /auth/email/verify { token }` → JWT 발급

### 3.2 JWT

- **Access Token**: 15분, 헤더 `Authorization: Bearer ...`
- **Refresh Token**: 14일, httpOnly secure 쿠키 또는 DB 저장
- **Claims**: `sub` (user.id), `handle`, `iat`, `exp`

### 3.3 엔드포인트

| Method | Path | 설명 |
|---|---|---|
| POST | `/auth/google` | Google ID Token → JWT |
| POST | `/auth/email/request` | Magic link 발송 |
| POST | `/auth/email/verify` | 토큰 검증 → JWT |
| POST | `/auth/refresh` | Access Token 갱신 |
| POST | `/auth/logout` | refresh 무효화 |
| GET | `/auth/me` | 현재 사용자 정보 |

### 3.4 신규 가입 플로우

1. `/auth/google` 또는 `/auth/email/verify` → 이메일 인증 성공
2. 신규라면 `users` 행 생성 (handle 자동 생성: 이메일 prefix + suffix)
3. FE: **온보딩 모달 강제** → handle / job_category / year / school_group 입력
4. `PATCH /users/me`로 저장 → 정상 로그인

---

## 4. 핵심 API 명세

> 모든 응답: `{ data: ..., error?: { code, message } }` 래퍼 권장.
> Auth 필요 시 `Authorization: Bearer <jwt>`.
> 페이징: `?cursor=...&limit=20` (cursor 기반) 또는 `?page=1&size=20`.

### 4.1 Users / Profile

| Method | Path | Body / Query | Response |
|---|---|---|---|
| GET | `/users/me` | — | User |
| PATCH | `/users/me` | `{ handle?, display_name?, job_category?, year?, school_group?, intro?, privacy_level? }` | User |
| POST | `/users/me/avatar` | multipart `file` | `{ avatar_url }` |
| GET | `/users/handles/check?handle=byulpyo` | — | `{ available: bool }` |
| DELETE | `/users/me` | — | 204 — soft delete + 30일 후 hard delete |

### 4.2 Portfolios (포리)

#### 4.2.1 작성

```http
POST /portfolios
Content-Type: multipart/form-data

pdf:        <file, max 10MB, application/pdf only>
title:      "30만 MAU 어드민 React 리빌드, LCP -42% · 번들 -36%"
intro:      "설계·구현 단독 담당. 디자인 시스템 토큰화..."
tech_stack: ["React","Next.js","TypeScript","Tailwind","Lighthouse CI"]
privacy_level: 1
```

응답:
```json
{
  "id": "uuid",
  "status": "anonymizing",
  "created_at": "2026-05-10T..."
}
```

이후 BE는 비동기로 AI 파이프라인 트리거 (§5).

#### 4.2.2 그 외

| Method | Path | 설명 |
|---|---|---|
| GET | `/portfolios/me` | 내 포폴 리스트 |
| GET | `/portfolios/:id` | **본인 포폴**: 원본 PDF 서명 URL + eval + comments 포함 |
| PATCH | `/portfolios/:id` | title / intro / tech_stack / privacy_level 수정 |
| DELETE | `/portfolios/:id` | 본인 포폴 삭제 |
| POST | `/portfolios/:id/republish` | v1 archive + v2 작성 시작 |

### 4.3 Community Feed (전체 포리)

```http
GET /portfolios/feed
  ?job=frontend            # job_category enum
  &year=4                  # year
  &score_min=80            # total_score 범위
  &score_max=89
  &skills=React,Next.js    # comma-separated, AND 매칭
  &sort=score_desc         # latest|score_desc|skill_match
  &cursor=<opaque>
  &limit=12
```

응답 (한 카드의 anonymized 형태):
```json
{
  "data": [
    {
      "card_id": "uuid",
      "handle": "byulpyo",
      "avatar": { "type": "gradient", "seed": 1 },
      "summary": "\"30만 MAU 어드민 React 리빌드, LCP -42%\"",
      "role_summary": "설계·구현 단독 담당...",
      "tech_stack": ["React","Next.js","TypeScript","Zustand"],
      "metrics": ["LCP -42%","번들 -36%"],
      "year_level": "4학년",
      "school_group_label": "수도권 IT 4년제",
      "scores": {
        "total": 88,
        "axes": [4.5, 4.8, 4.7, 4.4, 3.6]
      },
      "is_elite": true,
      "created_at": "..."
    }
  ],
  "next_cursor": "..."
}
```

> **Give-to-Get 게이트**: 본인 포폴 1개 이상 + total_score >= 60 미만이면 카드 상세/DM은 401. 리스트는 보여주되 미리보기에 마스킹.

### 4.4 Card Detail (전체 포리 상세)

```http
GET /portfolios/feed/:card_id
```

응답: 위 feed item + comments (10개)
```json
{
  "card": { ... feed item ... },
  "comments": [
    {
      "id": "uuid",
      "author_handle": "starcoder",
      "author_score": 92,
      "author_year": "4학년",
      "is_elite": true,
      "type": "suggest",
      "body": "...",
      "created_at": "..."
    }
  ],
  "comments_count": { "total": 12, "praise": 5, "suggest": 4, "question": 3 }
}
```

### 4.5 Comments

| Method | Path | Body | 비고 |
|---|---|---|---|
| GET | `/portfolios/:id/comments?type=praise&cursor=...` | — | 타입별 필터 |
| POST | `/portfolios/:id/comments` | `{ type, body }` | 본인 포폴엔 작성 X |
| DELETE | `/comments/:id` | — | 본인 댓글만 |
| POST | `/comments/:id/report` | `{ reason }` | report_count++ |

### 4.6 DM / Chat

| Method | Path | 설명 |
|---|---|---|
| GET | `/chats` | 대화 리스트 (최근순) |
| GET | `/chats/:peer_handle/messages?cursor=...` | 메시지 히스토리 |
| POST | `/chats/:peer_handle/messages` | `{ body, attached_portfolio_id? }` |
| POST | `/chats/:peer_handle/read` | 읽음 처리 (unread 0) |

**WebSocket**:
```
WS /ws/chat
  Authorization: Bearer <jwt>

→ subscribe: /topic/chat/<my_user_id>
   payload: { conversation_id, message: {...} }
```

### 4.7 Tech Stack Options

| Method | Path | 설명 |
|---|---|---|
| GET | `/tech-stack/options` | 45개 옵션 리스트 — FE가 칩 풀 렌더링용 |

응답:
```json
{
  "data": [
    { "key": "react", "label": "React", "category": "frontend" },
    { "key": "nextjs", "label": "Next.js", "category": "frontend" },
    ...
  ]
}
```

### 4.8 Activity

| Method | Path | 설명 |
|---|---|---|
| GET | `/users/me/activity?limit=20` | 최근 활동 |

---

## 5. AI 파이프라인 (Vertex AI Gemini)

> 포폴 업로드 시 비동기 큐(예: Spring `@Async` + `RabbitMQ` 또는 Cloud Tasks)로 3단계 처리.

### 5.1 Stage 1 — 익명화 (Gemini #1)

- 입력: PDF에서 추출한 텍스트 (Apache PDFBox / pdfplumber)
- 출력: anonymous_cards에 들어갈 JSON
- 안전 후처리: 정규식으로 이메일/전화/URL 패턴 한 번 더 마스킹

```
[System]
당신은 학생 포트폴리오에서 PII를 제거하고 핵심 구조 정보를 추출하는 어시스턴트입니다.
...
```

### 5.2 Stage 2 — 5축 평가 (Gemini #2)

- 입력: 익명화된 텍스트 + 직무
- 출력 (responseSchema 강제):

```json
{
  "totalScore": 0~100,
  "scores": {
    "summaryClarity": 0~100,
    "roleClarity": 0~100,
    "resultMetrics": 0~100,
    "techFit": 0~100,
    "messageConsistency": 0~100
  },
  "weaknessCategories": ["data_metrics","tech_depth"],
  "improvements": [
    {"title":"...", "detail":"...", "priority":1, "category":"data_metrics"}
  ],
  "firstImpression": "..."
}
```

### 5.3 Stage 3 — (선택) Embedding

- Phase 1+: `text-embedding-004`로 anonymous_card 임베딩 → pgvector 또는 Pinecone에 저장
- 유사 카드 추천에 사용

### 5.4 에러 처리

- JSON 파싱 실패 시 1회 retry (모델 버전 1.5-pro로 fallback 옵션)
- 5초 timeout * 2 → 최종 실패 시 `portfolios.status = 'failed'` + 사용자에게 토스트
- 안전 설정: `HARM_CATEGORY_*` BLOCK_ONLY_HIGH

---

## 6. 파일 저장 (PDF)

- **MVP**: AWS S3 또는 GCS · `portfolios/<user_id>/<portfolio_id>.pdf`
- **업로드**: Spring Multipart → 검증 (Content-Type, magic bytes, 10MB) → 스토리지 PUT → `pdf_storage_key` 저장
- **다운로드**: 본인만 — 사전서명 URL (15분 만료)
- **공개 노출 X** — anonymous_cards만 공개

---

## 7. 보안 / 프라이버시

### 7.1 RLS 또는 어플리케이션 레벨 권한

Postgres RLS를 쓸 수 없다면 **Service 레이어에서 강제**:

```java
@PreAuthorize("@portfolioOwnership.isOwner(#id, principal)")
public PortfolioDto getMy(@PathVariable UUID id) { ... }
```

원본 PDF / portfolios.intro 원본 / evaluations.improvements detail 같은 PII 위험 필드는 모두 `is_self || privacy_level >= ?` 검사.

### 7.2 익명화 보장

- AI 출력 후 정규식 후처리 (`\b\d{2,3}-\d{3,4}-\d{4}\b`, 이메일, URL)
- 학교명은 `privacy_level >= 2` 일 때만 노출
- 본명은 `privacy_level >= 3` 일 때만 노출

### 7.3 신고/모더레이션

- `comments.report_count >= 3` 자동 hide
- 어드민 대시보드에서 검토

### 7.4 Rate Limit

| 엔드포인트 | 제한 |
|---|---|
| `POST /auth/email/request` | 5/min/IP, 10/hour/email |
| `POST /portfolios` | 5/day/user |
| `POST /comments` | 30/hour/user |
| `POST /chats/.../messages` | 60/min/user |

---

## 8. 비기능 요구사항

- **응답 시간**: 일반 API p95 < 300ms, AI 파이프라인은 백그라운드 (FE는 polling 또는 WebSocket으로 진행률 받음)
- **가용성**: 99% (해커톤 데모 기준)
- **로그**: structured JSON, request_id traceable
- **모니터링**: 평균 평가 시간, AI 호출 실패율, DM 메시지 처리량
- **캐싱**: tech-stack/options (1d), public feed (30s)

---

## 9. 클라이언트 ↔ 서버 흐름 예시

### 9.1 새 포리 작성

```
[FE] POST /portfolios (multipart)
[BE] 200 { id, status: "anonymizing" }
[BE async] Stage 1 → portfolios.status = "evaluating"
[BE async] Stage 2 → evaluations row + status = "published"
[BE async] WS push: { type: "portfolio.published", portfolio_id }
[FE] WS 수신 → mypage 새로고침
```

### 9.2 DM 보내기

```
[FE] POST /chats/byulpyo/messages { body: "안녕하세요!" }
[BE] message INSERT + conversation upsert + unread++
[BE] WS push to recipient: { conversation_id, message }
[FE recipient] 새 메시지 표시 + unread badge
```

---

## 10. MVP 범위 (24h 해커톤)

| 우선순위 | 기능 |
|---|---|
| P0 | Auth (Google + Email), Users CRUD, Portfolio 등록 + AI Stage 1+2, Feed 리스트, Card 상세 |
| P0 | Comments (CRUD + 신고) |
| P0 | Privacy Level 토글 + 본인만 원본 접근 |
| P1 | DM (REST 기반 폴링으로 시작 → WebSocket은 후순위) |
| P1 | Activity log |
| P2 | Embedding 기반 유사 카드 추천 |
| P2 | 멘토 검증 인프라 (Phase 2+) |

---

## 11. 결정해야 할 것 (Open Questions)

1. **DB**: Supabase (Postgres + Auth + Storage 통합) vs Spring + RDS 별도 — Supabase가 24시간 안에 빠를 수 있음
2. **WebSocket**: Spring STOMP 아니면 SSE — 챗 트래픽 적으니 SSE도 OK
3. **PDF 추출**: Apache PDFBox로 충분? 한글 폰트 임베딩 PDF 호환성 필요
4. **AI Gateway**: Vertex AI Service Account 키를 Secret Manager로 관리
5. **이메일 발송**: SES vs SendGrid · 학교 도메인 화이트리스트 적용 여부
6. **Handle 자동 생성**: 충돌 시 suffix 정책 (`byulpyo`, `byulpyo2`, `byulpyo_3`...)

---

## 12. 다음 단계

1. ERD 그리기 + 스키마 마이그레이션 1차 (`flyway` 또는 `liquibase`)
2. Auth 모듈 (Google + JWT) — Postman으로 검증
3. `/portfolios` POST 멀티파트 + 비동기 큐 → Mock AI Stage 먼저
4. `/portfolios/feed` 리스트 + 필터 — FE 통합
5. AI Stage 실제 Vertex AI 연결
6. DM REST → WebSocket 마이그레이션

---

## 부록 A: enum 값 정리

```
job_category:        frontend | backend | fullstack | mobile | data_ai | other
privacy_level:       1 (anon+handle) | 2 (+school) | 3 (+real)
portfolio.status:    pending | anonymizing | evaluating | published | failed | archived
comment.type:        praise | suggest | question
weakness_category:   summary_clarity | role_clarity | data_metrics | tech_depth |
                     tech_fit | message_consistency | result_specificity | narrative
```

## 부록 B: 응답 코드

- `200/201` 성공
- `400` 검증 오류 (`error.code`: `VALIDATION_FAILED`, fields[])
- `401` 미인증
- `403` Give-to-Get 게이트 / 권한 없음 (`GATE_REQUIRED`)
- `404` 리소스 없음
- `413` PDF 10MB 초과
- `415` PDF 외 파일 (`UNSUPPORTED_MEDIA_TYPE`)
- `429` Rate limit
- `500` 서버 오류
- `503` AI 일시 장애 (`AI_UNAVAILABLE`)
