package org.fincl.miss.server.sms;

import org.fincl.miss.server.scheduler.job.sms.SmsMessageVO;

public enum SendType {

	DEFAULT(""),
	SMS_001("SMS_001"), //회원가입
	SMS_002("SMS_002"), //회원탈퇴
	SMS_003("SMS_003"), //대여안내
	SMS_004("SMS_004"), //반납안내
	SMS_005("SMS_005"), //대여종료 이전 안내
	SMS_006("SMS_006"), //대여종료 초과안내 - 추가요금 발생안내
	SMS_007("SMS_007"), //최대 대여시간 초과안내 - 장기미반납
	SMS_008("SMS_008"), //추가요금 자동결재
	SMS_009("SMS_009"), //미납요금 안내 - 대여불가
	SMS_010("SMS_010"), //강제회원탈퇴 안내
	SMS_011("SMS_011"), //회원확인
	SMS_012("SMS_012"), //일일권 구매 - 비회원
	SMS_013("SMS_013"), //강제 반납
	SMS_014("SMS_014"), //예약 취소
	SMS_015("SMS_015"), //회원재동의자 안내
	SMS_019("SMS_019"), //정기권종료 이전안내_20161109_JJH
	SMS_024("SMS_024"), //정차 자전거 주소정보 전송 (관리자 전용)
	SMS_025("SMS_025"), //정차 자전거 주소정보 변환안됨 (관리자 전용)
	SMS_026("SMS_026"), //정차 자전거 정보 전송 (사용자 전용)
	SMS_028("SMS_028"),
	SMS_081("SMS_081"), //qr자전거 반납
	SMS_083("SMS_083"), //LCD자전거대여
	SMS_090("SMS_090"), //qr자전거 반납
	SMS_093("SMS_093"), //SESSAK 따릉이 이탈 안내
	SMS_094("SMS_094"), //SESSAK 따릉이 대여 안내
	SMS_096("SMS_096"), //SESSAK 따릉이 대여 안내
	SMS_098("SMS_098"), //임시폐쇄 예정 안내
	SMS_100("SMS_100"); //새싹 따릉이 반납
	
	private String code;
	
	public String toString(){
		return SmsSender.getMsg(code);
	}
	
	public String getCode() {
		return code;
	}

	private SendType(String code){
		this.code = code;
	}
}
