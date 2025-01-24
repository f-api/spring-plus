package org.example.expert.domain.todo.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.request.TodoSearchRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.service.TodoService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TodoController {

	private final TodoService todoService;

	@PostMapping("/todos")
	public ResponseEntity<TodoSaveResponse> saveTodo(
		@Auth AuthUser authUser,
		@Valid @RequestBody TodoSaveRequest todoSaveRequest
	) {
		return ResponseEntity.ok(todoService.saveTodo(authUser, todoSaveRequest));
	}

	@GetMapping("/todos")
	public ResponseEntity<Page<TodoResponse>> getTodos(
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int size,
		// Lv1-5
		@RequestParam(required = false) String weather,
		@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
		@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
	) {
		// Lv1-5
		LocalDateTime startDateTime = getStartDateTime(startDate);
		LocalDateTime endDateTime = getEndDateTime(endDate);

		return ResponseEntity.ok(todoService.getTodos(page, size, weather, startDateTime, endDateTime));
	}

	@GetMapping("/todos/{todoId}")
	public ResponseEntity<TodoResponse> getTodo(@PathVariable long todoId) {
		return ResponseEntity.ok(todoService.getTodo(todoId));
	}

	// Lv3-10: 일정 검색 API
	@GetMapping("/todos/search")
	public ResponseEntity<List<TodoSearchResponse>> searchTodo(
		@ModelAttribute TodoSearchRequest todoSearchRequest,
		@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
		@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
	) {
		LocalDateTime startDateTime = getStartDateTime(startDate);
		LocalDateTime endDateTime = getEndDateTime(endDate);

		return ResponseEntity.ok(todoService.searchTodo(todoSearchRequest, startDateTime, endDateTime));
	}

	/* 날짜 변환 메서드 */
	private static LocalDateTime getStartDateTime(LocalDate startDate) {
		return Optional.ofNullable(startDate)
			.map(LocalDate::atStartOfDay)
			.orElse(null);
	}

	private static LocalDateTime getEndDateTime(LocalDate endDate) {
		return Optional.ofNullable(endDate)
			.map(date -> date.atTime(23, 59, 59))
			.orElse(null);
	}

}
