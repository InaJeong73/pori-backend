package com.pori.portfolio.repository;

import com.pori.portfolio.domain.AnonymousCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AnonymousCardRepository extends JpaRepository<AnonymousCard, UUID> {

    Optional<AnonymousCard> findByPortfolioId(UUID portfolioId);

    void deleteByPortfolioId(UUID portfolioId);

    @Query("""
        SELECT ac FROM AnonymousCard ac
        WHERE (:jobCategory IS NULL OR ac.jobCategory = :jobCategory)
        AND (:yearLevel IS NULL OR ac.yearLevel = :yearLevel)
        AND ac.totalScore >= :scoreMin
        AND ac.totalScore <= :scoreMax
        ORDER BY ac.totalScore DESC, ac.createdAt DESC
        """)
    Page<AnonymousCard> findFeed(@Param("jobCategory") String jobCategory,
                                  @Param("yearLevel") String yearLevel,
                                  @Param("scoreMin") int scoreMin,
                                  @Param("scoreMax") int scoreMax,
                                  Pageable pageable);
}
