package org.example.expert.domain.todo.dto.response;

import java.time.LocalDateTime;
import org.example.expert.domain.todo.entity.Todo;

public class TodoSearchResponse{
    public record Dto(
        // 아이디, 생성일, 제목, 담당자 수, 댓글 개수
        long id,
        LocalDateTime createdAt,
        String title,
        int managerCount,
        int commentCount
    ) { // 일정 객체에 담당자 수, 댓글 개수 내용을 더해야 하므로 생성자를 하나 만듦
        public Dto(Todo todo, long managerCount, long commentCount) {
            this(
                todo.getId(),
                todo.getCreatedAt(),
                todo.getTitle(),
                (int) managerCount,
                (int) commentCount
            );
        }
    }
}
