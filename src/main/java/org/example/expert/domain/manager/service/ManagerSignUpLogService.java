package org.example.expert.domain.manager.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.manager.entity.ManagerSignUpLog;
import org.example.expert.domain.manager.repository.ManagerSignUpLogRepository;
import org.example.expert.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ManagerSignUpLogService {
    private final ManagerSignUpLogRepository managerSignUpLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(User user, Long todoId) {
        managerSignUpLogRepository.save(new ManagerSignUpLog(user,todoId));
    }
}
