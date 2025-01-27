package org.example.expert.domain.todo.dto.request;

public class TodoSearchRequest {
    public record Dto(
        // 제목, 생성일 범위, 담당자 닉네임
        String title,
        String startDate, // YYYY-MM-DD 형식
        String endDate, // YYYY-MM-DD 형식
        String nickname
    ) {}
}






