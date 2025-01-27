package org.example.expert.domain.todo.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TodoRepositoryCustom {

    Page<Todo> findAllByConditionsOrderByModifiedAtDesc(
        LocalDateTime startDate,
        LocalDateTime endDate,
        String weather,
        Pageable pageable
    );

    Optional<Todo> findByIdWithUser(Long todoId);
}
