package com.pori.global.security;

import com.pori.user.domain.type.Role;

public record AuthPrincipal(
        Long userId,
        Role role
) {
}
