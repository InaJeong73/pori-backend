# Pori 기능 명세서

## 1. 프로젝트 개요

**서비스명:** Pori  
**서비스 유형:** AI 기반 개발자 포트폴리오 공유 및 평가 플랫폼  
**핵심 타깃:** 개발자 취준생  
**핵심 가치:** 포트폴리오를 AI가 평가하고, 일정 점수 이상 검증된 포트폴리오만 직무/점수 구간별로 조회할 수 있게 한다.

## 2. MVP 핵심 흐름

```text
1. 사용자가 포트폴리오 정보를 입력한다.
2. AI가 입력 정보를 기반으로 포트폴리오를 평가한다.
3. Pori Score가 산정된다.
4. 60점 이상이면 공개 게시판에 등록된다.
5. 59점 이하는 비공개 처리되고 작성자에게 개선 피드백만 제공된다.
6. 다른 사용자는 직무와 점수 구간별로 공개 포트폴리오를 탐색한다.
7. 사용자는 상세 페이지에서 상위 레퍼런스를 참고한다.
```

## 3. 기능 우선순위

### P0: 반드시 구현

```text
- 포트폴리오 등록
- AI 평가 결과 생성
- Pori Score 산정
- 점수에 따른 공개/비공개 처리
- 포트폴리오 목록 조회
- 포트폴리오 상세 조회
- 직무 필터링
- 점수 구간 필터링
```

### P1: 가능하면 구현

```text
- 평가 결과 상세 페이지
- 상위 레퍼런스 배지 표시
- 내 포트폴리오 목록
- 포트폴리오 재평가
```

### P2: 시간이 남으면 구현

```text
- 좋아요/북마크
- 조회수
- 검색
- 정렬
- 댓글/피드백
```

## 4. 화면 명세

### 4.1 홈 / 포트폴리오 목록 페이지

#### 목적

공개된 포트폴리오를 직무와 점수 구간별로 탐색한다.

#### 주요 UI 요소

```text
- 서비스명 Pori
- 한 줄 소개
- 직무 필터
- 점수 구간 필터
- 포트폴리오 카드 목록
- 포트폴리오 등록 버튼
```

#### 직무 필터

```text
전체
프론트엔드
백엔드
풀스택
모바일
데이터/AI
기타
```

#### 점수 구간 필터

```text
전체
60점 이상
80점 이상
90점 이상
```

#### 포트폴리오 카드 표시 정보

```text
- 포트폴리오 제목
- 작성자 닉네임
- 희망 직무
- Pori Score
- 등급
- 기술 스택
- 한 줄 요약
- 상위 레퍼런스 여부
```

#### 카드 클릭 시

```text
포트폴리오 상세 페이지로 이동
```

### 4.2 포트폴리오 등록 페이지

#### 목적

사용자가 포트폴리오 평가와 공개 등록을 위해 정보를 입력한다.

#### 입력 항목

```text
작성자 닉네임
포트폴리오 제목
희망 직무
포트폴리오 링크
기술 스택
대표 프로젝트
프로젝트 내 본인 역할
해결한 문제
사용한 기술과 이유
성과 또는 결과
GitHub 링크
배포 링크
포트폴리오 요약
```

#### 필수 입력값

```text
작성자 닉네임
포트폴리오 제목
희망 직무
포트폴리오 링크
기술 스택
대표 프로젝트
프로젝트 내 본인 역할
포트폴리오 요약
```

#### 제출 버튼 동작

```text
1. 입력값 검증
2. AI 평가 실행
3. 평가 결과 저장
4. 점수가 60점 이상이면 공개 상태로 저장
5. 점수가 59점 이하이면 비공개 상태로 저장
6. 평가 결과 페이지로 이동
```

### 4.3 AI 평가 결과 페이지

#### 목적

사용자가 자신의 포트폴리오 평가 결과를 확인한다.

#### 표시 정보

```text
- Pori Score
- 등급
- 공개 여부
- 항목별 점수
- 강점
- 개선점
- 추천 보완 방향
- 공개 게시판 이동 버튼
- 다시 작성하기 버튼
```

#### 공개 기준

```text
60점 이상:
공개 가능

59점 이하:
공개 불가
작성자만 평가 결과 확인 가능
```

#### 결과 메시지 예시

60점 이상:

```text
Pori Score 82점
상위 레퍼런스로 등록 가능한 포트폴리오입니다.
```

59점 이하:

```text
Pori Score 48점
아직 공개하기에는 정보가 부족합니다.
아래 개선점을 반영한 뒤 다시 제출해보세요.
```

### 4.4 포트폴리오 상세 페이지

#### 목적

사용자가 공개된 포트폴리오의 세부 내용을 확인한다.

#### 표시 정보

```text
- 포트폴리오 제목
- 작성자 닉네임
- 희망 직무
- Pori Score
- 등급
- 포트폴리오 링크
- 기술 스택
- 대표 프로젝트
- 본인 역할
- 해결한 문제
- 사용한 기술과 이유
- 성과 또는 결과
- GitHub 링크
- 배포 링크
- 포트폴리오 요약
- AI 평가 요약
```

#### 비공개 포트폴리오 접근

```text
isPublic이 false인 포트폴리오는 목록에 노출하지 않는다.
직접 URL 접근 시 "공개되지 않은 포트폴리오입니다." 메시지를 표시한다.
```

## 5. Pori Score 정책

### 5.1 점수 범위

```text
0점 ~ 100점
```

### 5.2 등급 기준

```text
90~100점: Excellent
80~89점: Strong
60~79점: Standard
0~59점: Needs Improvement
```

### 5.3 공개 정책

```text
60점 이상:
공개 게시판 등록

80점 이상:
상위 레퍼런스 표시

90점 이상:
고신뢰 포트폴리오 표시

59점 이하:
비공개
작성자에게만 평가 결과 제공
```

## 6. AI 평가 기준

MVP에서는 실제 링크/PDF 내용을 파싱하지 않고, 사용자가 입력한 구조화된 텍스트를 기준으로 평가한다.

### 평가 항목

```text
projectClarity:
프로젝트 설명의 구체성

problemSolving:
문제 해결 과정의 명확성

techJustification:
기술 스택 사용 이유의 타당성

contribution:
본인 기여도 명확성

impact:
성과 또는 결과의 구체성

evidence:
GitHub, 배포 링크 등 근거 자료 포함 여부

jobFit:
희망 직무와의 적합성

readability:
문서 구성과 가독성
```

### 항목별 점수

각 항목은 0~100점으로 평가한다.

### 총점 계산

해커톤 MVP에서는 단순 평균으로 계산한다.

```text
Pori Score = 평가 항목 점수 평균
```

### AI 평가 결과 구조

```json
{
  "score": 82,
  "grade": "Strong",
  "isPublic": true,
  "categoryScores": {
    "projectClarity": 85,
    "problemSolving": 80,
    "techJustification": 78,
    "contribution": 88,
    "impact": 72,
    "evidence": 90,
    "jobFit": 84,
    "readability": 79
  },
  "strengths": [
    "프로젝트에서 본인의 역할이 명확하게 드러납니다.",
    "GitHub와 배포 링크가 포함되어 신뢰도가 높습니다."
  ],
  "improvements": [
    "성과 지표가 조금 더 구체적으로 제시되면 좋습니다.",
    "문제 해결 과정의 전후 맥락을 보완하면 좋습니다."
  ],
  "recommendation": "프로젝트별 문제 상황, 해결 방법, 결과를 더 구체적으로 작성해보세요."
}
```

## 7. 데이터 모델 초안

### Portfolio

```ts
type Portfolio = {
  id: string;
  authorName: string;
  title: string;
  jobCategory: JobCategory;
  portfolioUrl: string;
  skills: string[];
  mainProject: string;
  role: string;
  problemSolved: string;
  techReason: string;
  result: string;
  githubUrl?: string;
  deployUrl?: string;
  summary: string;

  poriScore: number;
  grade: PoriGrade;
  isPublic: boolean;
  isTopReference: boolean;
  isHighTrust: boolean;

  categoryScores: CategoryScores;
  strengths: string[];
  improvements: string[];
  recommendation: string;

  createdAt: string;
  updatedAt: string;
};
```

### JobCategory

```ts
type JobCategory =
  | "frontend"
  | "backend"
  | "fullstack"
  | "mobile"
  | "data_ai"
  | "etc";
```

### PoriGrade

```ts
type PoriGrade =
  | "Excellent"
  | "Strong"
  | "Standard"
  | "Needs Improvement";
```

### CategoryScores

```ts
type CategoryScores = {
  projectClarity: number;
  problemSolving: number;
  techJustification: number;
  contribution: number;
  impact: number;
  evidence: number;
  jobFit: number;
  readability: number;
};
```

## 8. API 명세 초안

### 8.1 포트폴리오 목록 조회

```http
GET /api/portfolios
```

#### Query Parameters

```text
jobCategory?: frontend | backend | fullstack | mobile | data_ai | etc
minScore?: number
```

#### 동작

```text
- isPublic이 true인 포트폴리오만 반환한다.
- jobCategory가 있으면 해당 직무만 필터링한다.
- minScore가 있으면 해당 점수 이상만 반환한다.
- 최신순으로 정렬한다.
```

#### Response

```json
[
  {
    "id": "1",
    "authorName": "포리",
    "title": "프론트엔드 신입 포트폴리오",
    "jobCategory": "frontend",
    "portfolioUrl": "https://example.com",
    "skills": ["React", "TypeScript", "Next.js"],
    "summary": "React 프로젝트 3개를 중심으로 구성한 포트폴리오입니다.",
    "poriScore": 86,
    "grade": "Strong",
    "isTopReference": true,
    "isHighTrust": false,
    "createdAt": "2026-05-09T00:00:00.000Z"
  }
]
```

### 8.2 포트폴리오 상세 조회

```http
GET /api/portfolios/:id
```

#### 동작

```text
- isPublic이 true인 포트폴리오는 상세 정보를 반환한다.
- isPublic이 false인 포트폴리오는 조회 불가 처리한다.
```

#### Response

```json
{
  "id": "1",
  "authorName": "포리",
  "title": "프론트엔드 신입 포트폴리오",
  "jobCategory": "frontend",
  "portfolioUrl": "https://example.com",
  "skills": ["React", "TypeScript", "Next.js"],
  "mainProject": "개발자 포트폴리오 공유 플랫폼",
  "role": "프론트엔드 개발 및 API 연동",
  "problemSolved": "포트폴리오 레퍼런스를 찾기 어려운 문제를 해결했습니다.",
  "techReason": "빠른 개발과 컴포넌트 재사용을 위해 React를 사용했습니다.",
  "result": "MVP를 2일 내 구현하고 사용자 흐름을 완성했습니다.",
  "githubUrl": "https://github.com/example",
  "deployUrl": "https://pori.example.com",
  "summary": "React 프로젝트 3개를 중심으로 구성한 포트폴리오입니다.",
  "poriScore": 86,
  "grade": "Strong",
  "isTopReference": true,
  "isHighTrust": false,
  "categoryScores": {
    "projectClarity": 85,
    "problemSolving": 80,
    "techJustification": 78,
    "contribution": 88,
    "impact": 72,
    "evidence": 90,
    "jobFit": 84,
    "readability": 79
  },
  "strengths": [
    "본인의 역할이 명확합니다.",
    "GitHub와 배포 링크가 포함되어 있습니다."
  ],
  "improvements": [
    "성과 지표를 더 구체화하면 좋습니다."
  ],
  "recommendation": "프로젝트별 문제 상황, 해결 방법, 결과를 더 구체적으로 작성해보세요.",
  "createdAt": "2026-05-09T00:00:00.000Z"
}
```

### 8.3 포트폴리오 등록 및 AI 평가

```http
POST /api/portfolios
```

#### Request

```json
{
  "authorName": "포리",
  "title": "프론트엔드 신입 포트폴리오",
  "jobCategory": "frontend",
  "portfolioUrl": "https://example.com",
  "skills": ["React", "TypeScript", "Next.js"],
  "mainProject": "개발자 포트폴리오 공유 플랫폼",
  "role": "프론트엔드 개발 및 API 연동",
  "problemSolved": "포트폴리오 레퍼런스를 찾기 어려운 문제를 해결했습니다.",
  "techReason": "빠른 개발과 컴포넌트 재사용을 위해 React를 사용했습니다.",
  "result": "MVP를 2일 내 구현하고 사용자 흐름을 완성했습니다.",
  "githubUrl": "https://github.com/example",
  "deployUrl": "https://pori.example.com",
  "summary": "React 프로젝트 3개를 중심으로 구성한 포트폴리오입니다."
}
```

#### 동작

```text
1. 필수 입력값을 검증한다.
2. 입력된 포트폴리오 정보를 AI 평가 함수에 전달한다.
3. 평가 결과를 생성한다.
4. poriScore를 기준으로 grade, isPublic, isTopReference, isHighTrust 값을 결정한다.
5. 포트폴리오 데이터를 저장한다.
6. 저장된 포트폴리오와 평가 결과를 반환한다.
```

#### Response

```json
{
  "id": "1",
  "poriScore": 86,
  "grade": "Strong",
  "isPublic": true,
  "isTopReference": true,
  "isHighTrust": false,
  "categoryScores": {
    "projectClarity": 85,
    "problemSolving": 80,
    "techJustification": 78,
    "contribution": 88,
    "impact": 72,
    "evidence": 90,
    "jobFit": 84,
    "readability": 79
  },
  "strengths": [
    "본인의 역할이 명확합니다.",
    "GitHub와 배포 링크가 포함되어 있습니다."
  ],
  "improvements": [
    "성과 지표를 더 구체화하면 좋습니다."
  ],
  "recommendation": "프로젝트별 문제 상황, 해결 방법, 결과를 더 구체적으로 작성해보세요."
}
```

## 9. 점수 판정 로직

```ts
function getGrade(score: number): PoriGrade {
  if (score >= 90) return "Excellent";
  if (score >= 80) return "Strong";
  if (score >= 60) return "Standard";
  return "Needs Improvement";
}

function getVisibility(score: number) {
  return {
    isPublic: score >= 60,
    isTopReference: score >= 80,
    isHighTrust: score >= 90
  };
}
```

## 10. AI 평가 구현 방식

### MVP 권장 방식

해커톤에서는 실제 OpenAI API 연동이 어렵거나 시간이 부족할 수 있으므로, 아래 두 방식 중 하나로 구현한다.

### 1안: 실제 AI API 사용

```text
- 사용자가 입력한 포트폴리오 정보를 프롬프트로 구성한다.
- AI에게 항목별 점수와 피드백을 JSON 형태로 요청한다.
- 응답 JSON을 파싱해 저장한다.
```

### 2안: Mock AI 평가 함수 사용

```text
- 입력값의 길이, 필수 링크 여부, 기술 스택 개수 등을 기반으로 점수를 계산한다.
- 강점/개선점 문구는 조건에 따라 생성한다.
- 발표 시 "AI 평가 모듈로 확장 가능"하다고 설명한다.
```

해커톤 안정성을 위해 추천은 다음과 같다.

```text
기본은 Mock AI 평가 함수로 구현하고,
가능하면 실제 AI API 연동을 추가한다.
```

## 11. Mock AI 평가 기준 예시

```text
프로젝트 설명이 충분히 길면 projectClarity 가산
문제 해결 설명이 있으면 problemSolving 가산
기술 사용 이유가 있으면 techJustification 가산
본인 역할이 구체적이면 contribution 가산
성과/결과가 있으면 impact 가산
GitHub 또는 배포 링크가 있으면 evidence 가산
희망 직무와 기술 스택이 어울리면 jobFit 가산
요약과 입력값이 충분하면 readability 가산
```

## 12. 프론트엔드 라우팅 예시

```text
/                       포트폴리오 목록 페이지
/portfolios/new         포트폴리오 등록 페이지
/portfolios/:id         포트폴리오 상세 페이지
/portfolios/:id/result  AI 평가 결과 페이지
```

## 13. 개발 완료 기준

### 필수 완료 조건

```text
- 사용자가 포트폴리오 등록 폼을 작성할 수 있다.
- 등록 시 AI 평가 결과가 생성된다.
- Pori Score가 화면에 표시된다.
- 60점 이상 포트폴리오는 목록에 노출된다.
- 59점 이하 포트폴리오는 목록에 노출되지 않는다.
- 목록에서 직무 필터링이 가능하다.
- 목록에서 점수 구간 필터링이 가능하다.
- 포트폴리오 카드를 클릭하면 상세 페이지로 이동한다.
```

### 데모 시나리오

```text
1. 사용자가 Pori 메인 페이지에 접속한다.
2. 공개된 포트폴리오 목록을 확인한다.
3. 프론트엔드 직무와 80점 이상 필터를 적용한다.
4. 상위 레퍼런스 포트폴리오 상세 페이지를 확인한다.
5. 새 포트폴리오를 등록한다.
6. AI 평가 결과와 Pori Score를 확인한다.
7. 60점 이상이면 목록에 등록되는 것을 확인한다.
8. 낮은 품질 입력으로 등록하면 비공개 처리되는 것을 확인한다.
```

## 14. Claude 개발 요청 문구

아래 문구를 Claude에게 함께 전달한다.

```text
위 기능 명세서를 기준으로 해커톤 MVP를 개발해줘.

우선순위는 P0 기능 구현이야.
실제 AI API 연동이 어렵다면 Mock AI 평가 함수로 구현해도 돼.
중요한 것은 포트폴리오 등록 -> AI 평가 -> 점수 산정 -> 공개 여부 결정 -> 목록/상세 조회 흐름이 완성되는 것이야.

UI는 깔끔하고 직관적인 웹앱 형태로 만들어줘.
데이터베이스가 부담되면 우선 로컬 상태, JSON, 또는 간단한 인메모리/mock 데이터로 구현해도 돼.
다만 코드 구조는 나중에 백엔드 API와 연결하기 쉽게 분리해줘.
```
