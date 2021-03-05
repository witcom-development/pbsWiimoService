/*
 * @Project Name : spb-app
 * @Package Name : com.dkitec.spb.util.push
 * @파일명          : PushTest.java
 * @작성일          : 2015. 8. 3.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 3.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.server.push;

import org.fincl.miss.server.push.apns.APNSPushSender;
import org.fincl.miss.server.push.gcm.GCMPushSender;
import org.fincl.miss.server.push.message.APNSMessageBuilder;
import org.fincl.miss.server.push.message.GCMMessageBuilder;
import org.fincl.miss.server.util.IConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @파일명          : PushTest.java
 * @작성일          : 2015. 8. 3.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 3.   |   ymshin   |  최초작성
 */
public class PushSendProc {
	protected static Logger log = LoggerFactory.getLogger(GCMPushSender.class);
	
	public static int exePush(PushVO pushVo) throws Exception {
		log.debug("*************반납 종료 10분전 알림 배치Push ***********************");
		/*PushVO pVo = new PushVO();
		pVo.setPushType("android");
		pVo.setMessage("테슷흐...");
		//String m = super.getMessage(PushType.PUSH_001.getCode());
		pVo.setTokenId("APA91bE4CKJcwi_NC-ueh--4pbcRl6-kf4kNsSHBuMsLFog_yAYgmX4HGpmSjn0ZH_I8QWpAnbq-Q79oQucX7tSG4X-MKsesoyhymGTyRZjuO3xn62f5rFPrGFZ0y29prwf8LJfo5rV7");
	*/	
		PushManager manager = null;
		if(pushVo.getPushType().equals(IConstants.PUSH_TYPE_GCM)) {
			manager =  PushManager.getInstance();
			if(pushVo.getTokenList() != null && pushVo.getTokenList().size() > 0) {
				log.debug("*************push 스타트 GCM*************************");
				GCMPushSender sender =  (GCMPushSender)manager.getSender(pushVo);
				GCMMessageBuilder msgBuilder =(GCMMessageBuilder)manager.getBuilder(pushVo);
				
				Object msg;
				try {
					msg = msgBuilder.build(pushVo);
					sender.send(pushVo,pushVo.getTokenList(), msg);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} 
		} else {
			if(pushVo.getTokenList() != null && pushVo.getTokenList().size() > 0) {
				log.debug("*************push 스타트 APNS*************************");
				manager =  PushManager.getInstance();
				APNSPushSender sender = (APNSPushSender)manager.getSender(pushVo);
				APNSMessageBuilder msgBuilder = (APNSMessageBuilder)manager.getBuilder(pushVo);
				
				Object msg;
				try {
					msg = msgBuilder.build(pushVo);
					sender.send(pushVo,pushVo.getTokenList(), msg);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} 
		}
		return 1;
	}
}
