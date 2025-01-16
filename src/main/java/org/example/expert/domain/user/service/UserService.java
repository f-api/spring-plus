package org.example.expert.domain.user.service;

import static org.example.expert.domain.common.exception.ExceptionType.PASSWORD_NOT_MATCH;
import static org.example.expert.domain.common.exception.ExceptionType.PASSWORD_SAME;
import static org.example.expert.domain.common.exception.ExceptionType.USER_NOT_FOUND;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.exception.CustomException;
import org.example.expert.domain.common.util.PasswordEncoder;
import org.example.expert.domain.user.dto.request.UserPasswordChangeRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse getUser(long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        return UserResponse.toDto(user);
    }

    @Transactional
    public void changePassword(long userId, UserPasswordChangeRequest userPasswordChangeRequest) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        if (passwordEncoder.matches(userPasswordChangeRequest.getNewPassword(),
            user.getPassword())) {
            throw new CustomException(PASSWORD_SAME);
        }

        if (!passwordEncoder.matches(userPasswordChangeRequest.getOldPassword(),
            user.getPassword())) {
            throw new CustomException(PASSWORD_NOT_MATCH);
        }

        user.changePassword(passwordEncoder.encode(userPasswordChangeRequest.getNewPassword()));
    }
}
