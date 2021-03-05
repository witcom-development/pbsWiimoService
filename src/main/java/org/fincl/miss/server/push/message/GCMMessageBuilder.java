/*
 * @Project Name : spb-app
 * @Package Name : com.dkitec.spb.util.push.message
 * @파일명          : GCMMessageBuilder.java
 * @작성일          : 2015. 7. 21.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 7. 21.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.server.push.message;
import org.fincl.miss.server.push.PushVO;
import org.fincl.miss.server.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.android.gcm.server.Message;

/**
 * @파일명          : GCMMessageBuilder.java
 * @작성일          : 2015. 7. 21.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 7. 21.   |   ymshin   |  최초작성
 */
public class GCMMessageBuilder implements MessageBuilder  {
	private volatile static GCMMessageBuilder instance = new GCMMessageBuilder();	
	protected static Logger log = LoggerFactory.getLogger(GCMMessageBuilder.class);
	private GCMMessageBuilder(){
		
	}
	
	public static GCMMessageBuilder getInstance() {
		return instance;
	}
	
	/**
	 * @location   : com.dkitec.spb.util.push.message.MessageBuilder.build
	 * @writeDay   : 2015. 7. 21. 오후 2:29:42
	 * @overridden : @see com.dkitec.spb.util.push.message.MessageBuilder#build(com.dkitec.spb.util.push.message.HashMap)
	 * @Todo       :
	 */ 
	@Override
	public Object build(PushVO entity) throws Exception {
		//String collapseKey = "collapseKey";
		log.debug("****************************메세지 빌드 안드로이드******************************");
		log.debug("************************"+entity.getMessage()+"************************");
		log.debug("****************************메세지 빌드 안드로이드******************************");
		boolean delayWhileIdle = false;
		//int ttl = 0;
		String messageid = StringUtil.isNull(entity.getMessageId());
		String encryptyn = StringUtil.isNull(entity.getEncryptYn());
		String url     = StringUtil.isNull(entity.getPushLinkUrl());
		String message = StringUtil.isNull(entity.getMessage());
		
		Message payload = new Message.Builder()
		                .collapseKey(String.valueOf(Math.random() % 100 + 1))
				        .delayWhileIdle(delayWhileIdle)
				        .timeToLive(1300)
				        .addData("messageid", messageid)
				        .addData("encryptyn", encryptyn)
				        .addData("url", url)
				        .addData("message", message)
				        .build();
        return payload;
	}

}
