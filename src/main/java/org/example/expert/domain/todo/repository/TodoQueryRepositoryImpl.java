package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.todo.dto.response.TodoProjectionDto;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.example.expert.domain.comment.entity.QComment.comment;
import static org.example.expert.domain.manager.entity.QManager.manager;
import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TodoQueryRepositoryImpl implements TodoQueryRepository {

    private final JPAQueryFactory queryFactory;
    @Override
    public Todo findByIdWithUserByDsl(Long todoId) {
        return queryFactory
                .select(todo)
                .from(todo)
                .where(
                        todoIdEq(todoId)
                ).fetchOne();
    }
    @Override
    public Page<TodoProjectionDto> searchTodos(Pageable pageable, String keyword, LocalDate createdAt, String nickname) {
        List<TodoProjectionDto> todos = queryFactory
                .select(Projections.constructor(TodoProjectionDto.class,
                        todo.title,
                        manager.countDistinct().as("managerCount"),
                        comment.countDistinct().as("commentCount")
                ))
                .from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(manager.user, user)
                .leftJoin(todo.comments, comment)
                .where(
                        allConditions(keyword, createdAt, nickname)
                )
                .groupBy(todo.id)
                .orderBy(todo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(Wildcard.count)
                .from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(manager.user, user)
                .where(
                        allConditions(keyword, createdAt, nickname)
                )
                .fetchOne();

        return new PageImpl<>(todos, pageable, total);
    }

    private BooleanExpression allConditions(String keyword, LocalDate createdAt, String nickname) {
        BooleanExpression condition = todo.isNotNull();

        if (keyword != null && !keyword.isEmpty()) {
            condition = condition.and(titleContains(keyword));
        }

        if (createdAt != null) {
            condition = condition.and(createdAtEq(createdAt));
        }

        if (nickname != null && !nickname.isEmpty()) {
            condition = condition.and(nicknameContains(nickname));
        }

        return condition;
    }

    private BooleanExpression titleContains(String keyword) {
        return todo.title.containsIgnoreCase(keyword);
    }

    private BooleanExpression createdAtEq(LocalDate createdAt) {
        LocalDateTime startOfDay = createdAt.atStartOfDay();
        LocalDateTime endOfDay = createdAt.atTime(23, 59, 59);

        return todo.createdAt.between(startOfDay, endOfDay);
    }

    private BooleanExpression nicknameContains(String nickname) {
        return manager.user.nickname.containsIgnoreCase(nickname);
    }

    private BooleanExpression todoIdEq(Long todoId) {
        return todoId != null ? todo.id.eq(todoId) : null;
    }

}


