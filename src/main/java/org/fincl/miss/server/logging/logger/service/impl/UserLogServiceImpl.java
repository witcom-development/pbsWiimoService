package org.fincl.miss.server.logging.logger.service.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.fincl.miss.core.logging.UserLog;
import org.fincl.miss.core.logging.service.UserLogMapService;
import org.fincl.miss.server.logging.logger.service.UserLogMapper;
import org.fincl.miss.server.logging.logger.service.UserLogService;
import org.springframework.beans.factory.annotation.Autowired;

public class UserLogServiceImpl implements UserLogService {

	@Autowired
	private UserLogMapService userLogMapService;
	
	@Autowired
	private UserLogMapper userLogMapper;
	
	public boolean containsKey(String userId) {
		return userLogMapService.containsKey(userId);
	}

	public void put(String userId, UserLog userLog) {
		userLogMapService.put(userId, userLog);
	}

	public UserLog get(String userId) {
		return userLogMapService.get(userId);
	}

	public void remove(String userId) {
		userLogMapService.remove(userId);
	}

	@PostConstruct
	private void initMap() {
		if(userLogMapService.getUserLogMap().isEmpty()) {
			// DB에서 목록을 가져온다
			List<UserLog> list = userLogMapper.getPossibleUserLogList();
			
			// list 를 map으로 변경해서 userLogMapService에 저장
			Map<String, UserLog> initMap = new ConcurrentHashMap<String, UserLog>();
			for(UserLog vo:list) {
				initMap.put(vo.getUserId(), vo);
			}
			userLogMapService.getUserLogMap().putAll(initMap);
		}
	}
	
	@Override
	public String toString() {
		return userLogMapService.getUserLogMap().values().toString();
	}
	
}
