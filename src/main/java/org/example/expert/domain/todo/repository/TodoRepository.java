package org.example.expert.domain.todo.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.Optional;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u ORDER BY t.modifiedAt DESC")
    Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);

    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u WHERE t.weather = :weather ORDER BY t.modifiedAt DESC")
    Page<Todo> findAllByWeatherOrderByModifiedAtDesc(Pageable pageable,
                                                     @Param("weather") String weather);

    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u WHERE DATE(t.modifiedAt) BETWEEN :firstDate AND :lastDate")
    Page<Todo> findAllByPeriodOrderByModifiedAtDesc(Pageable pageable,
                                                    @Param("firstDate") Date firstDate,
                                                    @Param("lastDate") Date lastDate);

    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u WHERE t.weather = :weather AND DATE(t.modifiedAt) BETWEEN :firstDate AND :lastDate")
    Page<Todo> findAllByPeriodAndWeatherOrderByModifiedAtDesc(Pageable pageable,
                                                              @Param("weather") String weather,
                                                              @Param("firstDate") Date firstDate,
                                                              @Param("lastDate")Date lastDate);

    @Query("SELECT t FROM Todo t " +
            "LEFT JOIN t.user " +
            "WHERE t.id = :todoId")
    Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);
}
