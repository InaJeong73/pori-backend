package com.pori.portfolio.service;

import com.pori.global.exception.BusinessException;
import com.pori.global.exception.errorcode.GlobalErrorCode;
import com.pori.portfolio.domain.Portfolio;
import com.pori.portfolio.dto.*;
import com.pori.portfolio.repository.AnonymousCardRepository;
import com.pori.portfolio.repository.EvaluationRepository;
import com.pori.portfolio.repository.PortfolioRepository;
import com.pori.user.domain.User;
import com.pori.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final AnonymousCardRepository anonymousCardRepository;
    private final EvaluationRepository evaluationRepository;
    private final UserService userService;
    private final AiPipelineService aiPipelineService;

    public PortfolioService(PortfolioRepository portfolioRepository,
                             AnonymousCardRepository anonymousCardRepository,
                             EvaluationRepository evaluationRepository,
                             UserService userService,
                             AiPipelineService aiPipelineService) {
        this.portfolioRepository = portfolioRepository;
        this.anonymousCardRepository = anonymousCardRepository;
        this.evaluationRepository = evaluationRepository;
        this.userService = userService;
        this.aiPipelineService = aiPipelineService;
    }

    @Transactional
    public PortfolioSummaryResponse create(UUID userId, PortfolioCreateRequest request) {
        User user = userService.findById(userId);
        Portfolio portfolio = Portfolio.builder()
                .user(user)
                .title(request.title())
                .intro(request.intro())
                .techStack(request.techStack())
                .privacyLevel(request.privacyLevel())
                .build();
        portfolioRepository.save(portfolio);
        aiPipelineService.evaluate(portfolio.getId());
        return PortfolioSummaryResponse.from(portfolio);
    }

    public List<PortfolioSummaryResponse> getMyPortfolios(UUID userId) {
        return portfolioRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(PortfolioSummaryResponse::from)
                .toList();
    }

    public PortfolioDetailResponse getMyPortfolioDetail(UUID userId, UUID portfolioId) {
        Portfolio portfolio = portfolioRepository.findByIdAndUserId(portfolioId, userId)
                .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));
        var eval = evaluationRepository.findByPortfolioId(portfolioId).orElse(null);
        return PortfolioDetailResponse.from(portfolio, eval);
    }

    @Transactional
    public PortfolioSummaryResponse update(UUID userId, UUID portfolioId, PortfolioUpdateRequest request) {
        Portfolio portfolio = getOwnedPortfolio(userId, portfolioId);
        portfolio.update(request.title(), request.intro(), request.techStack(), request.privacyLevel());
        return PortfolioSummaryResponse.from(portfolio);
    }

    @Transactional
    public void delete(UUID userId, UUID portfolioId) {
        Portfolio portfolio = getOwnedPortfolio(userId, portfolioId);
        anonymousCardRepository.deleteByPortfolioId(portfolioId);
        portfolioRepository.delete(portfolio);
    }

    @Transactional
    public PortfolioSummaryResponse republish(UUID userId, UUID portfolioId, PortfolioCreateRequest request) {
        Portfolio old = getOwnedPortfolio(userId, portfolioId);
        old.archive();
        anonymousCardRepository.deleteByPortfolioId(portfolioId);

        User user = userService.findById(userId);
        Portfolio newPortfolio = Portfolio.builder()
                .user(user)
                .title(request.title())
                .intro(request.intro())
                .techStack(request.techStack())
                .privacyLevel(request.privacyLevel())
                .build();
        newPortfolio.setVersion(old.getVersion() + 1, old.getId());
        portfolioRepository.save(newPortfolio);
        aiPipelineService.evaluate(newPortfolio.getId());
        return PortfolioSummaryResponse.from(newPortfolio);
    }

    private Portfolio getOwnedPortfolio(UUID userId, UUID portfolioId) {
        return portfolioRepository.findByIdAndUserId(portfolioId, userId)
                .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));
    }
}
