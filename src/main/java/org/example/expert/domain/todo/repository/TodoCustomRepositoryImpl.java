package org.example.expert.domain.todo.repository;

import static com.querydsl.jpa.JPAExpressions.select;
import static org.example.expert.domain.comment.entity.QComment.comment;
import static org.example.expert.domain.manager.entity.QManager.manager;
import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.request.TodoSearchRequest.Dto;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TodoCustomRepositoryImpl implements TodoCustomRepository{

    private final JPAQueryFactory queryFactory;

    /** 일정 단건조회 JPQL을 QueryDSL로 작성하기
     * Query("SELECT t FROM Todo t " +
     *             "LEFT JOIN t.user " +
     *             "WHERE t.id = :todoId")
     */

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        // 엔티티를 기반으로 생성된 QueryDSL 클래스
        QTodo qtodo = QTodo.todo;
        QUser quser = QUser.user;

        // qtodo -> import static org.example.expert.domain.todo.entity.QTodo.todo;
        Todo todo = queryFactory
            .select(qtodo)
            .from(qtodo)
            .leftJoin(qtodo.user, quser).fetchJoin()
            .where(qtodo.id.eq(todoId))
            .fetchOne(); // 단일 결과 조회 (결과가 없으면 Null 반환)

        return Optional.ofNullable(todo);
    }

    /**
     * QueryDSL을 사용하여 검색 기능 만들기
     *
     * 검색 조건
     * - 일정의 제목 (부분적으로 일치해도 검색 가능)
     * - 일정의 생성일 범위 (일정을 생성일 최신순으로 정렬)
     * - 담당자의 닉네임 (부분적으로 일치해도 검색 가능)
     *
     * 검색 결과 반환
     * - 일정에 대한 모든 정보가 아닌, 제목만 넣어주기
     * - 해당 일정의 담당자 수
     * - 해당 일정의 총 댓글 개수
     *
     * 페이징 처리까지
     */
    @Override
    public Page<TodoSearchResponse.Dto> search(Dto requestDto, Pageable pageable) {
        List<TodoSearchResponse.Dto> todos =
            queryFactory
                .select(
                    Projections.constructor(
                        TodoSearchResponse.Dto.class,
                        todo,
                        // 담당자 수
                        select(Wildcard.count).from(manager).where(todo.id.eq(manager.todo.id)),
                        // 댓글 개수
                        select(Wildcard.count).from(comment).where(todo.id.eq(comment.todo.id))
                    )
                )
                .distinct()
                .from(todo)
                // 닉네임 검색 외에는 담당자가 없는 일정도 결과에 나와야함. -> leftJoin
                .leftJoin(todo.managers, manager)
                .leftJoin(manager.user, user)
                // 검색 조건: 제목, 생성일, 담당자 닉네임
                .where(
                    titleContains(requestDto.title()),
                    createdDateBetween(requestDto.startDate(), requestDto.endDate()),
                    nicknameContains(requestDto.nickname())
                )
                .orderBy(todo.createdAt.desc()) // 생성일 최신순으로 정렬
                .limit(pageable.getPageSize()) // 1페이지당 몇 개까지 받아올건지
                .offset(pageable.getOffset()) // 몇 번째 페이지에서부터 갖고올건지
                .fetch();

            /**
             * 전체 데이터의 개수 조회 -> 카운트 쿼리
             */
            Long totalCount = Optional.ofNullable(queryFactory
                    .select(Wildcard.count) // Wildcard : product.count() product의 개수 반환
                    .from(todo)
                    .where(
                        titleContains(requestDto.title()),
                        createdDateBetween(requestDto.startDate(), requestDto.endDate()),
                        nicknameContains(requestDto.nickname())
                    )
                    .fetchOne()) // 하나의 데이터만 조회할 때는 fetchOne()을 사용
                .orElse(0L); // 500 error 예외처리

            return new PageImpl<>(todos, pageable, totalCount);
    }

    /**
     * 조건 조회 -> 동적 쿼리 -> BooleanExpression
     */
    // 제목으로 일정 검색
    private BooleanExpression titleContains(String title) {
        if(title == null) {
            return null;
        }
        return todo.title.contains(title);
    }
    // 생성일 범위로 일정 검색
    private BooleanExpression createdDateBetween(String startDate, String endDate) {
        if(startDate == null && endDate == null) {
            return null;
        }
        LocalDateTime start =
            startDate != null
                ? LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay()
                : null;
        LocalDateTime end =
            endDate != null
                ? LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atTime(23,59,59)
                : null;
        if (start != null && end != null){
            return todo.createdAt.between(start, end);
        } else if (start != null) {
            return todo.createdAt.goe(start);
        } else {
            return todo.createdAt.loe(end);
        }
    }
    // 담당자 닉네임으로 일정 검색
    private BooleanExpression nicknameContains(String nickname) {
        if(nickname == null || nickname.isBlank()) {
            return null;
        }
        return todo.id.eq(manager.todo.id) // 어떤 일정? ↓매니저가 담당하는 일정의 아이디와 일치하는 일정
            .and(manager.user.id.eq(user.id)) // 어떤 매니저? ↓유저의 아이디와 일치하는 매니저
            .and(user.nickname.contains(nickname)); // 어떤 유저? 닉네임이 포함된 유저
    }

}
