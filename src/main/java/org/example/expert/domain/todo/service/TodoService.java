package org.example.expert.domain.todo.service;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;
    private final WeatherClient weatherClient;

    @Transactional
    public TodoSaveResponse saveTodo(AuthUser authUser, TodoSaveRequest todoSaveRequest) {
        User user = User.fromAuthUser(authUser);

        String weather = weatherClient.getTodayWeather();

        Todo newTodo = new Todo(
                todoSaveRequest.getTitle(),
                todoSaveRequest.getContents(),
                weather,
                user
        );
        Todo savedTodo = todoRepository.save(newTodo);

        return new TodoSaveResponse(
                savedTodo.getId(),
                savedTodo.getTitle(),
                savedTodo.getContents(),
                weather,
                new UserResponse(user.getId(), user.getEmail())
        );
    }

    public Page<TodoResponse> getTodos(int page, int size, String weather, String startDate, String endDate) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Todo> todos = todoRepository.findAll(makeDynamicQuery(weather, startDate, endDate), pageable);

        return todos.map(todo -> new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(todo.getUser().getId(), todo.getUser().getEmail()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        ));
    }

    public Page<TodoSearchResponse> searchTodos(int page, int size, String title, String nickname, String startDate, String endDate) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return todoRepository.search(
                pageable,
                title,
                nickname,
                strToDateFormat(startDate),
                strToDateFormat(endDate));
    }

    public TodoResponse getTodo(long todoId) {
        Todo todo = todoRepository.getTodo(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        User user = todo.getUser();

        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(user.getId(), user.getEmail()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        );
    }

    public Specification<Todo> makeDynamicQuery(String weather, String startDate, String endDate) {
        return ((root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (Strings.isBlank(weather) && Strings.isBlank(startDate) && Strings.isBlank(endDate)) return null;

            if (Strings.isNotBlank(weather)) {
                predicates.add(builder.equal(root.get("weather"), weather));
            }

            if (Strings.isNotBlank(startDate) && Strings.isNotBlank(endDate)) {
                predicates.add(builder.between(root.get("createdAt"), strToDateFormat(startDate), strToDateFormat(endDate)));
            } else {
                if (Strings.isNotBlank(startDate)) {
                    predicates.add(builder.greaterThan(root.get("createdAt"), strToDateFormat(startDate)));
                }
                if (Strings.isNotBlank(endDate)) {
                    predicates.add(builder.lessThan(root.get("createdAt"), strToDateFormat(endDate)));
                }
            }

            root.fetch("user", JoinType.LEFT);
            query.orderBy(builder.desc(root.get("modifiedAt")));

            return builder.and(predicates.toArray(new Predicate[0]));
        });
    }

    private LocalDateTime strToDateFormat(String date) {
        if (Strings.isBlank(date)) return null;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            return sdf.parse(date).toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        } catch (ParseException e) {
            throw new InvalidRequestException("Invalid date format");
        }
    }
}
