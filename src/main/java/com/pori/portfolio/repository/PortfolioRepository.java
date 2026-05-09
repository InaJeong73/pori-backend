package com.pori.portfolio.repository;

import com.pori.portfolio.domain.Portfolio;
import com.pori.portfolio.domain.PortfolioStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PortfolioRepository extends JpaRepository<Portfolio, UUID> {
    List<Portfolio> findByUserIdOrderByCreatedAtDesc(UUID userId);

    @Query("SELECT COUNT(p) FROM Portfolio p WHERE p.user.id = :userId AND p.status = 'PUBLISHED'")
    long countPublishedByUserId(@Param("userId") UUID userId);

    @Query("SELECT MAX(e.totalScore) FROM Evaluation e WHERE e.portfolio.user.id = :userId AND e.portfolio.status = 'PUBLISHED'")
    Optional<Integer> findMaxScoreByUserId(@Param("userId") UUID userId);

    Optional<Portfolio> findByIdAndUserId(UUID id, UUID userId);
}
