package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TodoQueryRepository {
    Optional<Todo> getTodo(long id);

    Page<TodoSearchResponse> search(Pageable pageable, String title, String managerName, LocalDateTime startDate, LocalDateTime endDate);
}
