package org.example.expert.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.expert.domain.user.entity.User;

@Getter
@AllArgsConstructor
public class UserResponse {

    private final Long id;
    private final String email;

    public static UserResponse toDto(User user) {
        return new UserResponse(user.getId(), user.getEmail());
    }
}
