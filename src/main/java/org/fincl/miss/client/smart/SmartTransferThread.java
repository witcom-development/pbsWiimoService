package org.fincl.miss.client.smart;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.fincl.miss.client.smart.SmartTransfer;
import org.fincl.miss.server.scheduler.job.transfer.vo.TransferVO;

public class SmartTransferThread implements Runnable {

	private String serverIp;
	
	private List<TransferVO> list ;
	
	private String fileDate;  // 대중교통 이용정보 전문요청 누락으로 인한 fileDate 추가_20171113_JJH
	
	private String headerDate; // 대중교통 이용정보 전문요청 누락으로 인한 fileDate 추가_20171120_JJH
	
	public SmartTransferThread(String serverIp,List<TransferVO> list, String fileDate, String headerDate){		// 대중교통 이용정보 전문요청 누락으로 인한 fileDate 추가_20171113_JJH
		this.serverIp = serverIp;
		this.list = list;
		this.fileDate = fileDate;	// 대중교통 이용정보 전문요청 누락으로 인한 fileDate 추가_20171113_JJH
		this.headerDate = headerDate;
	}
	


	@Override
	public void run() {
		String host = this.serverIp;
		List<TransferVO> list = this.list;
		SmartTransfer client = new SmartTransfer(host,"6100", fileDate, headerDate);	// 대중교통 이용정보 전문요청 누락으로 인한 fileDate 추가_20171113_JJH
		client.setList(list);
		client.start();
		
	}

}
