package org.fincl.miss.server.scheduler.job.voucherEndChk.service;

import org.fincl.miss.server.scheduler.job.voucherEndChk.vo.VoucherEndChkVO;

import egovframework.rte.psl.dataaccess.mapper.Mapper;

@Mapper("VoucherEndChkMapper")
public interface VoucherEndChkMapper {
	java.util.List<VoucherEndChkVO> getVoucherEndUserInfo();
}
