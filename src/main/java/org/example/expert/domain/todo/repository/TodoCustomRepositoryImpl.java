package org.example.expert.domain.todo.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TodoCustomRepositoryImpl implements TodoCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * @Query("SELECT t FROM Todo t " +
     *             "LEFT JOIN t.user " +
     *             "WHERE t.id = :todoId")
     */

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        // 엔티티를 기반으로 생성된 QueryDSL 클래스
        QTodo qtodo = QTodo.todo;
        QUser quser = QUser.user;

        Todo todo = jpaQueryFactory
            .select(qtodo)
            .from(qtodo)
            .leftJoin(qtodo.user, quser).fetchJoin()
            .where(qtodo.id.eq(todoId))
            .fetchOne(); // 단일 결과 조회 (결과가 없으면 Null 반환)

        return Optional.ofNullable(todo);
    }

}
