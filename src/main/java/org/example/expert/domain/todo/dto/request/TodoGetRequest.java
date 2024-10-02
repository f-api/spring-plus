package org.example.expert.domain.todo.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Getter
@NoArgsConstructor
public class TodoGetRequest {
    int page =1;
    int size = 10;
    String weather;
    Date firstDate;
    Date lastDate;
}
