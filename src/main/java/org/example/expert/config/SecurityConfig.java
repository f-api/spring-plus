package org.example.expert.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true) // 특정메소드에만 권한 검수
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable) //csrf() 더 이상 사용 x
            .httpBasic(AbstractHttpConfigurer::disable) // BasicAuthenticationFilter 비활성화
            .formLogin(AbstractHttpConfigurer::disable) // formLogin 비활성화
            .addFilterBefore(jwtFilter, SecurityContextHolderAwareRequestFilter.class)
            .authorizeHttpRequests
                (auth -> auth
                .requestMatchers("/auth/**").permitAll()
                    /**
                     * UserRole이 "ADMIN" 인 경우에만 접근 허용
                     * == @Secured("ROLE_ADMIN")
                     * == @PreAuthorize("hasAnyRole('ADMIN')")
                     */
//                .requestMatchers("/admin/**").hasRole("ADMIN")

                .anyRequest().authenticated()
                )
            .build();

    }

}
