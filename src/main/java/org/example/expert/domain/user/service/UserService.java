package org.example.expert.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.security.SecurityUtil;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.ProfilePicResponseDto;
import org.example.expert.domain.user.dto.response.UserProfileResponseDto;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.example.expert.util.S3Util;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtil securityUtil;
    private final S3Util s3Util;

    public UserProfileResponseDto getUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidRequestException("User not found"));
        if(user.getProfilePicKeyName()!=null) {
            return new UserProfileResponseDto(user, s3Util.createPresignedUrl(user.getProfilePicKeyName()));
        } else {
            return new UserProfileResponseDto(user, null);
        }
    }

    @Transactional
    public void changePassword(UserChangePasswordRequest userChangePasswordRequest) {
        validateNewPassword(userChangePasswordRequest);

        User user = securityUtil.getCurrentUser();

        if (passwordEncoder.matches(userChangePasswordRequest.getNewPassword(), user.getPassword())) {
            throw new InvalidRequestException("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.");
        }

        if (!passwordEncoder.matches(userChangePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new InvalidRequestException("잘못된 비밀번호입니다.");
        }

        user.changePassword(passwordEncoder.encode(userChangePasswordRequest.getNewPassword()));
    }

    private static void validateNewPassword(UserChangePasswordRequest userChangePasswordRequest) {
        if (userChangePasswordRequest.getNewPassword().length() < 8 ||
                !userChangePasswordRequest.getNewPassword().matches(".*\\d.*") ||
                !userChangePasswordRequest.getNewPassword().matches(".*[A-Z].*")) {
            throw new InvalidRequestException("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.");
        }
    }

    @Transactional
    public ProfilePicResponseDto uploadProfilePicture(MultipartFile multipartFile) throws IOException {
        User user = securityUtil.getCurrentUser();
        String keyName = s3Util.upload(multipartFile);
        user.setProfilePicKeyName(keyName);
        userRepository.save(user);
        String presignedUrl = s3Util.createPresignedUrl(keyName);
        return new ProfilePicResponseDto(keyName,presignedUrl);
    }
}
