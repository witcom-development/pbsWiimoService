package org.fincl.miss.server.scheduler.annotation;

import java.lang.reflect.Array;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

/**
 * Job 동기화를 처리하기 위한 객체.
 * 멀티노드 구성시 해당 노드의 id를 통합캐쉬(hazelcast)에 저장하고,
 * job이 먼저 수행된 node에서만 스케쥴러에 따른 job을 수행하고,
 * 다른 노드에서 수행되지 못하도록 lock을 설정한다.
 * 
 * @author civan
 *
 */
@Component
public class JobSyncronization {

	/**
	 * Spring CacheManager
	 * hazelcast의 경우.
	 * com.hazelcast.spring.cache.HazelcastCacheManager를 가져온다.  
	 */
	@Autowired
	private CacheManager cacheManager;
	
	/**
	 * 인스턴스 ID.
	 * 노드가 활성화되면 유일키로 사용된다.
	 */
	private final String instanceNodeId = UUID.randomUUID().toString();
	
	/**
	 * 잠김여부 확인.
	 * @param jobToken
	 * @return boolean
	 */
	final public boolean aquireLock(String jobToken){
	
		/**
		 * 해당 토큰이 존재하지 않거나, 빈값인 경우. 
		 * 해당 토큰을 키로 하여 수행된 노드값을 저장한다.
		 */
		if(this.cacheManager.getCache("jobRepository").get(jobToken) ==null 
			|| isEmpty((String)this.cacheManager.getCache("jobRepository").get(jobToken).get())){
			this.cacheManager.getCache("jobRepository").put(jobToken, this.instanceNodeId);
		}
		return this.isOurLock(jobToken);
	
	}

	/**
	 * 해당 노드에서 수행된 토큰여부 확인.
	 * 
	 * 현재 인스턴스와 토큰이 수행된 인스턴스를 비교하여,
	 * 동일한 경우 true, 다른 노드에서 수행된 토큰정보라면 false를 반환한다.
	 * 
	 * @param jobToken
	 * @return boolean
	 */
	final public boolean isOurLock(String jobToken) {
		try{
			boolean isOurLock = equals(this.instanceNodeId,  this.cacheManager.getCache("jobRepository").get(jobToken).get());
			
			return isOurLock;
		}catch(Exception e){
			
		}
		return false;
	}
	
	/**
	 * 현재 인스턴스 정보를 반환한다.
	 * 
	 * @return
	 */
	public String instanceNodeId(){
		return this.instanceNodeId();
	}
	
	/**
	 * 해당인스턴스에서 토큰을 삭제한다.
	 * 
	 * @param jobToken
	 */
	final public void releaseLock(String jobToken){
		if(this.isOurLock(jobToken)){
			try{
				this.cacheManager.getCache("jobRepository").evict(jobToken);
			}catch(Exception e){}
		}
	}
	
	/**
	 * 객체의 배열여부 확인.
	 * @param obj
	 * @return
	 */
	private boolean isArray(Object obj) {
		if (obj != null)
			return obj.getClass().isArray();
		return false;
	}

	/**
	 * 파라미터의 두인자의 객체가 동일한지 여부를 확인한다.
	 * 파라미터의 두인자가 배열인 경우 배열값이 동일한지 체크한다.
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
	private boolean equals(Object first, Object second) {
		if (isArray(first)) {
			if (!(isArray(second))) {
				return false;
			}
			int lenght = Array.getLength(first);
			if (lenght != Array.getLength(second)) {
				return false;
			}
			for (int i = 0; i < lenght; ++i) {
				if (!(equals(Array.get(first, i), Array.get(second, i)))) {
					return false;
				}
			}
			return true;
		}

		return objectEquals(first, second);
	}
	
	/**
	 * 파라미터의 두인자의 객체가 동일한지 여부를 확인한다.
	 * 
	 * @param one
	 * @param two
	 * @return
	 */
	private boolean objectEquals(Object one, Object two) {
		if ((one == null) && (two != null)) {
			return false;
		}
		return ((one == null) || (one.equals(two)));
	}
	
	/**
	 * 빈 문자열 여부를 확인한다.
	 * Null 또는 공백문자인 경우. true를 반환한다.
	 * 
	 * @param str
	 * @return
	 */
	private boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }
}
