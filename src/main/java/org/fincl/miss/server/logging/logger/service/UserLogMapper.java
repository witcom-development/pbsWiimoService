package org.fincl.miss.server.logging.logger.service;

import java.util.List;

import org.fincl.miss.core.logging.UserLog;

import egovframework.rte.psl.dataaccess.mapper.Mapper;

@Mapper("userLogMapper")
public interface UserLogMapper {
	
	/**
	 * map 초기화를 위한 전체 사용자 로그관리 목록을 조회한다.
	 * @return
	 */
	public List<UserLog> getPossibleUserLogList();
	
}
