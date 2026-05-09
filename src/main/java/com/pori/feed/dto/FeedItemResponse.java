package com.pori.feed.dto;

import com.pori.portfolio.domain.AnonymousCard;
import com.pori.user.domain.User;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record FeedItemResponse(
        UUID cardId,
        String handle,
        Map<String, Object> avatar,
        String summary,
        String roleSummary,
        List<String> techStack,
        List<String> metrics,
        String yearLevel,
        String schoolGroupLabel,
        ScoresDto scores,
        boolean isElite,
        Instant createdAt
) {
    public record ScoresDto(int total, List<Double> axes) {}

    public static FeedItemResponse from(AnonymousCard card, User user) {
        int total = card.getTotalScore();
        List<Double> axes = List.of(
                total / 20.0, total / 20.0, total / 20.0, total / 20.0, total / 20.0
        );
        Map<String, Object> avatar = user.getAvatarUrl() != null
                ? Map.of("type", "url", "url", user.getAvatarUrl())
                : Map.of("type", "gradient", "seed", Math.abs(user.getHandle().hashCode() % 10));
        return new FeedItemResponse(
                card.getId(), user.getHandle(), avatar,
                card.getSummary(), card.getRoleSummary(),
                card.getTechStack(), card.getMetrics(),
                card.getYearLevel(), card.getSchoolGroupLabel(),
                new ScoresDto(total, axes),
                total >= 90,
                card.getCreatedAt()
        );
    }
}
