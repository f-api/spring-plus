package org.example.expert.domain.common.dto;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@Getter
public class AuthUser {

    private final Long id;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;
    private final String nickname;

    public AuthUser(Long userId, String email, UserRole role, String nickname) {
        this.id = userId;
        this.email = email;
        if(role != null) {
            this.authorities = List.of(new SimpleGrantedAuthority(role.name()));
        }else {
            this.authorities = List.of();
            log.error("user is null : {}", email);
        }
        this.nickname = nickname;
    }
}
