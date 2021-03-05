package org.fincl.miss.server.logging.logger.service;

import org.fincl.miss.core.logging.UserLog;

/**
 * hazelCast 로그관리대상 사용자 map정보를 control할 서비스
 */
public interface UserLogService {
	
	/**
	 * 동기화된 사용자 로그관리 map에 해당 정보가 있는지 검사
	 * @param userId  : key값(사용자 아이디)
	 * @return true || false
	 */
	public boolean containsKey(String userId);
	
	/**
	 * map.put()
	 * @param userId  : key값(사용자 아이디)
	 * @param userLog : 로그 관리 대상 사용자 정보 vo
	 */
	public void put(String userId, UserLog userLog);
	
	/**
	 * map.get()
	 * @param userId   : key값(사용자 아이디)
	 * @return UserLog : 로그 관리 대상 사용자 정보 vo
	 */
	public UserLog get(String userId);
	
	/**
	 * map.remove()
	 * @param userId   : key값(사용자 아이디)
	 */
	public void remove(String userId);

	
}
