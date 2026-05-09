package com.pori.portfolio.service;

import com.pori.global.exception.BusinessException;
import com.pori.portfolio.domain.JobCategory;
import com.pori.portfolio.domain.Portfolio;
import com.pori.portfolio.domain.PoriGrade;
import com.pori.portfolio.dto.request.PortfolioCreateRequest;
import com.pori.portfolio.dto.response.PortfolioDetailResponse;
import com.pori.portfolio.dto.response.PortfolioSummaryResponse;
import com.pori.portfolio.exception.PortfolioErrorCode;
import com.pori.portfolio.repository.PortfolioRepository;
import com.pori.portfolio.service.ai.AiEvaluationService;
import com.pori.portfolio.service.ai.AiEvaluationService.EvaluationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final AiEvaluationService aiEvaluationService;

    @Transactional
    public PortfolioDetailResponse create(PortfolioCreateRequest req) {
        EvaluationResult eval = aiEvaluationService.evaluate(req);
        int score = eval.categoryScores().average();
        PoriGrade grade = PoriGrade.from(score);

        Portfolio portfolio = Portfolio.builder()
                .authorName(req.authorName())
                .title(req.title())
                .jobCategory(req.jobCategory())
                .portfolioUrl(req.portfolioUrl())
                .skills(req.skills() == null ? "" : String.join(",", req.skills()))
                .mainProject(req.mainProject())
                .role(req.role())
                .problemSolved(req.problemSolved())
                .techReason(req.techReason())
                .result(req.result())
                .githubUrl(req.githubUrl())
                .deployUrl(req.deployUrl())
                .summary(req.summary())
                .poriScore(score)
                .grade(grade)
                .isPublic(score >= 60)
                .isTopReference(score >= 80)
                .isHighTrust(score >= 90)
                .categoryScores(eval.categoryScores())
                .strengths(toJsonArray(eval.strengths()))
                .improvements(toJsonArray(eval.improvements()))
                .recommendation(eval.recommendation())
                .build();

        return PortfolioDetailResponse.from(portfolioRepository.save(portfolio));
    }

    @Transactional(readOnly = true)
    public List<PortfolioSummaryResponse> getPublicList(String jobCategory, Integer minScore) {
        JobCategory category = (jobCategory == null || jobCategory.isBlank())
                ? null
                : JobCategory.valueOf(jobCategory.toUpperCase());
        int min = (minScore == null) ? 0 : minScore;
        return portfolioRepository.findPublicPortfolios(category, min)
                .stream().map(PortfolioSummaryResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public PortfolioDetailResponse getDetail(Long id) {
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new BusinessException(PortfolioErrorCode.NOT_FOUND));
        if (!portfolio.isPublic()) {
            throw new BusinessException(PortfolioErrorCode.NOT_PUBLIC);
        }
        return PortfolioDetailResponse.from(portfolio);
    }

    private String toJsonArray(List<String> items) {
        if (items == null || items.isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < items.size(); i++) {
            sb.append("\"").append(items.get(i).replace("\"", "\\\"")).append("\"");
            if (i < items.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
