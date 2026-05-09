package com.pori.portfolio.service.ai;

import com.pori.portfolio.domain.CategoryScores;
import com.pori.portfolio.dto.request.PortfolioCreateRequest;

import java.util.List;

public interface AiEvaluationService {

    EvaluationResult evaluate(PortfolioCreateRequest request);

    record EvaluationResult(
            CategoryScores categoryScores,
            List<String> strengths,
            List<String> improvements,
            String recommendation
    ) {}
}
