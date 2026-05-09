package com.pori.user.service;

import com.pori.global.exception.BusinessException;
import com.pori.global.exception.errorcode.GlobalErrorCode;
import com.pori.user.domain.User;
import com.pori.user.dto.UpdateUserRequest;
import com.pori.user.dto.UserResponse;
import com.pori.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse getMe(UUID userId) {
        return UserResponse.from(findById(userId));
    }

    @Transactional
    public UserResponse updateMe(UUID userId, UpdateUserRequest request) {
        User user = findById(userId);
        if (request.handle() != null && !request.handle().equals(user.getHandle())) {
            if (userRepository.existsByHandle(request.handle())) {
                throw new BusinessException(GlobalErrorCode.CONFLICT);
            }
        }
        user.updateProfile(request.handle(), request.displayName(), request.jobCategory(),
                request.year(), request.schoolGroup(), request.schoolName(),
                request.intro(), request.privacyLevel());
        return UserResponse.from(user);
    }

    @Transactional
    public void deleteMe(UUID userId) {
        User user = findById(userId);
        user.softDelete();
    }

    public boolean isHandleAvailable(String handle) {
        return !userRepository.existsByHandle(handle);
    }

    public User findById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));
    }

    public User findByHandle(String handle) {
        return userRepository.findByHandle(handle)
                .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));
    }
}
