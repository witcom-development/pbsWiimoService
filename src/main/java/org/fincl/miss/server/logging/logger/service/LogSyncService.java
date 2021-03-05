package org.fincl.miss.server.logging.logger.service;

import java.util.Map;

import org.fincl.miss.core.logging.LogInfo;

/**
 * 각 로거별 로그 레벨 syncing을 담당하는 서비스
 */
public interface LogSyncService {
	
	/**
	 * hazelCast 전체 맵 정보의 setter
	 * @param logInfos
	 */
	public void setLogInfoSync(final Map<String, Map<String, LogInfo>> logInfos);
	
	/**
	 * hazelCast 전체 맵 정보의 getter
	 * @return
	 */
	public Map<String, Map<String, LogInfo>> getLogInfoSync();

	/**
	 * 서버 Id getter
	 * @return
	 */
	public String getServerId();

	/**
	 * 서버 Id setter
	 * @param serverId
	 */
	public void setServerId(String serverId);
	
	/**
	 * 서비스 레벨 변경 로거 이름, 서비스 레벨 필터 이름 문자열 getter, setter
	 */
	public String getServiceLevelMof();
	
	public void setServiceLevelMof(String serviceLevelMof);
	
	public String getServiceFilter();
	
	public void setServiceFilter(String serviceFilter);	
	
	/**
	 * LogSyncServiceImpl의 bean등록 시 맵의 초기화 작업(LogSyncServiceImpl에 private으로 구현)
	 */
	/**private void initLogInfoMap()*/
	
}
