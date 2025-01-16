package org.example.expert.domain.comment.service;

import static org.example.expert.domain.common.exception.ExceptionType.TODO_NOT_FOUND;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.repository.CommentRepository;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.CustomException;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final TodoRepository todoRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public CommentResponse saveComment(
        AuthUser authUser,
        long todoId,
        CommentSaveRequest commentSaveRequest
    ) {
        User user = User.fromAuthUser(authUser);

        Todo todo = todoRepository.findById(todoId)
            .orElseThrow(() -> new CustomException(TODO_NOT_FOUND));

        Comment newComment = new Comment(commentSaveRequest.getContents(), user, todo);
        Comment savedComment = commentRepository.save(newComment);

        return CommentResponse.toDto(savedComment);
    }

    public List<CommentResponse> getComments(long todoId) {
        List<Comment> commentList = commentRepository.findAllByTodoId(todoId);

        return commentList.stream().map(CommentResponse::toDto).toList();
    }
}
