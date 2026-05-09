package com.pori.comment.dto;

import com.pori.comment.domain.CommentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentCreateRequest(
        @NotNull CommentType type,
        @NotBlank String body
) {}
