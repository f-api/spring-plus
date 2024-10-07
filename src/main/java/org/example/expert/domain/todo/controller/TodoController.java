package org.example.expert.domain.todo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.request.*;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponseDto;
import org.example.expert.domain.todo.service.TodoService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/todos")
    public ResponseEntity<TodoSaveResponse> saveTodo(
            @Valid @RequestBody TodoSaveRequest todoSaveRequest
    ) {
        return ResponseEntity.ok(todoService.saveTodo(todoSaveRequest));
    }

    @GetMapping("/todos")
    public ResponseEntity<Page<TodoResponse>> getTodos(
            @RequestBody TodoGetRequest requestDto
            ) {
        return ResponseEntity.ok(todoService.getTodos(requestDto));
    }

    @GetMapping("/todos/{todoId}")
    public ResponseEntity<TodoResponse> getTodo(@PathVariable long todoId) {
        return ResponseEntity.ok(todoService.getTodo(todoId));
    }

    @GetMapping("/todos/search/title")
    public ResponseEntity<Page<TodoSearchResponseDto>> searchByTitle (@Valid @RequestBody TodoSearchByTitleRequestDto requestDto) {
        return ResponseEntity.ok(todoService.searchByTitle(requestDto));
    }

    @GetMapping("/todos/search/managerNickname")
    public ResponseEntity<Page<TodoSearchResponseDto>> searchByManagerNickname(@Valid @RequestBody TodoSearchByManagerNicknameRequestDto requestDto) {
        return ResponseEntity.ok(todoService.searchByManagerNickname(requestDto));
    }

    @GetMapping("/todos/search/createdPeriod")
    public ResponseEntity<Page<TodoSearchResponseDto>> searchByCreatedPeriod(@Valid @RequestBody TodoSearchByCreatedPeriodDto requestDto) {
        return ResponseEntity.ok(todoService.searchByCreatedPeriod(requestDto));
    }


}
