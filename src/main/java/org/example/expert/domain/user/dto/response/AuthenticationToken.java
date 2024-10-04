package org.example.expert.domain.user.dto.response;

import lombok.Getter;
import org.example.expert.domain.common.dto.AuthUser;
import org.springframework.security.authentication.AbstractAuthenticationToken;

@Getter
public class AuthenticationToken extends AbstractAuthenticationToken {
    private final AuthUser authUser;

    public AuthenticationToken(AuthUser authUser) {
        super(authUser.getAuthorities());
        this.authUser = authUser;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return authUser;
    }
}