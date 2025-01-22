package org.example.expert.domain.todo.repository;

import java.util.Optional;

import static org.example.expert.domain.todo.entity.QTodo.*;
import static org.example.expert.domain.user.entity.QUser.*;

import org.example.expert.domain.todo.entity.Todo;

import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

public class TodoQueryRepositoryImpl implements TodoQueryRepository{

	private final JPAQueryFactory jpaQueryFactory;

	public TodoQueryRepositoryImpl(EntityManager em){
		this.jpaQueryFactory=new JPAQueryFactory(em);
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
}
