package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Todo> findAllByConditionsOrderByModifiedAtDesc(
        LocalDateTime startDate,
        LocalDateTime endDate,
        String weather,
        Pageable pageable
    ) {
        QTodo todo = QTodo.todo;

        List<Todo> results = queryFactory.selectFrom(todo)
            .leftJoin(todo.user).fetchJoin()
            .where(
                betweenCreatedAt(startDate, endDate),
                eqWeather(weather)
            )
            .orderBy(todo.modifiedAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory.select(todo.count())
            .from(todo)
            .where(
                betweenCreatedAt(startDate, endDate),
                eqWeather(weather)
            )
            .fetchOne();

        return new PageImpl<>(results, pageable, Optional.ofNullable(total).orElse(0L));
    }

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        QTodo todo = QTodo.todo;

        Todo result = queryFactory.selectFrom(todo)
            .leftJoin(todo.user).fetchJoin()
            .where(todo.id.eq(todoId))
            .fetchOne();

        return Optional.ofNullable(result);
    }

    private BooleanExpression betweenCreatedAt(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            return null;
        }
        return QTodo.todo.createdAt.between(startDate, endDate);
    }

    private BooleanExpression eqWeather(String weather) {
        if (weather == null) {
            return null;
        }
        return QTodo.todo.weather.eq(weather);
    }
}
