package org.example.expert.domain.todo.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.expert.domain.todo.dto.request.TodoSearchRequest;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
public interface TodoQueryRepository {

	Optional<Todo> findByIdWithUser(Long todoId);

	List<TodoSearchResponse> searchTodos(TodoSearchRequest todoSearchRequest, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
