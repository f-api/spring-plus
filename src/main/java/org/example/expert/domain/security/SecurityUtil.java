package org.example.expert.domain.security;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.example.expert.config.CustomUserDetailsService;
import org.example.expert.config.JwtUtil;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtil {
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public Authentication getAuthentication(String token) {
        Claims claims = jwtUtil.extractClaims(token);
        String email = claims.get("email", String.class);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails,"",userDetails.getAuthorities());
    }

    public User getCurrentUser() {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(currentUserEmail).orElseThrow(
                ()-> new InvalidRequestException("현재 로그인한 이메일로 사용자를 찾을 수 없습니다.")
        );
    }
}
