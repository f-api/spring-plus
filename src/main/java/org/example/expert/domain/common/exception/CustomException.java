package org.example.expert.domain.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {

	private final HttpStatus httpStatus;
	private final String code;

	public CustomException(ExceptionType exceptionType) {
		super(exceptionType.getMessage());
		this.httpStatus = exceptionType.getHttpStatus();
		this.code = exceptionType.getCode();
	}
}
