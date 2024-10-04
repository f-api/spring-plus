package org.example.expert.domain.todo.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class TodoQueryRepositoryImpl implements TodoQueryRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Todo> getTodo(long id) {
        return Optional.ofNullable(
                queryFactory.selectFrom(todo)
                        .from(todo)
                        .leftJoin(todo.user, user)
                        .fetchJoin()
                        .where(todo.id.eq(id))
                        .fetchOne()
        );
    }
}
