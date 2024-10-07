package org.example.expert.domain.todo.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.security.SecurityUtil;
import org.example.expert.domain.todo.dto.request.*;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponseDto;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.todo.repository.TodoRepositoryCustom;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final SecurityUtil securityUtil;
    private final TodoRepository todoRepository;
    private final TodoRepositoryCustom todoRepositoryCustom;
    private final WeatherClient weatherClient;

    @Transactional
    public TodoSaveResponse saveTodo(TodoSaveRequest todoSaveRequest) {
        User user = securityUtil.getCurrentUser();

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
                new UserResponse(user.getId(), user.getEmail(), user.getNickname())
        );
    }

    public Page<TodoResponse> getTodos(TodoGetRequest request) {
        Page<Todo> todos = requestToTodoPage(request);

        return todos.map(todo -> new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(todo.getUser().getId(), todo.getUser().getEmail(), todo.getUser().getNickname()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        ));
    }

    /**
     * 분기 별 Page 반환
     * 분기 :
     * 1)날씨,기간 모두 없는 경우
     * 2)날씨만 있는 경우
     * 3)기간만 있는 경우
     * 4)날씨, 기간 모두 있는 경우
     *
     * @param request 페이지네이션과 분기 조건을 포함하는 RequestDto
     *                int page : 페이지 번호, 기본값 1
     *                int size : 페이지 크기, 기본값 10
     *                String weather : 문자열 날씨
     *                Date firstDate : 기간 시작일
     *                Date lastDate : 기간 마지막일
     * @return Page 반환
     */
    private Page<Todo> requestToTodoPage(TodoGetRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNum() - 1, request.getSize());

        //날씨, 기간 모두 없는 경우
        if (request.getWeather() == null && !validatePeriod(request.getFirstDate(),request.getLastDate())) {
            return todoRepository.findAllByOrderByModifiedAtDesc(pageable);
        }

        //날씨만 있는 경우
        else if (request.getWeather() != null && !validatePeriod(request.getFirstDate(),request.getLastDate())) {
            return todoRepository.findAllByWeatherOrderByModifiedAtDesc(pageable, request.getWeather());
        }

        //기간만 있는 경우
        else if (request.getWeather() == null && validatePeriod(request.getFirstDate(),request.getLastDate())) {
            return todoRepository.findAllByPeriodOrderByModifiedAtDesc(pageable, request.getFirstDate(), request.getLastDate());
        }

        //날씨, 기간 모두 있는 경우
        else {
            validatePeriod(request.getFirstDate(),request.getLastDate());
            return todoRepository.findAllByPeriodAndWeatherOrderByModifiedAtDesc(pageable, request.getWeather(), request.getFirstDate(), request.getLastDate());
        }
    }

    //기간이 입력된 경우 시작과 끝 둘 중 하나가 없는 경우, 끝 날짜가 시작 날짜보다 이전인 경우 예외 발생
    private boolean validatePeriod(Date firstDate, Date lastDate) {
        if (firstDate != null && lastDate == null) {
            throw new InvalidRequestException("기간의 끝이 입력되지 않았습니다.");
        }
        if (firstDate == null && lastDate != null) {
            throw new InvalidRequestException("기간의 시작이 입력되지 않았습니다.");
        }
        if (firstDate != null && lastDate != null && lastDate.before(firstDate)) {
            throw new InvalidRequestException("끝 날짜는 시작 날짜 이전일 수 없습니다.");
        }
        return firstDate != null && lastDate != null;
    }

    public TodoResponse getTodo(long todoId) {
        Todo todo = todoRepositoryCustom.findByIdWithUser(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        User user = todo.getUser();

        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(user.getId(), user.getEmail(), user.getNickname()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        );
    }

    //검색 관련
    public Page<TodoSearchResponseDto> searchByTitle(TodoSearchByTitleRequestDto requestDto) {
        Pageable pageable = PageRequest.of(requestDto.getPageNum()-1,requestDto.getSize());
        return todoRepositoryCustom.searchByTitle(pageable,requestDto.getTitle());
    }

    public Page<TodoSearchResponseDto> searchByManagerNickname(TodoSearchByManagerNicknameRequestDto requestDto) {
        Pageable pageable = PageRequest.of(requestDto.getPageNum()-1,requestDto.getSize());
        return todoRepositoryCustom.searchByManagerNickname(pageable, requestDto.getNickname());
    }

    public Page<TodoSearchResponseDto> searchByCreatedPeriod(TodoSearchByCreatedPeriodDto requestDto) {
        validatePeriod(requestDto.getFirstDate(),requestDto.getLastDate());
        Pageable pageable = PageRequest.of(requestDto.getPageNum()-1,requestDto.getSize());
        return todoRepositoryCustom.searchByCreatedDate(pageable,requestDto.getFirstDate(),requestDto.getLastDate());
    }

}
