package org.example.expert.domain.manager.repository;

import org.example.expert.domain.manager.entity.ManagerSignUpLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerSignUpLogRepository extends CrudRepository<ManagerSignUpLog,Long> {
}
