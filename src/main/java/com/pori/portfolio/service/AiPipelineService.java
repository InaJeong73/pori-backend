package com.pori.portfolio.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pori.portfolio.domain.AnonymousCard;
import com.pori.portfolio.domain.Evaluation;
import com.pori.portfolio.domain.Portfolio;
import com.pori.portfolio.repository.AnonymousCardRepository;
import com.pori.portfolio.repository.EvaluationRepository;
import com.pori.portfolio.repository.PortfolioRepository;
import com.pori.user.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AiPipelineService {

    private static final Logger log = LoggerFactory.getLogger(AiPipelineService.class);

    private final PortfolioRepository portfolioRepository;
    private final AnonymousCardRepository anonymousCardRepository;
    private final EvaluationRepository evaluationRepository;
    private final ObjectMapper objectMapper;

    public AiPipelineService(PortfolioRepository portfolioRepository,
                              AnonymousCardRepository anonymousCardRepository,
                              EvaluationRepository evaluationRepository,
                              ObjectMapper objectMapper) {
        this.portfolioRepository = portfolioRepository;
        this.anonymousCardRepository = anonymousCardRepository;
        this.evaluationRepository = evaluationRepository;
        this.objectMapper = objectMapper;
    }

    @Async
    @Transactional
    public void evaluate(UUID portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElse(null);
        if (portfolio == null) return;

        try {
            portfolio.startEvaluating();
            portfolioRepository.save(portfolio);

            int totalScore = mockScore(portfolio);
            String scoresJson = buildScoresJson(totalScore);
            List<String> weaknesses = buildWeaknesses(totalScore);
            String improvementsJson = buildImprovementsJson(weaknesses);
            String firstImpression = buildFirstImpression(totalScore);

            Evaluation evaluation = Evaluation.builder()
                    .portfolio(portfolio)
                    .totalScore(totalScore)
                    .scoresJson(scoresJson)
                    .weaknessCategories(weaknesses)
                    .improvementsJson(improvementsJson)
                    .firstImpression(firstImpression)
                    .build();
            evaluationRepository.save(evaluation);

            User user = portfolio.getUser();
            AnonymousCard card = AnonymousCard.builder()
                    .portfolio(portfolio)
                    .summary(portfolio.getTitle())
                    .roleSummary(truncate(portfolio.getIntro(), 200))
                    .metrics(extractMetrics(portfolio.getTitle()))
                    .techStack(new ArrayList<>(portfolio.getTechStack()))
                    .jobCategory(user.getJobCategory() != null ? user.getJobCategory().name() : null)
                    .yearLevel(user.getYear() != null ? user.getYear() + "학년" : null)
                    .schoolGroupLabel(resolveSchoolGroup(user))
                    .totalScore(totalScore)
                    .build();
            anonymousCardRepository.save(card);

            portfolio.publish();
            portfolioRepository.save(portfolio);
        } catch (Exception e) {
            log.error("AI pipeline failed for portfolio {}", portfolioId, e);
            portfolio.fail();
            portfolioRepository.save(portfolio);
        }
    }

    private int mockScore(Portfolio p) {
        int base = 55;
        int titleLen = p.getTitle() != null ? p.getTitle().length() : 0;
        int introLen = p.getIntro() != null ? p.getIntro().length() : 0;
        int stackBonus = p.getTechStack() != null ? Math.min(p.getTechStack().size() * 2, 10) : 0;
        return Math.min(95, base + Math.min(titleLen / 3, 15) + Math.min(introLen / 20, 15) + stackBonus);
    }

    private String buildScoresJson(int total) {
        int v = total;
        Map<String, Integer> scores = new LinkedHashMap<>();
        scores.put("summaryClarity", clamp(v + rand(-8, 8)));
        scores.put("roleClarity", clamp(v + rand(-8, 8)));
        scores.put("resultMetrics", clamp(v + rand(-12, 5)));
        scores.put("techFit", clamp(v + rand(-5, 8)));
        scores.put("messageConsistency", clamp(v + rand(-8, 8)));
        try {
            return objectMapper.writeValueAsString(scores);
        } catch (Exception e) {
            return "{}";
        }
    }

    private List<String> buildWeaknesses(int score) {
        List<String> all = List.of("data_metrics", "tech_depth", "message_consistency",
                "role_clarity", "summary_clarity", "tech_fit", "result_specificity", "narrative");
        if (score >= 85) return List.of(all.get(0), all.get(6));
        if (score >= 70) return List.of(all.get(0), all.get(2), all.get(6));
        return List.of(all.get(0), all.get(1), all.get(2), all.get(6));
    }

    private String buildImprovementsJson(List<String> weaknesses) {
        List<Map<String, Object>> items = new ArrayList<>();
        Map<String, String[]> templates = Map.of(
            "data_metrics", new String[]{"정량적 성과 지표 추가", "LCP, 전환율 등 수치 기반 결과를 명시하세요.", "1"},
            "tech_depth", new String[]{"기술 선택 근거 보완", "왜 해당 기술 스택을 선택했는지 설명하세요.", "2"},
            "message_consistency", new String[]{"핵심 메시지 통일", "포트폴리오 전반에 걸쳐 일관된 메시지를 유지하세요.", "2"},
            "role_clarity", new String[]{"역할 명확화", "프로젝트에서 본인이 담당한 역할을 구체적으로 서술하세요.", "1"},
            "result_specificity", new String[]{"결과 구체화", "추상적인 성과 대신 구체적인 수치와 사례를 포함하세요.", "1"}
        );
        for (String w : weaknesses.subList(0, Math.min(3, weaknesses.size()))) {
            String[] t = templates.getOrDefault(w, new String[]{"개선 필요", "포트폴리오를 더 구체적으로 작성하세요.", "2"});
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("title", t[0]);
            item.put("detail", t[1]);
            item.put("priority", Integer.parseInt(t[2]));
            item.put("category", w);
            items.add(item);
        }
        try {
            return objectMapper.writeValueAsString(items);
        } catch (Exception e) {
            return "[]";
        }
    }

    private String buildFirstImpression(int score) {
        if (score >= 85) return "전반적으로 완성도 높은 포트폴리오입니다. 정량적 성과가 인상적입니다.";
        if (score >= 70) return "기본기가 탄탄한 포트폴리오입니다. 몇 가지 개선을 통해 더욱 강력해질 수 있습니다.";
        return "포트폴리오의 기본 구조는 갖추어졌으나, 구체적인 성과와 역할 서술을 보강하면 좋겠습니다.";
    }

    private List<String> extractMetrics(String title) {
        if (title == null) return List.of();
        List<String> metrics = new ArrayList<>();
        String[] parts = title.split("[,·]");
        for (String part : parts) {
            String t = part.trim();
            if (t.matches(".*[\\d%]+.*") && t.length() < 30) {
                metrics.add(t);
            }
        }
        return metrics.subList(0, Math.min(metrics.size(), 3));
    }

    private String resolveSchoolGroup(User user) {
        if (user.getPrivacyLevel() >= 2 && user.getSchoolGroup() != null) return user.getSchoolGroup();
        return "수도권 IT 4년제";
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }

    private int clamp(int v) { return Math.max(0, Math.min(100, v)); }
    private int rand(int min, int max) { return min + (int)(Math.random() * (max - min + 1)); }
}
