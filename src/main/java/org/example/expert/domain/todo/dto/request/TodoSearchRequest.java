package org.example.expert.domain.todo.dto.request;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TodoSearchRequest {

    private String weather;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
