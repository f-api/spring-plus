package org.example.expert.domain.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExceptionType {

    //Server
    SERVER_IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S01", "이미지 업로드에 실패했습니다."),
    SERVER_IMAGE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S02", "이미지 삭제에 실패했습니다."),

    //Auth
    AUTH_WRONG_USED(HttpStatus.INTERNAL_SERVER_ERROR, "A01", "@Auth와 AuthUser 타입은 함께 사용되어야 합니다."),
    AUTH_FAILED(HttpStatus.BAD_REQUEST, "A02", "이메일 또는 비밀번호가 일치하지 않습니다."),
    AUTH_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "A03", "토큰을 찾을 수 없습니다."),

    //User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U01", "해당 사용자를 찾을 수 없습니다."),
    EMAIL_DUPLICATE(HttpStatus.BAD_REQUEST, "U02", "이메일이 중복됩니다."),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "U03", "기존 비밀번호가 일치하지 않습니다."),
    PASSWORD_SAME(HttpStatus.BAD_REQUEST, "U04", "새 비밀번호는 기존 비밀번호와 같을 수 없습니다."),
    INVALID_USER_ROLE(HttpStatus.BAD_REQUEST, "U05", "유효하지 않은 UserRole"),

    //Manager
    MANAGER_NOT_FOUND(HttpStatus.NOT_FOUND, "M01", "해당 매니저를 찾을 수 없습니다."),
    NOT_OWNER(HttpStatus.FORBIDDEN, "M02", "해당 일정을 만든 유저가 아닙니다."),
    MANAGER_ONESELF(HttpStatus.BAD_REQUEST, "M03", "일정 작성자는 본인을 담당자로 등록할 수 없습니다."),
    MANGER_NOT_EQUAL(HttpStatus.BAD_REQUEST, "M04", "해당 일정에 등록된 담당자가 아닙니다."),

    //Comment
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "C01", "해당 댓글을 찾을 수 없습니다."),

    //to-do
    TODO_NOT_FOUND(HttpStatus.NOT_FOUND, "T01", "해당 Todo 를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
