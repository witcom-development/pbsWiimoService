/*
 * @Project Name : spb-app
 * @Package Name : com.dkitec.spb.util.push
 * @파일명          : Sender.java
 * @작성일          : 2015. 7. 21.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 7. 21.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.server.push;

import java.util.List;

/**
 * @파일명          : Sender.java
 * @작성일          : 2015. 7. 21.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 7. 21.   |   ymshin   |  최초작성
 */
public interface Send {
	public void send(PushVO pushVo, Object obj) throws Exception;
	public void send(PushVO pushVo, List<String> regIds,Object obj) throws Exception;
}
