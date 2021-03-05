package org.fincl.miss.server.scheduler.job.tmoney;

import java.util.List;

import org.fincl.miss.server.scheduler.job.tmoney.vo.TmoneyStatusVo;

import egovframework.rte.psl.dataaccess.mapper.Mapper;

@Mapper("tmoneyResultChkMapper")
public interface TmoneyResultChkMapper {

	List<TmoneyStatusVo> getPaymentResultChkTargetList(TmoneyStatusVo pVo);

	void setPaymentReadResCd(TmoneyStatusVo vo);

	void addTmoneyPaymentCancelHist(TmoneyStatusVo vo);

}
