package org.example.expert.domain.manager.service;

import jakarta.transaction.Transactional;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.log.repository.LogRepository;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.service.TodoService;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class ManagerServiceTest {
    @Autowired
    private LogRepository logRepository;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private TodoService todoService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("매니저 등록에는 실패하지만 로그 등록에는 성공한다.")
    public void saveManager_failure() {
        // given
        User user = new User("email1", "password", "nickname", UserRole.ROLE_USER);
        User savedUser = userRepository.save(user);

        AuthUser authUser = new AuthUser(savedUser.getId(), savedUser.getEmail(), savedUser.getNickname(), savedUser.getUserRole());

        TodoSaveRequest todoSaveRequest = new TodoSaveRequest("title", "contents");
        TodoSaveResponse todoSaveResponse = todoService.saveTodo(authUser, todoSaveRequest);

        ManagerSaveRequest managerSaveRequest = new ManagerSaveRequest(111L);

        // when
        Throwable t = assertThrows(InvalidRequestException.class, () -> managerService.saveManager(authUser, todoSaveResponse.getId(), managerSaveRequest));

        // then
        assertTrue(logRepository.existsByRequestUserId(savedUser.getId()));
        assertEquals("등록하려고 하는 담당자 유저가 존재하지 않습니다.", t.getMessage());
    }

    @Test
    @DisplayName("매니저 등록, 로그 등록 모두 성공한다.")
    public void saveManager_success() {
        // given
        User user = new User("email1", "password", "nickname", UserRole.ROLE_USER);
        User savedUser = userRepository.save(user);

        User manager = new User("email2", "password", "nickname", UserRole.ROLE_USER);
        User savedManager = userRepository.save(manager);

        AuthUser authUser = new AuthUser(savedUser.getId(), savedUser.getEmail(), savedUser.getNickname(), savedUser.getUserRole());

        TodoSaveRequest todoSaveRequest = new TodoSaveRequest("title", "contents");
        TodoSaveResponse todoSaveResponse = todoService.saveTodo(authUser, todoSaveRequest);

        ManagerSaveRequest managerSaveRequest = new ManagerSaveRequest(savedManager.getId());

        // when
        managerService.saveManager(authUser, todoSaveResponse.getId(), managerSaveRequest);

        // then
        assertTrue(logRepository.existsByRequestUserId(savedUser.getId()));
        assertTrue(managerService.getManagers(todoSaveResponse.getId()).size() > 1);
    }

}