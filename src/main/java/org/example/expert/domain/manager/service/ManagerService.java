package org.example.expert.domain.manager.service;

import static org.example.expert.domain.common.exception.ExceptionType.MANAGER_NOT_FOUND;
import static org.example.expert.domain.common.exception.ExceptionType.MANAGER_ONESELF;
import static org.example.expert.domain.common.exception.ExceptionType.MANGER_NOT_EQUAL;
import static org.example.expert.domain.common.exception.ExceptionType.NOT_OWNER;
import static org.example.expert.domain.common.exception.ExceptionType.TODO_NOT_FOUND;
import static org.example.expert.domain.common.exception.ExceptionType.USER_NOT_FOUND;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.CustomException;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.manager.repository.ManagerRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final UserRepository userRepository;
    private final TodoRepository todoRepository;

    @Transactional
    public ManagerResponse saveManager(
        AuthUser authUser,
        long todoId,
        ManagerSaveRequest managerSaveRequest
    ) {
        User user = User.fromAuthUser(authUser);
        Todo todo = todoRepository.findById(todoId)
            .orElseThrow(() -> new CustomException(TODO_NOT_FOUND));

        if (!ObjectUtils.nullSafeEquals(user.getId(), todo.getUser().getId())) {
            throw new CustomException(NOT_OWNER);
        }

        User managerUser = userRepository.findById(managerSaveRequest.getManagerUserId())
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        if (ObjectUtils.nullSafeEquals(user.getId(), managerUser.getId())) {
            throw new CustomException(MANAGER_ONESELF);
        }

        Manager saved = managerRepository.save(new Manager(managerUser, todo));
        return ManagerResponse.toDto(saved);
    }

    public List<ManagerResponse> getManagers(long todoId) {
        if (!todoRepository.existsById(todoId)) {
            throw new CustomException(TODO_NOT_FOUND);
        }

        List<Manager> managerList = managerRepository.findAllByTodoId(todoId);

        return managerList.stream().map(ManagerResponse::toDto).toList();
    }

    @Transactional
    public void deleteManager(AuthUser authUser, long todoId, long managerId) {
        User user = User.fromAuthUser(authUser);
        Todo todo = todoRepository.findById(todoId)
            .orElseThrow(() -> new CustomException(TODO_NOT_FOUND));

        if (!ObjectUtils.nullSafeEquals(user.getId(), todo.getUser().getId())) {
            throw new CustomException(NOT_OWNER);
        }

        Manager manager = managerRepository.findById(managerId)
            .orElseThrow(() -> new CustomException(MANAGER_NOT_FOUND));

        if (!ObjectUtils.nullSafeEquals(todo.getId(), manager.getTodo().getId())) {
            throw new CustomException(MANGER_NOT_EQUAL);
        }

        managerRepository.delete(manager);
    }
}
