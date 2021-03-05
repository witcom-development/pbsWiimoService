package org.fincl.miss.server.scheduler;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.fincl.miss.server.scheduler.annotation.ClusterSynchronized;
import org.fincl.miss.server.scheduler.job.sms.PartitionVO;
import org.fincl.miss.server.scheduler.job.sms.SmsMessageVO;
import org.fincl.miss.server.scheduler.job.sms.service.SmsMessageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 배치스케쥴러..
 * 
 * Spring Batch를 이용한 스케쥴러..
 * Scheduled 어노테이션의 trigger를 schedule.xml로 처리하여 수행하도록 수정.
 * @author civan
 *
 */
@Component
public class BatchScheduler {
	
	@Autowired
	private SmsMessageMapper smsMessageMapper;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * SMS 재전송 처리 배치 Job.
	 * 배치 동기화를 위해 ClusterSynchronized Annotation을 설정하여야 한다.
	 * 
	 * 처리로직.
	 * 1. 재전송 실패로직을 가져옴.
	 * 2. 실패내역(이력)에 재전송상태 변경
	 * 3. 재전송 등록.
	 */
	@ClusterSynchronized( jobToken="reSendSMS")
	public void reSendSMS(){
		List<SmsMessageVO> list = smsMessageMapper.getFailSmsList();
		for(SmsMessageVO msgVO:list){
			smsMessageMapper.updateAutoSendResult(msgVO);
			smsMessageMapper.insertSmsMessage(msgVO);
		}
		
	}
	
	/**
	 * 파티션 삭제 스케쥴러
	 */
	@ClusterSynchronized( jobToken="dropPartition")
	public void dropPartition(){
		logger.debug("****************[Drop partition scheduler Start]**********************");
		
		List<PartitionVO> list = smsMessageMapper.getDropPartitionLit();
		if(list != null && list.size() >0){
			for(PartitionVO info:list){
				smsMessageMapper.execDropPartition(info);
			}
			logger.debug("****************[Drop partition scheduler End : {} 건 ]**********************", list.size());
		}else{
			logger.debug("****************[Drop partition scheduler End : 0 건 ]**********************");
		}
		
		
	}
	
	/**
	 * 일별 파티션 생성 스케쥴러.
	 */
	@ClusterSynchronized( jobToken="createDailyPartition")
	public void createDailyPartition(){
		logger.debug("****************[Create daily partition scheduler Start]**********************");
		
		List<PartitionVO> list = smsMessageMapper.getCreateDailyPartitionList();
		if(list != null && list.size() >0){
			for(PartitionVO info:list){
				smsMessageMapper.execCreatePartition(info);
			}
			logger.debug("****************[Create daily partition scheduler End : {} 건 ]**********************", list.size());
		}else{
			logger.debug("****************[Create daily partition scheduler End : 0 건 ]**********************");
		}
		
		
	}
	
	/**
	 * 일별 파티션 생성 스케쥴러.
	 */
	@ClusterSynchronized( jobToken="createDailyPartitionWithToDays")
	public void createDailyPartitionWithToDays(){
		logger.debug("****************[Create daily partition scheduler with Todays Start]**********************");
		
		List<PartitionVO> list = smsMessageMapper.getCreateDailyPartitionListWithToDays();
		if(list != null && list.size() >0){
			for(PartitionVO info:list){
				smsMessageMapper.execCreatePartitionWithToDays(info);
			}
			logger.debug("****************[Create daily partition scheduler with Todays End : {} 건 ]**********************", list.size());
		}else{
			logger.debug("****************[Create daily partition scheduler with Todays End : 0 건 ]**********************");
		}
		
		
	}
	
	/**
	 * 월별 파티션 생성 스케쥴러.
	 */
	@ClusterSynchronized( jobToken="createMonthPartition")
	public void createMonthPartition(){
		logger.debug("****************[Create Month partition scheduler Start]**********************");
		
		List<PartitionVO> list = smsMessageMapper.getCreateMonthPartitionList();
		if(list != null && list.size() >0){
			for(PartitionVO info:list){
				smsMessageMapper.execCreatePartition(info);
			}
			logger.debug("****************[Create Month partition scheduler End : {} 건 ]**********************", list.size());
		}else{
			logger.debug("****************[Create Month partition scheduler End : 0 건 ]**********************");
		}
		
		
	}
	
	/**
	 * 월별 파티션 생성 스케쥴러.
	 */
	@ClusterSynchronized( jobToken="createMonthPartitionWithToDays")
	public void createMonthPartitionWithToDays(){
		logger.debug("****************[Create Month partition scheduler with Todays Start]**********************");
		
		List<PartitionVO> list = smsMessageMapper.getCreateMonthPartitionListWithToDays();
		if(list != null && list.size() >0){
			for(PartitionVO info:list){
				smsMessageMapper.execCreatePartitionWithToDays(info);
			}
			logger.debug("****************[Create Month partition scheduler with Todays End : {} 건 ]**********************", list.size());
		}else{
			logger.debug("****************[Create Month partition scheduler with Todays End : 0 건 ]**********************");
		}
		
		
	}
	
	/**
	 * 보관주기가 경과된 로그파일을 삭제.
	 */
	@ClusterSynchronized( jobToken="deleteLog")
	public void deleteLog(){
		logger.debug("****************[deleteLog scheduler Start]**********************");
		
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd");
	    
		String logPath = "/nas_link/spb/logs/";
		
		Calendar cal = new GregorianCalendar(Locale.KOREA);
	    cal.setTime(new Date());
	    cal.add(Calendar.DAY_OF_YEAR, -180); // 180일 전 날자를 구함.
	     
	    String strDate = fm.format(cal.getTime());
	    String strDate2 = format.format(cal.getTime());
	    
	    //로그 경로를 파라미터로 받아서 처리하도록 변경 필요.
	    String[] dirPath ={
	    		"spbSvcWeb1/spb-admin/",
	    		"spbSvcWeb2/spb-admin/",
	    		"spbSvcWeb1/spb-user/",
	    		"spbSvcWeb2/spb-user/",
	    		"spbSvcApp1/spb-app/",
	    		"spbSvcApp2/spb-app/",
	    		"spbSvcApp1/spb-multi/",
	    		"spbSvcApp2/spb-multi/",
	    		"spbSvcControl1/spbServer/",
	    		"spbSvcControl2/spbServer/"
	   	};
	    
	    /**
	     * 조회할 서브 디렉토리, 톰캣 카탈리나 로그, 에러로그 , 서비스로그를 의미한다.
	     */
	    String[] subPath = {
	    		"catalina/",
	    		"error/",
	    		"service/"
	    };
	    
	    StringBuffer sb = new StringBuffer();
	    StringBuffer sb1 = new StringBuffer();
	    int result = 0;
	    for(String path:dirPath){
	    	for(String sub: subPath){
	    		sb.setLength(0);
		    	sb.append(logPath).append(path).append(sub);
		    	File searchDir = new File(sb.toString());
				if(searchDir.isDirectory()){
					String[] list = searchDir.list();
					for(String fileName : list){
						sb1.setLength(0);
						sb1.append(sb);
						if(fileName.indexOf(strDate) > 0 || fileName.indexOf(strDate2) >0){
							//해당 목록의 내용이 파일인지 디렉토리인지 비교하고 파일인 경우에만 삭제...
							sb1.append(fileName);
							File f = new File(sb1.toString());
							if(f.isFile()){
								logger.debug("delete Log : {}", sb1.toString());
								f.delete();
								result++;
							}
						} 
					}
					
				}
	    	}
	    	
	    }
		
	    logger.debug("****************[deleteLog scheduler End : {} 건 ]**********************", result);
		
		
	}
}
