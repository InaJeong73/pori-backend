package com.pori.comment.service;

import com.pori.comment.domain.Comment;
import com.pori.comment.domain.CommentType;
import com.pori.comment.dto.CommentCreateRequest;
import com.pori.comment.dto.CommentResponse;
import com.pori.comment.repository.CommentRepository;
import com.pori.global.exception.BusinessException;
import com.pori.global.exception.errorcode.GlobalErrorCode;
import com.pori.portfolio.domain.Portfolio;
import com.pori.portfolio.repository.EvaluationRepository;
import com.pori.portfolio.repository.PortfolioRepository;
import com.pori.user.domain.User;
import com.pori.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PortfolioRepository portfolioRepository;
    private final EvaluationRepository evaluationRepository;
    private final UserService userService;

    public CommentService(CommentRepository commentRepository,
                           PortfolioRepository portfolioRepository,
                           EvaluationRepository evaluationRepository,
                           UserService userService) {
        this.commentRepository = commentRepository;
        this.portfolioRepository = portfolioRepository;
        this.evaluationRepository = evaluationRepository;
        this.userService = userService;
    }

    public Page<CommentResponse> getComments(UUID portfolioId, CommentType type, int page, int size) {
        Page<Comment> comments = commentRepository.findByPortfolioId(portfolioId, type, PageRequest.of(page, size));
        return comments.map(c -> {
            int score = evaluationRepository.findByPortfolioId(
                    portfolioRepository.findByUserIdOrderByCreatedAtDesc(c.getAuthor().getId())
                            .stream().findFirst().map(p -> p.getId()).orElse(null))
                    .map(e -> e.getTotalScore()).orElse(0);
            return CommentResponse.from(c, score);
        });
    }

    @Transactional
    public CommentResponse create(UUID userId, UUID portfolioId, CommentCreateRequest request) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));
        if (portfolio.getUser().getId().equals(userId)) {
            throw new BusinessException(GlobalErrorCode.FORBIDDEN);
        }
        User author = userService.findById(userId);
        Comment comment = Comment.builder()
                .portfolio(portfolio)
                .author(author)
                .type(request.type())
                .body(request.body())
                .build();
        commentRepository.save(comment);
        return CommentResponse.from(comment, 0);
    }

    @Transactional
    public void delete(UUID userId, UUID commentId) {
        Comment comment = commentRepository.findByIdAndAuthorId(commentId, userId)
                .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));
        commentRepository.delete(comment);
    }

    @Transactional
    public void report(UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));
        comment.incrementReport();
    }

    public Map<String, Long> getCommentCounts(UUID portfolioId) {
        return Map.of(
                "total", commentRepository.countByPortfolioIdAndType(portfolioId, null) +
                         commentRepository.countByPortfolioIdAndType(portfolioId, CommentType.PRAISE) +
                         commentRepository.countByPortfolioIdAndType(portfolioId, CommentType.SUGGEST) +
                         commentRepository.countByPortfolioIdAndType(portfolioId, CommentType.QUESTION),
                "praise", commentRepository.countByPortfolioIdAndType(portfolioId, CommentType.PRAISE),
                "suggest", commentRepository.countByPortfolioIdAndType(portfolioId, CommentType.SUGGEST),
                "question", commentRepository.countByPortfolioIdAndType(portfolioId, CommentType.QUESTION)
        );
    }
}
