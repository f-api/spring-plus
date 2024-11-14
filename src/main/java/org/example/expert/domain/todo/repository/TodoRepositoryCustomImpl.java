package org.example.expert.domain.todo.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TodoRepositoryCustomImpl implements TodoRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QTodo qTodo = QTodo.todo;

        Todo todo = queryFactory.selectFrom(qTodo)
                .leftJoin(qTodo.user).fetchJoin()
                .where(qTodo.id.eq(todoId))
                .fetchOne();

        return Optional.ofNullable(todo);
    }
}