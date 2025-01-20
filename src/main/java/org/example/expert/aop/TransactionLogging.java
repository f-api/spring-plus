package org.example.expert.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class TransactionLogging {

	@Pointcut("execution(* org.example.expert.domain.todo.service .*.*(..))")
	private void allTodoService() {
	}

	@Before("allTodoService()")
	public void doBeforeServiceMethod(JoinPoint joinPoint) {
		log.info("[Method] {}", joinPoint.getSignature());
		boolean isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
		log.info("[Transaction active] {}", isTransactionActive);
		log.info("[Is Transaction ReadOnly] {}", TransactionSynchronizationManager.isCurrentTransactionReadOnly());
		log.info("===================================");
	}
}