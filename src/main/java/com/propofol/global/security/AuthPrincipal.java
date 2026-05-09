package com.propofol.global.security;

import com.propofol.user.domain.type.Role;

public record AuthPrincipal(
        Long userId,
        Role role
) {
}
