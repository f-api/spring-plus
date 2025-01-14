package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long>, JpaSpecificationExecutor<Todo> {

    @Query("SELECT t FROM Todo t " +
            "LEFT JOIN t.user " +
            "WHERE t.id = :todoId")
    Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);


    // pagination

    @EntityGraph(attributePaths = "user")
    Page<Todo> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "user")
    Page<Todo> findAllByWeather(String weather, Pageable pageable);

    @EntityGraph(attributePaths = "user")
    Page<Todo> findAllByModifiedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);

    @EntityGraph(attributePaths = "user")
    Page<Todo> findAllByWeatherAndModifiedAtBetween(String weather, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);
}
