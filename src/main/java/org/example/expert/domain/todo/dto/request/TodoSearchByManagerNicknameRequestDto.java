package org.example.expert.domain.todo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TodoSearchByManagerNicknameRequestDto {
    int pageNum = 1;
    int size = 10;
    @NotBlank
    String nickname;
}
