package org.fincl.miss.server.scheduler.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ClusterSynchronied 어노테이션이 적용된 함수의 동기화 처리.
 * 
 * 
 * 
 * @author civan
 *
 */
@Component
@Aspect
public class JobSyncronizationAspect {

	@Autowired
	JobSyncronization jobSync;
	
	/**
	 * 멀티노드에서 수행시, Task가 수행된 경우, 다른 노드에서는 동일한 Task가 수행되지 않도록 Lock.
	 * 장애가 발생하는 경우, Task정보는 초기화하며, 다음 수행시 먼저 수행되는 노드에서 우선권을 획득한다.
	 * 
	 * 
	 * @param pjp
	 * @param syncParams
	 * @return
	 * @throws Throwable
	 */
	@Around("execution(public * *(..)) && @annotation(syncParams)")
	public Object processCllsterSyncronized(
			ProceedingJoinPoint pjp, ClusterSynchronized syncParams) throws Throwable{
		final String token = syncParams.jobToken();

		if (this.jobSync.aquireLock(token)) {
	        try {
	            return pjp.proceed();
	        } catch(Exception e) {
	           this.jobSync.releaseLock(token);
	        }
		}
		
		return null;
	}
}
