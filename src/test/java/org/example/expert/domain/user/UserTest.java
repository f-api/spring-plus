package org.example.expert.domain.user;

import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자 데이터 100만건 저장에 성공한다.")
    public void insertUser_success() {
        for (int i = 0; i < 1_000_000; i++) {
            userRepository.save(new User("email" + i, "password", "nickname" + i, UserRole.ROLE_USER));
        }
    }

}