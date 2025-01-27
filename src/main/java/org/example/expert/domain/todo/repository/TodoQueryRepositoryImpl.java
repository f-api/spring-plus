package org.example.expert.domain.todo.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.example.expert.domain.comment.entity.QComment.*;
import static org.example.expert.domain.manager.entity.QManager.*;
import static org.example.expert.domain.todo.entity.QTodo.*;
import static org.example.expert.domain.user.entity.QUser.*;

import org.example.expert.domain.todo.dto.request.TodoSearchRequest;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

public class TodoQueryRepositoryImpl implements TodoQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public TodoQueryRepositoryImpl(EntityManager em) {
		this.jpaQueryFactory = new JPAQueryFactory(em);
	}

	// Lv2-8: JPQL로 작성된 findByIdWithUser()를 QueryDSL로 변경
	@Override
	public Optional<Todo> findByIdWithUser(Long todoId) {
		return Optional.ofNullable(jpaQueryFactory
			.selectFrom(todo)
			.join(todo.user, user).fetchJoin()
			.where(todo.id.eq(todoId))
			.fetchOne());
	}

	// Lv3-10
	@Override
	public List<TodoSearchResponse> searchTodos(TodoSearchRequest todoSearchRequest,
		LocalDateTime startDateTime, LocalDateTime endDateTime
	) {
		return jpaQueryFactory
			.select(Projections.fields(TodoSearchResponse.class,
				todo.title.as("title"),
				manager.count().as("managerNum"),
				comment.count().as("commentNum")
			))
			.from(todo)
			.join(todo.managers, manager)
			.join(todo.comments, comment)
			.where(containsManagerName(todoSearchRequest.getManagerNickname()),
				containsTitle(todoSearchRequest.getTitle()),
				checkStartTime(startDateTime),
				checkEndTime(endDateTime))
			.groupBy(todo.id)
			.orderBy(todo.createdAt.desc())
			.offset((todoSearchRequest.getPage() - 1) * todoSearchRequest.getSize())
			.limit(todoSearchRequest.getSize())
			.fetch();
	}

	/*============== WHERE 조건 메서드 ==============*/
	private BooleanExpression checkEndTime(LocalDateTime endDateTime) {
		return endDateTime == null ? null : todo.modifiedAt.loe(endDateTime);
	}

	private BooleanExpression checkStartTime(LocalDateTime startDateTime) {
		return startDateTime == null ? null : todo.modifiedAt.goe(startDateTime);
	}

	private BooleanExpression containsTitle(String title) {
		return title == null ? null : todo.title.contains(title);
	}

	private BooleanExpression containsManagerName(String managerNickname) {
		return managerNickname == null ? null : manager.user.nickname.contains(managerNickname);
	}

}
