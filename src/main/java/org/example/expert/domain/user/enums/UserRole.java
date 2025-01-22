package org.example.expert.domain.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.exception.InvalidRequestException;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    private final String role; //스프링 시큐리티가 제공하는 User에 role 전달 (getRole)

    public static UserRole of(String role) {
        return Arrays.stream(UserRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new InvalidRequestException("유효하지 않은 UserRole"));
    }
}
