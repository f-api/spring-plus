package org.example.expert.domain.user.dto.response;

import lombok.Getter;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;

@Getter
public class UserProfileResponseDto {
    private final Long userId;
    private final String email;
    private final String nickname;
    private final UserRole userRole;
    private final String profilePicKeyName;
    private final String profilePicUrl;

    public UserProfileResponseDto (User user, String profilePicUrl) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.userRole = user.getUserRole();
        this.profilePicKeyName = user.getProfilePicKeyName();
        this.profilePicUrl = profilePicUrl;
    }

}
