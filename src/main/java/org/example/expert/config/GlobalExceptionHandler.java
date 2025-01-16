package org.example.expert.config;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.common.exception.CustomException;
import org.example.expert.domain.common.exception.ExceptionResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 비즈니스 에러
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionResponse> handleCustomException(
        HttpServletRequest request,
        CustomException e
    ) {
        log.warn("잘못된 요청이 들어왔습니다. URI: {}, 코드: {}, 내용:  {}",
            request.getRequestURI(),
            e.getCode(),
            e.getMessage());

        return ResponseEntity
            .status(e.getHttpStatus())
            .body(new ExceptionResponse(e.getCode(), e.getMessage()));
    }

    // 500 서버에러
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(
        HttpServletRequest request,
        Exception e
    ) {
        log.error("예상하지 못한 예외가 발생했습니다. URI:{}, 내용:{}", request.getRequestURI(), e.getMessage(), e);
        return ResponseEntity
            .internalServerError()
            .body(new ExceptionResponse("G00", "서버가 응답할 수 없습니다."));
    }

    // @Valid 에러
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValid(
        HttpServletRequest request,
        MethodArgumentNotValidException e
    ) {
        //글로벌 에러 메시지들
        String globalErrorMessage = e.getGlobalErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .collect(Collectors.joining(", ", "[Global Error : ", "], \t"));

        //필드 에러 메시지들
        String fieldErrorMessage = e.getFieldErrors().stream()
            .map(error -> error.getField() + " : " + error.getDefaultMessage())
            .collect(Collectors.joining(" ", "[Field Error : ", "]"));

        String errorMessage = globalErrorMessage + fieldErrorMessage;
        log.warn("잘못된 요청이 들어왔습니다. URI:{}, 내용:{}", request.getRequestURI(), errorMessage);
        return ResponseEntity
            .badRequest()
            .body(new ExceptionResponse("G01", errorMessage));
    }

    // 파라미터 존재하지 않을 때 발생
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ExceptionResponse> handleMissingServletRequestParameterException(
        HttpServletRequest request,
        MissingServletRequestParameterException e
    ) {
        String errorMessage = e.getParameterName() + " 값이 누락되었습니다.";
        log.warn("잘못된 요청이 들어왔습니다. URI:{}, 내용:{}", request.getRequestURI(), errorMessage);
        return ResponseEntity
            .badRequest()
            .body(new ExceptionResponse("G02", errorMessage));
    }

    // 파라미터 타입과 일치하지 않을 때 발생
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentTypeMismatch(
        HttpServletRequest request,
        MethodArgumentTypeMismatchException e
    ) {
        String errorMessage = String.format("파라미터 타입 불일치: %s (기대된 타입: %s, 실제 값: %s)", e.getName(),
            Objects.requireNonNull(e.getRequiredType()).getSimpleName(), e.getValue());
        log.warn("잘못된 요청이 들어왔습니다. URI:{}, 내용:{}", request.getRequestURI(), errorMessage);
        return ResponseEntity
            .badRequest()
            .body(new ExceptionResponse("G03", errorMessage));
    }

    // HTTP 요청의 본문을 읽을 수 없을 때 발생
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleHttpMessageNotReadable(
        HttpServletRequest request,
        HttpMessageNotReadableException e
    ) {
        log.warn("잘못된 요청이 들어왔습니다. URI:{}, 내용:{}", request.getRequestURI(), e.getMessage());
        return ResponseEntity
            .badRequest()
            .body(new ExceptionResponse("G04", "요청 메시지가 유효하지 않습니다."));
    }
}
