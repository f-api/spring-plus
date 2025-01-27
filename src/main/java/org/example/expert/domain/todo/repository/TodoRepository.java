package org.example.expert.domain.todo.repository;

import java.time.LocalDateTime;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    @Query("""
            SELECT t
            FROM Todo t
            JOIN FETCH t.user
            WHERE (:startDate IS NULL OR :endDate IS NULL OR t.createdAt BETWEEN :startDate AND :endDate)
              AND (:weather IS NULL OR t.weather = :weather)
            ORDER BY t.modifiedAt DESC
        """)
    Page<Todo> findAllByConditionsOrderByModifiedAtDesc(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("weather") String weather,
        Pageable pageable
    );
}

