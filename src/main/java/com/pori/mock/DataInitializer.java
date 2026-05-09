package com.pori.mock;

import com.pori.portfolio.dto.request.PortfolioCreateRequest;
import com.pori.portfolio.domain.JobCategory;
import com.pori.portfolio.repository.PortfolioRepository;
import com.pori.portfolio.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioService portfolioService;

    @Override
    public void run(String... args) {
        if (portfolioRepository.count() > 0) return;

        // 90점대 - Excellent (프론트엔드)
        portfolioService.create(new PortfolioCreateRequest(
                "김지현", "React & Next.js 풀스택 포트폴리오",
                JobCategory.FRONTEND, "https://jihyun-dev.vercel.app",
                List.of("React", "Next.js", "TypeScript", "TailwindCSS", "Zustand"),
                "실시간 협업 메모 플랫폼 개발",
                "프론트엔드 리드 및 WebSocket 통신 구조 설계, 컴포넌트 아키텍처 전반 담당",
                "여러 명이 동시에 메모를 편집할 때 충돌이 발생하는 문제를 Operational Transformation 알고리즘과 WebSocket을 조합해 해결했습니다.",
                "빠른 렌더링과 SEO가 중요했기 때문에 Next.js를 선택했고, 상태 관리는 경량 라이브러리인 Zustand를 도입해 불필요한 리렌더링을 줄였습니다.",
                "동시 편집 지연을 150ms 이하로 줄였고, Lighthouse 성능 점수 94점을 달성했습니다. 베타 테스터 50명에게 긍정적인 피드백을 받았습니다.",
                "https://github.com/jihyun-dev/collab-memo",
                "https://collab-memo.vercel.app",
                "Next.js와 WebSocket을 활용한 실시간 협업 메모 플랫폼입니다. 동시 편집, SEO, 성능 최적화를 모두 고려한 풀스택 프로젝트입니다."
        ));

        // 85점대 - Strong (백엔드)
        portfolioService.create(new PortfolioCreateRequest(
                "박준호", "Spring Boot 백엔드 엔지니어 포트폴리오",
                JobCategory.BACKEND, "https://notion.so/junho-park-portfolio",
                List.of("Java", "Spring Boot", "JPA", "PostgreSQL", "Redis", "Docker"),
                "대규모 트래픽 대응 이커머스 주문 시스템",
                "주문 도메인 백엔드 개발 및 Redis 캐싱 전략 수립, 성능 테스트 진행",
                "플래시 세일 이벤트 시 초당 3,000건 이상의 주문 요청이 들어와 DB가 다운되는 문제를 Redis 분산 락과 비동기 처리로 해결했습니다.",
                "JPA는 복잡한 도메인 로직을 객체 지향적으로 다루기 위해 선택했고, Redis는 재고 조회 속도 개선을 위해 캐시 레이어로 추가했습니다.",
                "주문 처리 응답 시간을 평균 1.2초에서 0.18초로 단축했고, DB 부하를 약 70% 감소시켰습니다.",
                "https://github.com/junho-park/ecommerce-order",
                null,
                "Spring Boot와 Redis 기반의 고성능 주문 처리 시스템입니다. 분산 락과 비동기 처리로 대규모 트래픽에 대응했습니다."
        ));

        // 75점대 - Standard (풀스택)
        portfolioService.create(new PortfolioCreateRequest(
                "이수민", "풀스택 개발자 포트폴리오",
                JobCategory.FULLSTACK, "https://sumin-portfolio.netlify.app",
                List.of("Vue.js", "Node.js", "Express", "MySQL"),
                "개인 일정 관리 웹 애플리케이션",
                "프론트엔드 및 백엔드 전반 개발",
                "기존 메모 앱들이 일정과 연동이 안 되는 문제를 해결하기 위해 캘린더와 메모를 통합한 앱을 만들었습니다.",
                "Vue.js는 학습 곡선이 낮고 컴포넌트 기반 개발에 적합하다고 판단했습니다. 백엔드는 익숙한 Node.js를 선택했습니다.",
                "사용자 10명과 함께 베타 테스트를 진행했고 일정 등록 성공률 98%를 달성했습니다.",
                "https://github.com/sumin-dev/planner-app",
                "https://sumin-planner.netlify.app",
                "Vue.js와 Node.js로 만든 일정-메모 통합 관리 앱입니다. 풀스택 개발 경험을 담은 개인 프로젝트입니다."
        ));

        // 62점대 - Standard (모바일)
        portfolioService.create(new PortfolioCreateRequest(
                "최다은", "안드로이드 모바일 앱 포트폴리오",
                JobCategory.MOBILE, "https://daeeun.notion.site",
                List.of("Kotlin", "Android", "Retrofit"),
                "동네 중고거래 앱",
                "안드로이드 앱 UI 및 API 연동 담당",
                "기존 중고거래 앱이 동네 기반 필터링을 제대로 지원하지 않아서 위치 기반 필터링 기능을 직접 구현했습니다.",
                "Kotlin은 현대적인 안드로이드 개발 표준이라 선택했습니다.",
                "앱스토어 출시 후 다운로드 200건을 달성했습니다.",
                null,
                null,
                "Kotlin으로 개발한 위치 기반 중고거래 앱입니다. 안드로이드 네이티브 개발 역량을 담았습니다."
        ));

        // 45점대 - 비공개 (미완성 포트폴리오)
        portfolioService.create(new PortfolioCreateRequest(
                "임테스트", "포트폴리오 초안",
                JobCategory.BACKEND, "https://example.com",
                List.of("Java"),
                "게시판",
                "개발",
                null,
                null,
                null,
                null,
                null,
                "자바로 만든 게시판입니다."
        ));
    }
}
