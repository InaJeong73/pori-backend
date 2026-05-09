package com.pori.mock;

import com.pori.techstack.domain.TechStackOption;
import com.pori.techstack.repository.TechStackOptionRepository;
import com.pori.user.domain.JobCategory;
import com.pori.user.domain.User;
import com.pori.user.repository.UserRepository;
import com.pori.portfolio.domain.Portfolio;
import com.pori.portfolio.repository.PortfolioRepository;
import com.pori.portfolio.service.AiPipelineService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;
    private final TechStackOptionRepository techStackOptionRepository;
    private final AiPipelineService aiPipelineService;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                            PortfolioRepository portfolioRepository,
                            TechStackOptionRepository techStackOptionRepository,
                            AiPipelineService aiPipelineService,
                            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.portfolioRepository = portfolioRepository;
        this.techStackOptionRepository = techStackOptionRepository;
        this.aiPipelineService = aiPipelineService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedTechStack();
        seedUsers();
    }

    private void seedTechStack() {
        if (techStackOptionRepository.count() > 0) return;
        List<TechStackOption> options = List.of(
            new TechStackOption("react", "React", "frontend"),
            new TechStackOption("nextjs", "Next.js", "frontend"),
            new TechStackOption("vue", "Vue", "frontend"),
            new TechStackOption("typescript", "TypeScript", "frontend"),
            new TechStackOption("tailwind", "Tailwind CSS", "frontend"),
            new TechStackOption("zustand", "Zustand", "frontend"),
            new TechStackOption("redux", "Redux", "frontend"),
            new TechStackOption("svelte", "Svelte", "frontend"),
            new TechStackOption("storybook", "Storybook", "frontend"),
            new TechStackOption("figma", "Figma", "design"),
            new TechStackOption("spring", "Spring Boot", "backend"),
            new TechStackOption("nodejs", "Node.js", "backend"),
            new TechStackOption("nestjs", "NestJS", "backend"),
            new TechStackOption("express", "Express", "backend"),
            new TechStackOption("fastapi", "FastAPI", "backend"),
            new TechStackOption("django", "Django", "backend"),
            new TechStackOption("flask", "Flask", "backend"),
            new TechStackOption("go", "Go", "backend"),
            new TechStackOption("rust", "Rust", "backend"),
            new TechStackOption("graphql", "GraphQL", "backend"),
            new TechStackOption("postgresql", "PostgreSQL", "database"),
            new TechStackOption("mysql", "MySQL", "database"),
            new TechStackOption("mongodb", "MongoDB", "database"),
            new TechStackOption("redis", "Redis", "database"),
            new TechStackOption("elasticsearch", "Elasticsearch", "database"),
            new TechStackOption("kafka", "Kafka", "infra"),
            new TechStackOption("docker", "Docker", "infra"),
            new TechStackOption("kubernetes", "Kubernetes", "infra"),
            new TechStackOption("aws", "AWS", "infra"),
            new TechStackOption("gcp", "GCP", "infra"),
            new TechStackOption("terraform", "Terraform", "infra"),
            new TechStackOption("python", "Python", "data"),
            new TechStackOption("pytorch", "PyTorch", "data"),
            new TechStackOption("tensorflow", "TensorFlow", "data"),
            new TechStackOption("pandas", "Pandas", "data"),
            new TechStackOption("langchain", "LangChain", "data"),
            new TechStackOption("react_native", "React Native", "mobile"),
            new TechStackOption("flutter", "Flutter", "mobile"),
            new TechStackOption("swift", "Swift", "mobile"),
            new TechStackOption("kotlin", "Kotlin", "mobile"),
            new TechStackOption("java", "Java", "backend"),
            new TechStackOption("csharp", "C#", "backend"),
            new TechStackOption("dotnet", ".NET", "backend"),
            new TechStackOption("nginx", "Nginx", "infra"),
            new TechStackOption("github_actions", "GitHub Actions", "infra")
        );
        techStackOptionRepository.saveAll(options);
    }

    private void seedUsers() {
        if (userRepository.count() > 0) return;
        String pw = passwordEncoder.encode("password123");

        User u1 = User.builder().email("alice@pori.dev").passwordHash(pw).handle("alice_fe").build();
        User u2 = User.builder().email("bob@pori.dev").passwordHash(pw).handle("bob_be").build();
        User u3 = User.builder().email("carol@pori.dev").passwordHash(pw).handle("carol_fs").build();
        userRepository.saveAll(List.of(u1, u2, u3));

        Portfolio p1 = Portfolio.builder()
                .user(u1)
                .title("30만 MAU 어드민 React 리빌드, LCP -42% · 번들 -36%")
                .intro("설계·구현 단독 담당. 디자인 시스템 토큰화 및 Lighthouse CI 도입으로 성능 지표를 대폭 개선했습니다.")
                .techStack(List.of("React", "Next.js", "TypeScript", "Tailwind CSS"))
                .privacyLevel((short) 1)
                .build();
        Portfolio p2 = Portfolio.builder()
                .user(u2)
                .title("결제 MSA 전환, TPS 300 → 1200 달성")
                .intro("Spring Boot 기반 모놀리스를 MSA로 전환. Kafka 기반 이벤트 드리븐 아키텍처 설계 및 구현.")
                .techStack(List.of("Spring Boot", "Kafka", "PostgreSQL", "Docker", "Kubernetes"))
                .privacyLevel((short) 1)
                .build();
        Portfolio p3 = Portfolio.builder()
                .user(u3)
                .title("AI 기반 코드 리뷰 플랫폼, DAU 5천 달성")
                .intro("풀스택 단독 개발. GPT-4 API 연동 및 비용 최적화를 통해 월 API 비용 70% 절감.")
                .techStack(List.of("Next.js", "FastAPI", "PostgreSQL", "Redis", "AWS"))
                .privacyLevel((short) 2)
                .build();
        portfolioRepository.saveAll(List.of(p1, p2, p3));

        aiPipelineService.evaluate(p1.getId());
        aiPipelineService.evaluate(p2.getId());
        aiPipelineService.evaluate(p3.getId());
    }
}
