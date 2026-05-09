package com.pori.auth.service;

import com.pori.auth.dto.LoginRequest;
import com.pori.auth.dto.RegisterRequest;
import com.pori.auth.dto.TokenResponse;
import com.pori.global.exception.BusinessException;
import com.pori.global.exception.errorcode.GlobalErrorCode;
import com.pori.global.jwt.JwtProvider;
import com.pori.user.domain.User;
import com.pori.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    @Transactional
    public TokenResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(GlobalErrorCode.CONFLICT);
        }
        if (userRepository.existsByHandle(request.handle())) {
            throw new BusinessException(GlobalErrorCode.CONFLICT);
        }
        User user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .handle(request.handle())
                .build();
        userRepository.save(user);
        return issueTokens(user);
    }

    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(GlobalErrorCode.AUTHENTICATION_FAILED));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessException(GlobalErrorCode.AUTHENTICATION_FAILED);
        }
        return issueTokens(user);
    }

    public TokenResponse refresh(String refreshToken) {
        if (!jwtProvider.isValid(refreshToken)) {
            throw new BusinessException(GlobalErrorCode.AUTHENTICATION_FAILED);
        }
        Claims claims = jwtProvider.parse(refreshToken);
        UUID userId = UUID.fromString(claims.getSubject());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(GlobalErrorCode.AUTHENTICATION_FAILED));
        return issueTokens(user);
    }

    private TokenResponse issueTokens(User user) {
        return new TokenResponse(
                jwtProvider.createAccessToken(user.getId(), user.getHandle()),
                jwtProvider.createRefreshToken(user.getId(), user.getHandle())
        );
    }
}
