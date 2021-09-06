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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fincl.miss.server.scheduler.annotation.ClusterSynchronized;
import org.fincl.miss.server.scheduler.job.guest.GuestRemoveInfoMapper;
import org.fincl.miss.server.scheduler.job.overFeePayScheuler.vo.OverFeeVO;
import org.fincl.miss.server.scheduler.job.refundDeposit.service.UsrRefundDepositMapper;
import org.fincl.miss.server.scheduler.job.sms.SmsMessageVO;
import org.fincl.miss.server.sms.SmsSender;
import org.fincl.miss.server.util.MainPayUtil;
import org.fincl.miss.server.scheduler.job.guest.vo.*;
import org.fincl.miss.server.scheduler.job.overFeePayScheuler.vo.OverFeeVO;
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
							guestRemoveInfoMapper.setVoucherUseComplete(refund.get("VOUCHER_SEQ").toString());
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
		
		
		
		logger.debug("******************************[반납 실패 자전거 조회 START]******************************");
		List<HashMap<String, Object>> InvalidReturnList = guestRemoveInfoMapper.chkRentInvalidReturnOver();
		if(InvalidReturnList.size() > 0) 
		{
			for(HashMap<String, Object> InvalidRent : InvalidReturnList)
			{
			
				MainPayUtil MainPayutil = new MainPayUtil();
				
				try{
					
					int req_sec = Integer.valueOf(InvalidRent.get("REQ_SEC").toString());
					
					logger.debug("[반납 요청 시간 경과  ] ="+ InvalidRent.get("END_DTTM") +", 경과 시간 = " + req_sec + "초" );
					
			//		if(Integer.valueOf(String.valueOf(InvalidRent.get("REQ_MI").toString()) > 0)
					//강제반납 (반납버튼 누른후) 50초로 늘림.
					if(req_sec >= 50)
					{
						logger.debug("[강제 반납 처리 진행 : USRSEQ :"+InvalidRent.get("USR_SEQ")+" DEV_ID="+InvalidRent.get("RENT_BIKE_ID") +" , 강제 반납 처리 !!!!!!!!!");
						RentHistVo info = guestRemoveInfoMapper.getForReturnUse(InvalidRent.get("RENT_BIKE_ID").toString());
						

						Map<String, Object> return_GPS = guestRemoveInfoMapper.getBikeRETURN_GPS(info.getRENT_SEQ());
						String latitude = String.valueOf(return_GPS.get("BIKE_LATITUDE"));
				        String longitude = String.valueOf(return_GPS.get("BIKE_LONGITUDE"));
				        
				        info.setGPS_X(latitude);
			    		info.setGPS_Y(longitude);
			    		
			    		logger.debug("GPS INFO2 ##### => : {} , {} ",String.valueOf(latitude),String.valueOf(longitude));
				        
				        
				        Map<String, String> GPS = new HashMap<String, String>();
						GPS.put("BIKE_LATITUDE", String.valueOf(latitude));
						GPS.put("BIKE_LONGITUDE", String.valueOf(longitude));
						GPS.put("BIKE_ID", String.valueOf(info.getRENT_BIKE_ID()));
						Map<String, Object> stationInfo = guestRemoveInfoMapper.CheckStation_ForGPS(GPS);
						
						if(stationInfo == null )
						{
							logger.error("stationInfo is NULL : beconID is not GPS : DEFAULT STATION INSERT" );
							info.setRETURN_STATION_ID("ST-999");
			     		
			     			if(info.getBIKE_SE_CD().equals("BIK_001"))
			     			{
			     				info.setRETURN_RACK_ID("45800099900000");
			     			}
			     			else
			     				info.setRETURN_RACK_ID("45800099900099");
						}
						else
						{
							logger.debug("##### BIKE RETURN ## GPS STATION FIND START! #####");
							logger.debug("##### BIKE RETURN ## GPS STATION ID[" + String.valueOf(stationInfo.get("STATION_ID")) + "] ENTERED");
			     			info.setRETURN_STATION_ID(stationInfo.get("STATION_ID").toString());
			     			info.setRETURN_RACK_ID(stationInfo.get("RACK_ID").toString());
				 
						}
						info.setCASCADE_YN("N");
						
						
						int sysTime = Integer.parseInt(info.getUSE_MI().toString());
			        	
			        	info.setTRANSFER_YN("N");
			        	info.setOVER_FEE_YN("N");
			        	
			         	Integer tem_USE_SEQ = 0;
			         	
			         	Map<String, String> GPS_DATA = new HashMap<String, String>();
			         	GPS_DATA.put("RENT_SEQ", info.getRENT_SEQ());
			         	
			         	double ACC_DIST = 0.0;
			         	
			         	Map<String, Object> bikeData = guestRemoveInfoMapper.getBikeMoveDist_COUNT(GPS_DATA);
			         	
						if(bikeData != null)
						{
							tem_USE_SEQ = Integer.valueOf(String.valueOf(bikeData.get("USE_SEQ")));
							int USE_SEQ = tem_USE_SEQ;
							ACC_DIST = new Double(bikeData.get("ACC_DIST").toString()); //현재까지 누적 데이터 db에서 추출
			        		logger.debug("getBikeMoveDist_COUNT BICYCLE_ID {}, USE_SEQ {}" ,info.getRENT_BIKE_ID(), USE_SEQ);
			        		
			        		GPS_DATA.put("BIKE_LATITUDE", String.valueOf(info.getGPS_X()));
				         	GPS_DATA.put("BIKE_LONGITUDE", String.valueOf(info.getGPS_Y()));
			        		
				         	double dlatp =  Double.parseDouble( info.getGPS_X());
				         	double dlonp = Double.parseDouble( info.getGPS_Y());
			        		if(dlatp != 0.0 && dlonp != 0.0)
			        		{
			        			double USE_DIST = guestRemoveInfoMapper.getBikeMoveDist_Last(GPS_DATA);  //db 데이터 최종과 현재 첫번째 데이터와 비교
			        			ACC_DIST += USE_DIST;
			        		}
			        		logger.debug("GPS DISTANCE LAST DB ACC_DIST {} ", ACC_DIST);
			        		
			        		info.setUSE_DIST(ACC_DIST + "");
			        	}
						else
							info.setUSE_DIST("0");	//gps 로 거리 하는 함수 막음.
			         	

			        	info.setUSE_MI(sysTime+"");
			        	
			        	int weight = guestRemoveInfoMapper.getUserWeight(info.getUSR_SEQ());
			        	
			        	double co2 = (((double)ACC_DIST/1000)*0.232);
			        	double cal = 5.94 * (weight==0?65:weight) *((double)ACC_DIST/1000) / 15;
			        	

			        	info.setREDUCE_CO2(co2+"");
			        	info.setCONSUME_CAL(cal+"");
			        	int baseRentTime = 0;
			        	
			        	logger.debug(" ##### [scheduler] server_time is baseRentTime {}  sysTime {}"  , baseRentTime, sysTime);
			    		
			    		
						Map<String, Object> fee = new HashMap<String, Object>();
						
						fee.put("ADD_FEE_CLS", info.getPAYMENT_CLS_CD());
						fee.put("BIKE_SE_CD", info.getBIKE_SE_CD());
						
						Map<String, Object> minPolicy = guestRemoveInfoMapper.getOverFeeMinPolicy(fee);	//TB_SVC_ADD_FEE  
						baseRentTime = Integer.parseInt(minPolicy.get("OVER_STR_MI").toString());
						
						if(sysTime - baseRentTime > 0)
						{
				//			info.setOVER_FEE_YN("Y");		
							int overPay = getPay(minPolicy, (sysTime- baseRentTime));
							
							if(overPay > 0)	// 2021.09.06 추가  0 일 경우 있음.
							{
								info.setOVER_FEE_YN("Y");		
								
								info.setOVER_FEE(overPay+"");
								baseRentTime = Integer.parseInt(minPolicy.get("OVER_STR_MI").toString());
								info.setOVER_MI(String.valueOf(sysTime - baseRentTime));
							}
							else
							{
								logger.debug("ADD_OVER_FEE baseTime {} , sysTime {} overPay {} is NO_OVER_FEE",baseRentTime,sysTime,overPay);
								
							}
							
						}
						// 반납 프로세스 실행
						guestRemoveInfoMapper.parkingInfoDelete(info.getRENT_BIKE_ID());
			    		
						//반납시에 일어나는 과정 
						//이용시간 관련 과금 진행 , 과금 관련 문자 발송.
						// 이력, 파킹정보는 대여시 정보로 파킹
						//자전거 상태 BKS_012 
						
						//TB_SVC_ENFRC_RETURN_HIST 에 넣어주기 
						//rent_테이블 삭제 
						//RentHistVo info = new RentHistVo();
						procReturn(info);
					}
				}
				catch (Exception e) 
				{
					logger.debug("******************************[반납 실패 자전거  FAIL 3] " );
		        }
			}//for
		}
		logger.debug("******************************[반납 실패 자전거 조회 END]******************************");
		
		
	}
	
	public void procReturn(RentHistVo info) 
	{
		SimpleDateFormat _sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat _sdfh = new SimpleDateFormat("HH");
		Date today = new Date();
		String rentSeq = "";
		boolean b_overfee = false;

		logger.debug("QR_enforce_procReturn :: {}", info); // 로그 수정....2018.04.02

		// 자전거 주차 정보 INSERT PARKING
		
		try 
		{
			guestRemoveInfoMapper.insertParkingInfo(info);
		} 
		catch (Exception e) 
		{
		}
		

		// 자전거 과거 배치 이력 UPDATE LOCATION_BIKE
		guestRemoveInfoMapper.updateBikeLocation(info);

		// 자전거 배치 이력 INSERT LOCATION_BIKE
		guestRemoveInfoMapper.insertBikeLocation(info);

		// 자전거 정보 UPDATE BIKE
		guestRemoveInfoMapper.updateBike_012(info);

		guestRemoveInfoMapper.updateBikeGPS(info);
	
		rentSeq = String.valueOf(guestRemoveInfoMapper.getRentSeq(info));

		if (rentSeq != null && rentSeq != "") 
		{
			info.setRENT_SEQ(rentSeq);
		}

		// 대여 이력 INSERT RENT_HIST
		guestRemoveInfoMapper.insertRentHist(info);

		Map<String, String> GPS_DATA = new HashMap<String, String>();
		GPS_DATA.put("RENT_SEQ", rentSeq);
		GPS_DATA.put("RENT_HIST_SEQ", info.getRENT_HIST_SEQ());
		//bicycleMapper.insertRentMove_Info(GPS_DATA);
		
		guestRemoveInfoMapper.deleteRentGPSDATA(info);

		// 대여 예약도 DELET RENT
		guestRemoveInfoMapper.deleteRentInfo_rserved(info);
		OverFeeVO fee = new OverFeeVO();

		// 대여 초과 요금 여부 확인 INSERT RENT_OVER_FEE
		if (info.getOVER_FEE_YN().equals("Y")) 
		{
			guestRemoveInfoMapper.insertRentOverFee(info); 
			
			MainPayUtil MainPayutil = new MainPayUtil();
			fee = guestRemoveInfoMapper.getOverFeeRETURN(info.getUSR_SEQ());
			HashMap<String, String> parameters = new HashMap<String, String>();
			String billkey = fee.getBillingKey() ;
    		if( billkey != null && !"".equals(billkey)) {	// 빌링키 없음 실패		
    			parameters.put("billkey", billkey);	// 정기결제 인증 키
    		}
    		parameters.put("goodsId", "BIL_009");
    		parameters.put("goodsName", "추가과금");
    		parameters.put("amount", fee.getOverFee());
        	
        	
    		String responseJson = MainPayutil.approve(parameters,"Y");
    		
    		Map responseMap = ParseUtils.fromJson(responseJson, Map.class);
			String resultCode = (String) responseMap.get("resultCode");
			String resultMessage = (String) responseMap.get("resultMessage");
		    
			if(!"200".equals(resultCode)) {	// API 호출 실패
				logger.debug("Return Pay Fail-->> ["+resultMessage + "]");
				guestRemoveInfoMapper.setOverFeePayReset(fee);
			}
			else
			{	// API 호출 성공
				try 
				{
					fee.setPaymentMethodCd("BIM_001");
					fee.setResultCD("0000");
					fee.setPaymentStusCd("BIS_001");
					fee.setMb_serial_no(parameters.get("mbrRefNo"));
					Map dataMap = (Map)responseMap.get("data");
					fee.setPaymentConfmNo((String) dataMap.get("refNo"));
					fee.setTotAmt(fee.getOverFee());
					fee.setOrder_certify_key((String)responseMap.get("applNo"));
					fee.setProcessReasonDesc(resultMessage);
					
					fee.setRentHistSeq(info.getRENT_HIST_SEQ());	//add rent_hist_seq by dearkim
					
					Map<String, String> returnMap = new HashMap<String, String>();
					
					returnMap = guestRemoveInfoMapper.getPaymentInfoExist(fee);
					
					logger.debug("check-->> " +returnMap.get("PAYMENT_INFO_EXIST"));
					
					if(returnMap.get("PAYMENT_INFO_EXIST").equals("N"))
					{
						logger.debug("##### 초과요금 결제정보가 없다. insert payment_seq ##### {}",fee.getRentHistSeq());
						
						int result = guestRemoveInfoMapper.addTicketPayment(fee);
					}else{
						logger.debug("##### 초과요금 결제정보가 이미 있다. #####");
					}
					
					b_overfee = true;
					
					int result = guestRemoveInfoMapper.setOverFeePayComplete(fee);
					
				} 
				catch (Exception e)
				{
				}
			}
		}


		info.setMILEAGE_CD("MIG_003");
		info.setMILEAGE_DAY_CD("MSI_030");
		info.setMILEAGE_DAY_MAX_CD("MSI_031");
		info.setMILEAGE_POLICY_OPEN_CD("MSI_032");
		info.setMILEAGE_POLICY_NM("절감탄소량");

		/**
		 * 상태미전송 오류처리를 위해 반납/관리자 설치시에도 상태보고 시간 필드를 업데이트하는 기능 추가.
		 */
		guestRemoveInfoMapper.updateDeviceState(info);
		guestRemoveInfoMapper.deleteRentInfo(info);		//2021.05.10 rent 지우기 문자전으로 이동
		guestRemoveInfoMapper.addENFRC_RETURN(info);
		// SMS전송.

		String returnStationNo = String.valueOf(guestRemoveInfoMapper.getStationNo(String.valueOf(info.getRETURN_RACK_ID()))); // 대여소
		// 번호가져오기_20161121_JJH
		logger.debug("#### SMS_MESSAGE ==>station {} state {} " , returnStationNo,info.getSTATION_USE_YN());

	
		if (info.getUSR_MPN_NO() != null && !info.getUSR_MPN_NO().equals(""))
		{
			try 
			{
				SmsMessageVO smsVo = new SmsMessageVO();
				smsVo.setDestno(info.getUSR_MPN_NO());
				smsVo.setMsg("반납되었습니다");
				
				if(b_overfee == false)
				{
					if(info.getBIKE_SE_CD().equals("BIK_001"))
					{
						smsVo.setMsg("<위고> " + info.getBIKE_NO() +" 자전거 반납완료. 이용시간은 "+ info.getUSE_MI() + "분 입니다.");
					}
					else
					{
						smsVo.setMsg("<위고> " + info.getBIKE_NO() +" 킥보드 반납완료. 이용시간은 "+ info.getUSE_MI() + "분 입니다.");
					}
				}
				else
					
				{
					smsVo.setDestno(info.getUSR_MPN_NO());
					if(info.getBIKE_SE_CD().equals("BIK_001"))
					{
						smsVo.setMsg("<위고> " + info.getBIKE_NO() +" 자전거  "+ info.getUSE_MI() + "분이용 반납완료. 초과이용은 " + fee.getOverMi() + "분으로 " +  fee.getOverFee() + "원 결제완료");
					}
					else
					{
						smsVo.setMsg("<위고> " + info.getBIKE_NO() +" 킥보드  "+ info.getUSE_MI() + "분이용 반납완료. 초과이용은 " + fee.getOverMi() + "분으로 " + fee.getOverFee() + "원 결제완료");
					}
				}
				SmsSender.sender(smsVo);
			} 
			catch (Exception e) 
			{

			}
		}
	}
	
	
	public int getPay(Map<String, Object> min, int overFee)
	{
		int over = overFee;
	    int pay = 0;
	    	
	    int minStr = Integer.parseInt(min.get("OVER_STR_MI").toString());
	    int minEnd = Integer.parseInt(min.get("OVER_END_MI").toString());
	    int minPay = Integer.parseInt(min.get("ADD_FEE").toString());
	    	
	    String tmp = min.get("ADD_FEE_INTER_MI")==null?"30":min.get("ADD_FEE_INTER_MI").toString();
	    int intervalMin = Integer.parseInt(tmp);
	    	
	    logger.debug(" getPay overtime {} minStr {} minPay {} maxStr {} maxPay {}" , overFee,minStr,minPay);	//log 수정 
	    	
		/**
		* 기본초과요금 부과
		*/
		if(overFee - (minStr-1)>0)
		{
			/**
			* 추과요금 부과
			*/
			//int overTime = overFee;
			if(overFee > 0)
			{
				// 추과 요금 부과시간
				int payCount = overFee/intervalMin;
				if(overFee%intervalMin>0)
				{
					payCount++;
				}
				pay = (minPay * payCount);
				logger.debug("기본 초과 + 추가초과 요금 : getPay_base_fee+overfee {}" , pay);	//log 수정 
			}
			else
			{
				/**
				 * 기본요금만 부과.
				 */
				pay = minPay;
				logger.debug("기본 초과  getPay_overfee : {}", pay);	//수정 
			}
	    		
		}
		else
		{
			logger.debug("초과요금 없음  getPay no_overfee");		//수정 
		}
		return pay;
	}
}
