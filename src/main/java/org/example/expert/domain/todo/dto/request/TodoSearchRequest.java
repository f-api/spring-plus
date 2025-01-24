package org.example.expert.domain.todo.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TodoSearchRequest {
	private final Long page = 1L;
	private final Long size = 10L;
	private String title;
	private String managerNickname;
}
