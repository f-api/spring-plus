package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.response.TodoSearchResponseDto;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Date;
import java.util.Optional;

public interface TodoRepositoryCustom {
    Optional<Todo> findByIdWithUser(Long todoId);
    Page<TodoSearchResponseDto> searchByTitle(Pageable pageable, String title);
    Page<TodoSearchResponseDto> searchByManagerNickname(Pageable pageable, String nickname);
    Page<TodoSearchResponseDto> searchByCreatedDate(Pageable pageable, Date firstDate, Date lastDate);
}
