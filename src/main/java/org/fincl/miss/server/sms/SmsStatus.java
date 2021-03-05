package org.fincl.miss.server.sms;

public class SmsStatus {

	
	public enum Status{
		
		BEFORE_READ_CD(100){
			public String message(){
				return "센터 서버로 메시지 보내기 전 송신대기";
			}
		},
		AFTER_READ_CD(101){
			public String message(){
				return "DB에서 Agent가 메모리로 읽어 들임";
			}
		},
		DELIVER_ACK_CD(102){
			public String message(){
				return "센터 서버로 메시지 전달성공, 결과대기";
			}
		},
		E_OK(0){
			public String message(){
				return "DELIVER_ACK 성공";
			}
		},
		E_SYSFAIL(1){
			public String message(){
				return "시스템 장애";
			}
		},
		E_AUTH_FAIL(2){
			public String message(){
				return "인증실패(엔진 내부적으로 사용)";
			}
		},
		E_FORMAT_ERR(3){
			public String message(){
				return "메시지 형식 오류";
			}
		},
		E_DUPL_CONN(4){
			public String message(){
				return "중복 접속 요청";
			}
		},
		E_AGT_NO_SMSNUM(5){
			public String message(){
				return "Agent 번호의 형식이 잘못됨";
			}
		},
		E_AGT_REQ_LIMIT(17){
			public String message(){
				return "Agent 전송 유효 시간 초과 (Default: 24h)";
			}
		},
		E_AGT_REJECT_TIME(18){
			public String message(){
				return "Agent 타임 블록 Fail 처리";
			}
		},
		E_AGT_SPAM_ERR(19){
			public String message(){
				return "Agent 스팸 테이블 처리";
			}
		},
		E_DAY_LIMIT_COUNT(50){
			public String message(){
				return "1일 제한 건수를 넘어서는 메시지 전송건수(사업부에 연락하여 제한을 풀면 전송 가능함)";
			}
		},
		E_TOT_LIMIT_COUNT(51){
			public String message(){
				return "총 메시지 전송건수 제약을 넘어서 메시지 송신함(사업부에 연락하여 제한을 풀면 전송 가능함)";
			}
		},
		E_SPAM_WORD(52){
			public String message(){
				return "릴레이(센터)서버에서 판단할 때 메시지 내용 중에 SPAM단어가 있음.";
			}
		},
		E_SPAM_TELNO(53){
			public String message(){
				return "릴레이(센터)서버에서 판단할 콜백 번호 또는 메시지수신 번호가 스팸 번호임.";
			}
		},
		E_SPAM_WORD_TELNO(54){
			public String message(){
				return "내용과 전화번호를 조합한 결과 스팸으로 간주";
			}
		},
		E_BILL_LIMIT_OVER(55){
			public String message(){
				return "잔액 부족";
			}
		},
		E_NUM_NO_NULL(56){
			public String message(){
				return "착 발신 번호 중에 공백 있는 에러";
			}
		},
		E_INDEX_NO_DUP(70){
			public String message(){
				return "인덱스 값 중복";
			}
		},
		E_GW_REJECT_TIME(71){
			public String message(){
				return "센터에서 금지시간 거절";
			}
		},
		E_TIMEOUT_MT(91){
			public String message(){
				return "(이통사) DELIVER_ACK을 받지 못한 경우";
			}
		},
		E_TIMEOUT_RP(92){
			public String message(){
				return "(이통사) REPORT를 못 받은 경우";
			}
		},
		E_DELIVER_UNKNOWN_ACK(93){
			public String message(){
				return "DELIVER Unknown ACK";

			}
		};

		private int value;
		
		public abstract String message();
		
		public int getValue(){
			return value;
		}
		private Status(int value){
			this.value = value;
		}
	}
	
	public enum SmsResult{
		
		E_SMS_SEND(6){
			public String message(){
				return "휴대폰으로 SMS 전송성공";
			}
		},
		E_SMS_NOUSE(7){
			public String message(){
				return "비가입자, 결번, 서비스정지";
			}
		},
		E_SMS_POWEROFF(8){
			public String message(){
				return "Power-off";
			}
		},
		E_SMS_SHADE(9){
			public String message(){
				return "음영 지역";
			}
		},
		E_SMS_MSG_FULL(10){
			public String message(){
				return "단말기 메시지 Full";
			}
		},
		E_SMS_ETC(11){
			public String message(){
				return "기타 실패";
			}
		},
		E_SMS_SPAM(12){
			public String message(){
				return "스팸 처리";
			}
		},
		E_SMS_PORTED_OUT(13){
			public String message(){
				return "번호 이동된 가입자";
			}
		},
		E_SMS_FWD(14){
			public String message(){
				return "메시지 착신전환 회수 초과";
			}
		},
		E_SMS_FOWARD(15){
			public String message(){
				return "Forward 된 가입자";
			}
		},
		E_SMS_NYPD_SYNC(16){
			public String message(){
				return "타사 NPDB와 자사 HLR정보 불일치";
			}
		},
		E_SMS_PAUSE(17){
			public String message(){
				return "서비스 일시 정지";
			}
		}, 
		E_SMS_REFUSE(18){
			public String message(){
				return "착신 거부";
			}
		},
		E_SMS_ABANDONED(40){
			public String message(){
				return "전송 실패 (무선망 단)";
			}
		},
		E_SMS_EXPIRED(41){
			public String message(){
				return "전송 실패 (무선망  단말기) – 콜백KTF";
			}
		},
		E_SMS_DELETED(45){
			public String message(){
				return "메시지 삭제됨 – 콜백KTF";
			}
		},
		E_SMS_UNKNOWN(90){
			public String message(){
				return "메시지 타입오류 – 콜백KTF";
			}
		};

		private int value;
		
		public abstract String message();
		
		public int getValue(){
			return value;
		}
		private SmsResult(int value){
			this.value = value;
		}
	}
	
	public enum MmsResult{
		
		E_MMS_SEND(1000){
			public String message(){
				return "휴대폰으로 MMS 전송성공";
			}
		},
		E_MMS_PARTOK(1100){
			public String message(){
				return "부분 성공";
			}
		},
		E_MMS_FORMAT_ERR(2000){
			public String message(){
				return "포멧 관련 알 수 없는 오류";
			}
		},
		E_MMS_ADDR_ERR(2001){
			public String message(){
				return "주소(포맷) 에러";
			}
		},
		E_MMS_CONTENTLENTH_ERR(2002){
			public String message(){
				return "Content 개체 오류";
			}
		},
		E_MMS_MIME_ERR(2003){
			public String message(){
				return "MIME 형식 오류"; 
			}
		},
		E_MMS_MSGID_ERR(2004){
			public String message(){
				return "MessageID,Type,TransactionID오류,중복,부재";
			}
		},
		E_MMS_HEAD_ERR(2005){
			public String message(){
				return "Head 내 각 필드의 부적절";
			}
		},
		E_MMS_BODY_ERR(2006){
			public String message(){
				return "Body 내 각 필드의 부적절";
			}
		},
		E_MMS_MEDIA_ERR(2007){
			public String message(){
				return "지원하지 않는 미디어 존재";
			}
		},
		E_MMS_NOUSE_ERR(2008){
			public String message(){
				return "비가입자, 결번, 서비스정지";
			}
		},
		E_MMS_SENDER_ERR(2009){
			public String message(){
				return "발신자정보오류";
			}
		},
		E_MMS_NOMMSPHONE(3000){
			public String message(){
				return "MMS 미지원 단말기";
			}
		},
		E_MMS_MSG_FULL(3001){
			public String message(){
				return "단말 수신용량 초과";
			}
		},
		E_MMS_EXPIRED(3002){
			public String message(){
				return "전송 시간 초과, 재전송 실패";
			}
		},
		E_MMS_OVERFLOWER(3003){
			public String message(){
				return "센터서버 수신용량 초과";
			}
		},
		E_MMS_POWEROFF(3004){
			public String message(){
				return "전원 꺼짐";
			}
		},
		E_MMS_SHADE(3005){
			public String message(){
				return "음영 지역";
			}
		},
		E_MMS_ETC(3006){
			public String message(){
				return "기타";
			}
		},
		E_MMS_SYS_FAIL(4000){
			public String message(){
				return "센터처리실패(미지원단말,수신자일시정지등)";
			}
		},
		E_MMS_AUTH_FAIL(4001){
			public String message(){
				return "가입자 MMS사용불가상태";
			}
		},
		E_MMS_NET_FAIL(4002){
			public String message(){
				return "네트워크 에러";
			}
		},
		E_MMS_BUSY_FAIL(4003){
			public String message(){
				return "일시적인서비스에러 (단말기off, 단말기무응답)";
			}
		},
		E_MMS_TERM_SPAM(4004){
			public String message(){
				return "착신거부 및 스팸처리";
			}
		},
		E_MMS_CENTER_SPAM(4005){
			public String message(){
				return "센터 스팸처리 및 중복요청";
			}
		},
		E_MMS_PORTED(5000){
			public String message(){
				return "번호이동 된 가입자";
			}
		};

		private int value;
		
		public abstract String message();
		
		public int getValue(){
			return value;
		}
		private MmsResult(int value){
			this.value = value;
		}
		
			
	}
	
}
