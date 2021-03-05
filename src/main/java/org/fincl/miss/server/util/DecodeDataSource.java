package org.fincl.miss.server.util;


/**
 * 암호화된 접속 계정 정보를 처리하는 데이터베이스 풀 클래스
 */
public class DecodeDataSource extends org.apache.commons.dbcp.BasicDataSource {

	/**
	 * 암호화된 사용자 계정을 지정하는 함수
	 * @param username 암호화된 사용자 계정
	 */
	@Override
	public void setUsername(String username) {

		try {
			super.setUsername(AES256anicar.decrypt(username));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 암호화된 비밀번호를 지정하는 함수
	 * @param password 암호화된 비밀번호
	 */
	@Override
	public void setPassword(String password) {

		try {
			super.setPassword(AES256anicar.decrypt(password));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
