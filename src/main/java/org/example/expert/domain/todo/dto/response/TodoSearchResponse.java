package org.example.expert.domain.todo.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TodoSearchResponse {
	private String title;
	private Long managerNum;
	private Long commentNum;
}
