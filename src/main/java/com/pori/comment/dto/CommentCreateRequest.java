package com.pori.comment.dto;

import com.pori.comment.domain.CommentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "댓글 작성 요청")
public record CommentCreateRequest(
        @Schema(description = "댓글 유형. PRAISE=칭찬, SUGGEST=제안, QUESTION=질문", example = "PRAISE")
        @NotNull CommentType type,

        @Schema(description = "댓글 본문", example = "성과 지표가 명확해서 프로젝트 임팩트가 잘 보입니다.")
        @NotBlank String body
) {}
