package org.example.expert.domain.todo.repository;

import java.time.LocalDateTime;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TodoRepository extends JpaRepository<Todo, Long>, TodoCustomRepository{

    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u "
        + "WHERE (:weather IS NULL OR t.weather = :weather )"
        + "AND (:startDate IS NULL OR :endDate IS NULL OR t.modifiedAt between :startDate AND :endDate )"
        + "ORDER BY t.modifiedAt DESC")
    Page<Todo> findTodos(
        String weather, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable
    );

//    @Query("SELECT t FROM Todo t " +
//            "LEFT JOIN t.user " +
//            "WHERE t.id = :todoId")
//    Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);
}
