/*
 * @Project Name : miss-service
 * @Package Name : org.fincl.miss.server.push.message
 * @파일명          : MessageBuilder.java
 * @작성일          : 2015. 8. 21.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 21.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.server.push.message;

import org.fincl.miss.server.push.PushVO;


/**
 * @파일명          : MessageBuilder.java
 * @작성일          : 2015. 8. 21.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 21.   |   ymshin   |  최초작성
 */
public interface MessageBuilder {
	public Object build(PushVO unit) throws Exception;
}
