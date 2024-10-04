package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.example.expert.domain.todo.dto.response.QTodoSearchResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.example.expert.domain.comment.entity.QComment.comment;
import static org.example.expert.domain.manager.entity.QManager.manager;
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

    @Override
    public Page<TodoSearchResponse> search(Pageable pageable, String title, String managerName, LocalDateTime startDate, LocalDateTime endDate) {
        List<TodoSearchResponse> searchResultList = queryFactory
                .select(new QTodoSearchResponse(todo.title, manager.count().intValue(), comment.count().intValue()))
                .from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(todo.comments, comment)
                .where(titleLike(title), managerLike(managerName), createdAt(startDate, endDate))
                .groupBy(todo.id)
                .orderBy(todo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(Wildcard.count)
                .from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(todo.comments, comment)
                .where(titleLike(title), managerLike(managerName), createdAt(startDate, endDate))
                .groupBy(todo.id)
                .fetchOne();

        return new PageImpl<>(searchResultList, pageable, totalCount);
    }

    private BooleanExpression titleLike(String title) {
        if (Strings.isBlank(title)) return null;

        return todo.title.contains(title);
    }

    private BooleanExpression createdAt(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null && endDate == null) return null;

        if (startDate != null && endDate != null) {
            return todo.createdAt.between(startDate, endDate);
        }

        if (startDate != null) {
            return todo.createdAt.after(startDate);
        } else {
            return todo.createdAt.before(endDate);
        }
    }

    private BooleanExpression managerLike(String nickname) {
        if (Strings.isBlank(nickname)) return null;

        return manager.user.nickname.contains(nickname);
    }
}
