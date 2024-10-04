package org.example.expert.domain.todo.service;

import jakarta.transaction.Transactional;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
class TodoServiceTest {
    @Autowired
    private TodoService todoService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("todo 저장에 성공한다.")
    void saveTodo_success() {
        // given
        User user = new User("email", "password", "nickname", UserRole.ROLE_USER);
        User savedUser = userRepository.save(user);

        AuthUser authUser = new AuthUser(savedUser.getId(), savedUser.getEmail(), savedUser.getNickname(), savedUser.getUserRole());

        TodoSaveRequest todoSaveRequest = new TodoSaveRequest("title", "contents");

        // when
        TodoSaveResponse todoSaveResponse = todoService.saveTodo(authUser, todoSaveRequest);

        // then
        assertEquals(todoSaveRequest.getContents(), todoSaveResponse.getContents());
    }

    @Test
    @DisplayName("todo 조회에 성공한다.")
    void getTodo_success() {
        // given
        User user = new User("email", "password", "nickname", UserRole.ROLE_USER);
        User savedUser = userRepository.save(user);

        AuthUser authUser = new AuthUser(savedUser.getId(), savedUser.getEmail(), savedUser.getNickname(), savedUser.getUserRole());

        TodoSaveRequest todoSaveRequest = new TodoSaveRequest("title", "contents");
        TodoSaveResponse todoSaveResponse = todoService.saveTodo(authUser, todoSaveRequest);

        // when
        TodoResponse todoResponse = todoService.getTodo(todoSaveResponse.getId());

        // then
        assertEquals("email", todoResponse.getUser().getEmail());
    }

    @Test
    @DisplayName("todo 검색에 성공한다.")
    void searchTodo_success() {
        // given
        User user = new User("email", "password", "nickname", UserRole.ROLE_USER);
        User savedUser = userRepository.save(user);

        AuthUser authUser = new AuthUser(savedUser.getId(), savedUser.getEmail(), savedUser.getNickname(), savedUser.getUserRole());

        TodoSaveRequest todoSaveRequest1 = new TodoSaveRequest("title", "contents");
        todoService.saveTodo(authUser, todoSaveRequest1);

        TodoSaveRequest todoSaveRequest2 = new TodoSaveRequest("11111", "contents");
        todoService.saveTodo(authUser, todoSaveRequest2);

        // when
        Page<TodoSearchResponse> searchList = todoService.searchTodos(1, 10, "t", "nick", null, null);

        // then
        assertEquals(1, searchList.getContent().size());
    }
}