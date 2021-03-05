package org.fincl.miss.server.scheduler.job.blackUsr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import egovframework.rte.psl.dataaccess.mapper.Mapper;

@Mapper("blackListMapper")
public interface BlackListMapper {

	List<String> getBlackTargetList();

	void delBlackList(String  map);

	void setPenaltyStatus(String map);

}
