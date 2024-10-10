package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.response.TodoProjectionDto;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface TodoQueryRepository {
    Todo findByIdWithUserByDsl(Long todoId);


    Page<TodoProjectionDto> searchTodos(Pageable pageable, String keyword, LocalDate createdAt, String nickname);

    Page<TodoResponse> search(Pageable pageable);

    TodoProjectionDto findByIdFromProjection(long todoId);


}
