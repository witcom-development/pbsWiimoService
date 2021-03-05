/*
 * @Project Name : spb-app
 * @Package Name : com.dkitec.spb.util.push
 * @파일명          : PushType.java
 * @작성일          : 2015. 8. 4.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 4.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.server.push;

/**
 * @파일명          : PushType.java
 * @작성일          : 2015. 8. 4.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 4.   |   ymshin   |  최초작성
 */
public enum PushType {
	DEFAULT(""),
	PUSH_001("push.msg.warnning"), //반납 10분전 경고.
	PUSH_002("push.msg.return"); //회원탈퇴
	
	
	private String code;
	
	public String getCode() {
		return code;
	}

	private PushType(String code){
		this.code = code;
	}
}
	
