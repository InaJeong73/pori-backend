package com.propofol.auth.service;

import com.propofol.auth.dto.request.LoginRequest;
import com.propofol.auth.dto.request.RegisterRequest;
import com.propofol.auth.dto.response.LoginResponse;
import com.propofol.auth.dto.response.ReissueResponse;
import com.propofol.global.exception.BusinessException;
import com.propofol.global.exception.errorcode.GlobalErrorCode;
import com.propofol.global.security.AuthPrincipal;
import com.propofol.global.security.jwt.JwtTokenProvider;
import com.propofol.user.domain.User;
import com.propofol.user.dto.response.UserResponse;
import com.propofol.user.exception.UserErrorCode;
import com.propofol.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(UserErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User user = User.create(request.email(), passwordEncoder.encode(request.password()), request.nickname());
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(GlobalErrorCode.AUTHENTICATION_FAILED));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(GlobalErrorCode.AUTHENTICATION_FAILED);
        }

        return new LoginResponse(
                jwtTokenProvider.createAccessToken(user),
                jwtTokenProvider.createRefreshToken(user),
                UserResponse.from(user));
    }

    public ReissueResponse reissue(String refreshToken) {
        String token = resolveBearerToken(refreshToken);
        if (!jwtTokenProvider.validate(token)) {
            throw new BusinessException(GlobalErrorCode.AUTHENTICATION_FAILED);
        }

        AuthPrincipal principal = jwtTokenProvider.getPrincipal(token);
        User user = userRepository.findById(principal.userId())
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
        return new ReissueResponse(jwtTokenProvider.createAccessToken(user));
    }

    private String resolveBearerToken(String headerValue) {
        if (headerValue != null && headerValue.startsWith("Bearer ")) {
            return headerValue.substring(7);
        }
        throw new BusinessException(GlobalErrorCode.AUTHENTICATION_FAILED);
    }
}
