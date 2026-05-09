package com.pori.comment.dto;

import com.pori.comment.domain.Comment;
import com.pori.comment.domain.CommentType;

import java.time.Instant;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        String authorHandle,
        int authorScore,
        boolean isElite,
        CommentType type,
        String body,
        Instant createdAt
) {
    public static CommentResponse from(Comment c, int authorScore) {
        return new CommentResponse(c.getId(), c.getAuthor().getHandle(),
                authorScore, authorScore >= 90,
                c.getType(), c.getBody(), c.getCreatedAt());
    }
}
