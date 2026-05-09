package com.pori.portfolio.repository;

import com.pori.portfolio.domain.JobCategory;
import com.pori.portfolio.domain.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    @Query("""
            SELECT p FROM Portfolio p
            WHERE p.isPublic = true
              AND (:jobCategory IS NULL OR p.jobCategory = :jobCategory)
              AND p.poriScore >= :minScore
            ORDER BY p.createdAt DESC
            """)
    List<Portfolio> findPublicPortfolios(
            @Param("jobCategory") JobCategory jobCategory,
            @Param("minScore") int minScore
    );
}
