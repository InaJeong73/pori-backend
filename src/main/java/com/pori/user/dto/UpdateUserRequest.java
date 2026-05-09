package com.pori.user.dto;

import com.pori.user.domain.JobCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "프로필 수정 요청. 변경할 필드만 포함하면 됩니다.")
public record UpdateUserRequest(
        @Schema(description = "핸들 (영문·숫자·언더스코어, 3~20자)", example = "alice_dev")
        @Size(min = 3, max = 20) String handle,

        @Schema(description = "표시 이름 (실명 또는 닉네임)", example = "Alice Kim")
        String displayName,

        @Schema(description = "직군. FRONTEND / BACKEND / FULLSTACK / MOBILE / DATA_AI / OTHER", example = "FRONTEND")
        JobCategory jobCategory,

        @Schema(description = "경력 연차 (숫자)", example = "3")
        Short year,

        @Schema(description = "학력 그룹 (예: 대졸, 대학원, 비전공 등)", example = "대졸")
        String schoolGroup,

        @Schema(description = "학교 이름", example = "아주대학교")
        String schoolName,

        @Schema(description = "한 줄 소개", example = "사용자 경험을 중시하는 프론트엔드 개발자입니다.")
        String intro,

        @Schema(description = "프로필 공개 범위. 1=전체공개, 2=링크공개, 3=비공개", example = "1")
        short privacyLevel
) {}
