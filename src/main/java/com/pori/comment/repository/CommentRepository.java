package com.pori.comment.repository;

import com.pori.comment.domain.Comment;
import com.pori.comment.domain.CommentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    @Query("""
        SELECT c FROM Comment c
        WHERE c.portfolio.id = :portfolioId
        AND c.hidden = false
        AND (:type IS NULL OR c.type = :type)
        ORDER BY c.createdAt DESC
        """)
    Page<Comment> findByPortfolioId(@Param("portfolioId") UUID portfolioId,
                                     @Param("type") CommentType type,
                                     Pageable pageable);

    List<Comment> findTop10ByPortfolioIdAndHiddenFalseOrderByCreatedAtDesc(UUID portfolioId);

    @Query("""
        SELECT COUNT(c) FROM Comment c
        WHERE c.portfolio.id = :portfolioId AND c.hidden = false AND c.type = :type
        """)
    long countByPortfolioIdAndType(@Param("portfolioId") UUID portfolioId, @Param("type") CommentType type);

    Optional<Comment> findByIdAndAuthorId(UUID id, UUID authorId);
}
