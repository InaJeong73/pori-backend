package com.pori.feed.service;

import com.pori.feed.dto.FeedItemResponse;
import com.pori.global.exception.BusinessException;
import com.pori.global.exception.errorcode.GlobalErrorCode;
import com.pori.portfolio.domain.AnonymousCard;
import com.pori.portfolio.repository.AnonymousCardRepository;
import com.pori.portfolio.repository.PortfolioRepository;
import com.pori.user.domain.User;
import com.pori.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class FeedService {

    private final AnonymousCardRepository anonymousCardRepository;
    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;

    public FeedService(AnonymousCardRepository anonymousCardRepository,
                        PortfolioRepository portfolioRepository,
                        UserRepository userRepository) {
        this.anonymousCardRepository = anonymousCardRepository;
        this.portfolioRepository = portfolioRepository;
        this.userRepository = userRepository;
    }

    public Page<FeedItemResponse> getFeed(UUID requesterId, String jobCategory, String yearLevel,
                                           int scoreMin, int scoreMax, int page, int size) {
        Page<AnonymousCard> cards = anonymousCardRepository.findFeed(
                jobCategory, yearLevel, scoreMin, scoreMax, PageRequest.of(page, size));
        return cards.map(card -> {
            User user = card.getPortfolio().getUser();
            return FeedItemResponse.from(card, user);
        });
    }

    public FeedItemResponse getCardDetail(UUID requesterId, UUID cardId) {
        checkGate(requesterId);
        AnonymousCard card = anonymousCardRepository.findById(cardId)
                .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));
        User user = card.getPortfolio().getUser();
        return FeedItemResponse.from(card, user);
    }

    private void checkGate(UUID userId) {
        long published = portfolioRepository.countPublishedByUserId(userId);
        int maxScore = portfolioRepository.findMaxScoreByUserId(userId).orElse(0);
        if (published == 0 || maxScore < 60) {
            throw new BusinessException(GlobalErrorCode.FORBIDDEN);
        }
    }
}
