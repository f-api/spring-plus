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

    // Lv1-5: 조건을 만족하는 쿼리문 작성
    @Query("SELECT t FROM Todo t " +
            "LEFT JOIN FETCH t.user u " +
            "WHERE (t.weather LIKE :weather OR :weather IS NULL) " +
            "AND (:startDateTime IS NULL OR t.modifiedAt >= :startDateTime)" +
            "AND (:endDateTime IS NULL OR t.modifiedAt <= :endDateTime)" +
            "ORDER BY t.modifiedAt DESC")
    Page<Todo> findAllByWeatherAndPeriodAtDesc(Pageable pageable, @Param("weather") String weather,
       @Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);

    @Query("SELECT t FROM Todo t " +
            "LEFT JOIN t.user " +
            "WHERE t.id = :todoId")
    Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);
}
