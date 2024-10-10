package org.example.expert.domain.todo.dto.response;

import lombok.Getter;

@Getter
public class TodoSearchResponse {

    private String title;
    private int assigneeCount;
    private int commentCount;

    public TodoSearchResponse(String title, int assigneeCount, int commentCount) {
        this.title = title;
        this.assigneeCount = assigneeCount;
        this.commentCount = commentCount;
    }
}
