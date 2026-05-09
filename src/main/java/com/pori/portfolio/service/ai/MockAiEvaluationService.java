package com.pori.portfolio.service.ai;

import com.pori.portfolio.domain.CategoryScores;
import com.pori.portfolio.dto.request.PortfolioCreateRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock AI evaluator based on input completeness and length.
 * Replace this bean with a real AI API implementation to go live.
 */
@Service
public class MockAiEvaluationService implements AiEvaluationService {

    @Override
    public EvaluationResult evaluate(PortfolioCreateRequest req) {
        CategoryScores scores = CategoryScores.builder()
                .projectClarity(scoreByLength(req.mainProject(), 50, 200))
                .problemSolving(scoreByLength(req.problemSolved(), 30, 150))
                .techJustification(scoreByLength(req.techReason(), 30, 150))
                .contribution(scoreByLength(req.role(), 30, 120))
                .impact(scoreByLength(req.result(), 20, 100))
                .evidence(scoreEvidence(req.githubUrl(), req.deployUrl()))
                .jobFit(scoreJobFit(req.skills(), req.jobCategory()))
                .readability(scoreByLength(req.summary(), 30, 100))
                .build();

        return new EvaluationResult(scores, buildStrengths(req, scores), buildImprovements(req, scores), buildRecommendation(scores));
    }

    private int scoreByLength(String text, int minGood, int maxGood) {
        if (text == null || text.isBlank()) return 20;
        int len = text.trim().length();
        if (len < minGood) return 40 + (len * 30 / minGood);
        if (len >= maxGood) return 95;
        return 70 + ((len - minGood) * 25 / (maxGood - minGood));
    }

    private int scoreEvidence(String github, String deploy) {
        int score = 20;
        if (github != null && !github.isBlank()) score += 40;
        if (deploy != null && !deploy.isBlank()) score += 35;
        return Math.min(score, 100);
    }

    private int scoreJobFit(List<String> skills, com.pori.portfolio.domain.JobCategory category) {
        if (skills == null || skills.isEmpty()) return 30;
        int base = Math.min(50 + skills.size() * 8, 85);
        return switch (category) {
            case FRONTEND -> containsAny(skills, "React", "Vue", "Angular", "Next", "TypeScript", "JavaScript") ? base + 10 : base;
            case BACKEND -> containsAny(skills, "Spring", "Java", "Node", "Django", "FastAPI", "Go", "Kotlin") ? base + 10 : base;
            case FULLSTACK -> skills.size() >= 4 ? base + 10 : base;
            case DATA_AI -> containsAny(skills, "Python", "PyTorch", "TensorFlow", "Pandas", "ML", "AI") ? base + 10 : base;
            default -> base;
        };
    }

    private boolean containsAny(List<String> skills, String... keywords) {
        for (String skill : skills) {
            for (String kw : keywords) {
                if (skill.toLowerCase().contains(kw.toLowerCase())) return true;
            }
        }
        return false;
    }

    private List<String> buildStrengths(PortfolioCreateRequest req, CategoryScores s) {
        List<String> list = new ArrayList<>();
        if (s.getContribution() >= 70) list.add("프로젝트에서 본인의 역할이 명확하게 드러납니다.");
        if (s.getEvidence() >= 60) list.add("GitHub 또는 배포 링크가 포함되어 신뢰도가 높습니다.");
        if (s.getProjectClarity() >= 75) list.add("프로젝트 설명이 구체적으로 잘 작성되어 있습니다.");
        if (s.getTechJustification() >= 70) list.add("기술 선택의 이유가 논리적으로 서술되어 있습니다.");
        if (s.getJobFit() >= 80) list.add("희망 직무와 기술 스택의 연관성이 높습니다.");
        if (list.isEmpty()) list.add("포트폴리오 링크가 포함되어 기본 검토가 가능합니다.");
        return list;
    }

    private List<String> buildImprovements(PortfolioCreateRequest req, CategoryScores s) {
        List<String> list = new ArrayList<>();
        if (s.getProblemSolving() < 65) list.add("문제 해결 과정을 더 구체적으로 서술해 주세요.");
        if (s.getImpact() < 65) list.add("성과 또는 결과를 수치나 구체적인 사례로 보완해 주세요.");
        if (s.getEvidence() < 60) list.add("GitHub 또는 배포 링크를 추가하면 신뢰도가 높아집니다.");
        if (s.getTechJustification() < 60) list.add("기술 스택 선택 이유를 추가로 설명해 주세요.");
        if (s.getReadability() < 65) list.add("포트폴리오 요약을 더 풍부하게 작성해 주세요.");
        return list;
    }

    private String buildRecommendation(CategoryScores s) {
        if (s.average() >= 80) return "전반적으로 완성도 높은 포트폴리오입니다. 성과 지표를 수치로 보완하면 더욱 강력해집니다.";
        if (s.average() >= 60) return "프로젝트별 문제 상황, 해결 방법, 결과를 더 구체적으로 작성해보세요.";
        return "각 항목을 충분히 작성하고, GitHub 및 배포 링크를 추가한 뒤 다시 제출해보세요.";
    }
}
