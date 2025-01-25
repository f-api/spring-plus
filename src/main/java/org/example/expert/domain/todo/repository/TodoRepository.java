package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    @Query("""
            SELECT t FROM Todo t
            LEFT JOIN FETCH t.user u
            WHERE (:weather IS NULL OR t.weather = :weather)
            AND (:startTime IS NULL OR t.modifiedAt >= :startTime)
            AND (:endTime IS NULL OR t.modifiedAt <= :endTime)
            ORDER BY t.modifiedAt DESC
            """)
    Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable, @Param("weather") String weather, LocalDateTime startTime, LocalDateTime endTime);

    @Query("SELECT t FROM Todo t " +
            "LEFT JOIN t.user " +
            "WHERE t.id = :todoId")
    Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);
}
