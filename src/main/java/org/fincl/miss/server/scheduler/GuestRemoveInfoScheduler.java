/*
 * @Project Name : miss-service
 * @Package Name : org.fincl.miss.server.scheduler
 * @파일명          : GuestRemoveInfoScheduler.java
 * @작성일          : 2015. 8. 27.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 27.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.server.scheduler;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fincl.miss.server.scheduler.annotation.ClusterSynchronized;
import org.fincl.miss.server.scheduler.job.guest.GuestRemoveInfoMapper;
import org.fincl.miss.server.scheduler.job.refundDeposit.service.UsrRefundDepositMapper;
import org.fincl.miss.server.scheduler.job.sms.SmsMessageVO;
import org.fincl.miss.server.sms.SmsSender;
import org.fincl.miss.server.util.MainPayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mainpay.sdk.utils.ParseUtils;

/**
 * @파일명          : GuestRemoveInfoScheduler.java
 * @작성일          : 2015. 8. 27.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 27.   |   ymshin   |  최초작성
 */
@Component
public class GuestRemoveInfoScheduler {

	@Autowired
	private GuestRemoveInfoMapper guestRemoveInfoMapper;
	
	@Autowired
	private UsrRefundDepositMapper usrRefundDepositMapper;
	
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@ClusterSynchronized( jobToken="guestExpiredUseProc")
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED, rollbackFor={Exception.class, SQLException.class})
	public void guestExpiredUseProc() {
		
		
		logger.debug("******************************[미대여 건 자동 환불 START]******************************");
		List<HashMap<String, Object>> refundList = guestRemoveInfoMapper.chkGuestVoucherStataus();
		if(refundList.size() > 0) 
		{
			for(HashMap<String, Object> refund : refundList){
				
				MainPayUtil MainPayutil = new MainPayUtil();
				try 
				{
		        	HashMap<String, String> parameters = new HashMap<String, String>();
		        	
		        	
		        	String billkey = refund.get("PAYMENT_CONFM_NO").toString();
		    		if( billkey != null && !"".equals(billkey)) {	// 빌링키 없음 실패		
		    			parameters.put("orgRefNo", billkey);	// 정기결제 인증 키
		    		}
		    		//parameters.put("goodsId", "BIL_009");
		    		//parameters.put("goodsName", "추가과금");
		    		parameters.put("amount", refund.get("TOT_AMT").toString());
		    		parameters.put("orgTranDate", refund.get("PAY_DTTM").toString());
		    		parameters.put("clinetType", "MERCHANT");
		        	
		        	
		    		String responseJson = MainPayutil.cancel(parameters,"Y");
		    		
		    		
		    		
		    		Map responseMap = ParseUtils.fromJson(responseJson, Map.class);
					String resultCode = (String) responseMap.get("resultCode");
					String resultMessage = (String) responseMap.get("resultMessage");
				    
				    
					if(!"200".equals(resultCode)) 
					{	// API 호출 실패
						
						
						guestRemoveInfoMapper.addRefundHistFail(refund.get("PAYMENT_SEQ").toString());
						guestRemoveInfoMapper.setCancelInfo(refund.get("CANCLE_SEQ").toString());
						logger.debug("******************************[미대여 건 자동 환불 FAIL] CANCLE_SEQ = " + refund.get("CANCLE_SEQ").toString());
					}
					else
					{	// API 호출 성공
						try 
						{
							
							guestRemoveInfoMapper.addRefundHist(refund.get("PAYMENT_SEQ").toString());
							guestRemoveInfoMapper.setCancelInfo(refund.get("CANCLE_SEQ").toString());
							logger.debug("******************************[미대여 건 자동 환불 SUCCESS] CANCLE_SEQ = " + refund.get("CANCLE_SEQ").toString());
							
						} 
						catch (Exception e) 
						{
							
							guestRemoveInfoMapper.addRefundHistFail(refund.get("PAYMENT_SEQ").toString());
							guestRemoveInfoMapper.setCancelInfo(refund.get("CANCLE_SEQ").toString());
							logger.debug("******************************[미대여 건 자동 환불 FAIL 2] CANCLE_SEQ = " + refund.get("CANCLE_SEQ").toString());
						}
					}
		            
		        } 
				catch (Exception e) 
				{
		        	
		            guestRemoveInfoMapper.addRefundHistFail(refund.get("PAYMENT_SEQ").toString());
					guestRemoveInfoMapper.setCancelInfo(refund.get("CANCLE_SEQ").toString());
					logger.debug("******************************[미대여 건 자동 환불 FAIL 3] CANCLE_SEQ = " + refund.get("CANCLE_SEQ").toString());
		        }
				
			}
		  
		}
		logger.debug("******************************[미대여 건 자동 환불 END]******************************");
		
		
	}
	
	
}
