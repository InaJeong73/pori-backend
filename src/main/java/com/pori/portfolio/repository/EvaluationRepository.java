package com.pori.portfolio.repository;

import com.pori.portfolio.domain.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EvaluationRepository extends JpaRepository<Evaluation, UUID> {
    Optional<Evaluation> findByPortfolioId(UUID portfolioId);
}
