package org.example.expert.domain.todo.repository.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.response.QTodoSearchResponseDto;
import org.example.expert.domain.todo.dto.response.TodoSearchResponseDto;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.example.expert.domain.comment.entity.QComment.comment;
import static org.example.expert.domain.manager.entity.QManager.manager;
import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class TodoRepositoryCustomImpl implements TodoRepositoryCustom {
    private final JPAQueryFactory q;

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        return Optional.ofNullable(q
                .select(todo)
                .from(todo)
                .leftJoin(todo.user, user).fetchJoin()
                .where(todoIdEq(todoId))
                .fetchOne());
    }

    private BooleanExpression todoIdEq(Long todoId) {
        return todoId != null ? todo.id.eq(todoId) : null;
    }

    /*
    검색 기능
    1.제목
    2.생성일 범위
    3.담당자 닉네임
     */
    @Override
    public Page<TodoSearchResponseDto> searchByTitle(Pageable pageable, String title) {
        List<TodoSearchResponseDto> result = q
                .select(
                        new QTodoSearchResponseDto(
                                todo.title,
                                todo.managers.size().longValue(),
                                todo.comments.size().longValue()
                        )
                )
                .from(todo)
                .leftJoin(todo.comments, comment)
                .leftJoin(todo.managers, manager)
                .where(todo.title.contains(title))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = Optional.ofNullable(
                q
                .select(Wildcard.count)
                .from(todo)
                .where(todo.title.contains(title))
                .fetchOne())
                .orElse(0L);

        return new PageImpl<>(result, pageable, total);
    }

    @Override
    public Page<TodoSearchResponseDto> searchByManagerNickname(Pageable pageable, String nickname) {
        List<TodoSearchResponseDto> result = q
                .selectDistinct(
                        new QTodoSearchResponseDto(
                                todo.title,
                                todo.managers.size().longValue(),
                                todo.comments.size().longValue()
                        )
                )
                .from(manager)
                .leftJoin(manager.user, user)
                .leftJoin(manager.todo, todo)
                .where(user.nickname.contains(nickname))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = Optional.ofNullable(
                        q
                                .selectDistinct(todo.id.countDistinct())
                                .from(manager)
                                .leftJoin(manager.todo,todo)
                                .leftJoin(manager.user,user)
                                .where(user.nickname.contains(nickname))
                                .fetchOne())
                .orElse(0L);

        return new PageImpl<>(result, pageable, total);
    }

    @Override
    public Page<TodoSearchResponseDto> searchByCreatedDate(Pageable pageable, Date firstDate, Date lastDate) {
        List<TodoSearchResponseDto> result = q
                .select(
                        new QTodoSearchResponseDto(
                                todo.title,
                                todo.managers.size().longValue(),
                                todo.comments.size().longValue()
                        )
                )
                .from(todo)
                .where(
                        Expressions.dateTemplate(Date.class, "DATE({0})",todo.createdAt)
                        .between(firstDate,lastDate))
                .orderBy(todo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = Optional.ofNullable(
                q
                        .select(Wildcard.count)
                        .from(todo)
                        .where(
                                Expressions.dateTemplate(Date.class, "DATE({0})",todo.createdAt)
                                        .between(firstDate,lastDate)
                        )
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(result, pageable, total);
    }

}
