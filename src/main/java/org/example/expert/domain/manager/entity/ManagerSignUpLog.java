package org.example.expert.domain.manager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.common.entity.Timestamped;
import org.example.expert.domain.user.entity.User;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "log")
public class ManagerSignUpLog extends Timestamped{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long todoId;

    //등록 요청 유저
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public ManagerSignUpLog(User user,Long todoId) {
        this.user = user;
        this.todoId = todoId;
    }
}
