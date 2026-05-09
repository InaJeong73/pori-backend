package com.pori.global.security;

import java.util.UUID;

public record UserPrincipal(UUID userId, String handle) {
}
