package com.pori.user.dto;

import com.pori.user.domain.JobCategory;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Size(min = 3, max = 20) String handle,
        String displayName,
        JobCategory jobCategory,
        Short year,
        String schoolGroup,
        String schoolName,
        String intro,
        short privacyLevel
) {}
