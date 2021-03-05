package org.fincl.miss.server.scheduler.job.transfer.service;

import java.util.List;

import org.fincl.miss.server.scheduler.job.transfer.vo.TransferVO;

import egovframework.rte.psl.dataaccess.mapper.Mapper;

@Mapper("transferMapper")
public interface TransferMapper {
	public List<TransferVO> getTransCardList();
	public int addTransTmoneyHistory(TransferVO transferVO);
	public List<TransferVO> getTransMileList(TransferVO transferVO);
	public int addTransMileage(TransferVO transferVO);
	public int setTransTmoney(TransferVO transferVO);
}
