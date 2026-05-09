package com.pori.user.dto;

import com.pori.user.domain.JobCategory;
import com.pori.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "사용자 프로필 응답")
public record UserResponse(
        @Schema(description = "사용자 고유 ID (UUID)") UUID id,
        @Schema(description = "이메일 주소", example = "alice@pori.dev") String email,
        @Schema(description = "고유 핸들", example = "alice_fe") String handle,
        @Schema(description = "표시 이름", example = "Alice Kim") String displayName,
        @Schema(description = "직군. FRONTEND / BACKEND / FULLSTACK / MOBILE / DATA_AI / OTHER", example = "FRONTEND") JobCategory jobCategory,
        @Schema(description = "경력 연차", example = "3") Short year,
        @Schema(description = "학력 그룹", example = "대졸") String schoolGroup,
        @Schema(description = "한 줄 소개", example = "사용자 경험을 중시하는 프론트엔드 개발자입니다.") String intro,
        @Schema(description = "아바타 이미지 URL (없으면 null)") String avatarUrl,
        @Schema(description = "프로필 공개 범위. 1=전체공개, 2=링크공개, 3=비공개", example = "1") short privacyLevel
) {
    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getEmail(), u.getHandle(), u.getDisplayName(),
                u.getJobCategory(), u.getYear(), u.getSchoolGroup(),
                u.getIntro(), u.getAvatarUrl(), u.getPrivacyLevel());
    }
}
