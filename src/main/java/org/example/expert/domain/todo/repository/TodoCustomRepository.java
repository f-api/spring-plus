package org.example.expert.domain.todo.repository;

import java.util.Optional;
import org.example.expert.domain.todo.entity.Todo;

public interface TodoCustomRepository {

    Optional<Todo> findByIdWithUser(Long todoId);

}
