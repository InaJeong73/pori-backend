package com.pori.user.dto;

import com.pori.user.domain.JobCategory;
import com.pori.user.domain.User;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String handle,
        String displayName,
        JobCategory jobCategory,
        Short year,
        String schoolGroup,
        String intro,
        String avatarUrl,
        short privacyLevel
) {
    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getEmail(), u.getHandle(), u.getDisplayName(),
                u.getJobCategory(), u.getYear(), u.getSchoolGroup(),
                u.getIntro(), u.getAvatarUrl(), u.getPrivacyLevel());
    }
}
