package org.example.expert.domain.todo.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;

import java.util.Optional;

@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoRepositoryDSL {

    private final JPAQueryFactory jpaQueryFactory;

    QTodo todo = QTodo.todo;

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(todo)
                .leftJoin(todo.user).fetchJoin()
                .where(todo.id.eq(todoId))
                .fetchOne());
    }
}
