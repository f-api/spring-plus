package org.example.expert.domain.user.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.user.entity.User;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserListResponse {
    private List<UserResponse> userList;

    private UserListResponse(List<UserResponse> userList) {
        this.userList = userList;
    }

    public static UserListResponse from(List<User> userList) {
        List<UserResponse> userResponseList = userList
                .stream()
                .map(user -> new UserResponse(user.getId(), user.getEmail()))
                .toList();

        return new UserListResponse(userResponseList);
    }
}
