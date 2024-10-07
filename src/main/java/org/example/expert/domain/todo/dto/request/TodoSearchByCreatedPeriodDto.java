package org.example.expert.domain.todo.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Getter
@NoArgsConstructor
public class TodoSearchByCreatedPeriodDto {
    int pageNum = 1;
    int size = 10;
    @NotNull
    Date firstDate;
    @NotNull
    Date lastDate;
}
