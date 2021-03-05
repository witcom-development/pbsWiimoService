package org.fincl.miss.server.exeption;

public class ErrorConstant {
    
    /**
     * 성공
     */
    static public String SUCCESS = "00000000";
    
    /**
     * 서비스등록기 공통 오류코드
     */
    static public String SERVICE_REGISTER_ERROR = "10000000";
    
    /**
     * 서비스등록기 설정파일 오류코드
     */
    static public String SERVICE_REGISTER_FILE_NOT_FOUND = "10000001";
    
    /**
     * 서비스등록기 클래스설정 오류코드
     */
    static public String SERVICE_REGISTER_CLASS_NOT_FOUND = "10000002";
    
    /**
     * 서비스 Invoke 공통 오류코드
     */
    static public String SERVICE_INVOKE_ERROR = "10000010";
    
    /**
     * 등록되어 있지 않은 서비스 요청 시 오류코드
     */
    static public String NOT_REGISTERED_SERVICE_ERROR = "10000011";
    
    /**
     * ServiceId 필드 누락 오류코드
     */
    static public String SERVICE_ID_ISNULL = "10000012";
    
    /**
     * 사용하지 않음으로 설정된 서비스 요청 시 오류 코드
     */
    static public String UNUSABLE_SERVICE_ERROR = "10000013";
    
    /**
     * 사용할 수 없는 시간대에 서비스 요청 시 오류 코드
     */
    static public String UNSERVICEABLE_TIME_ERROR = "10000014";
    
    /**
     * Simple Tcp Client 접속 오류 코드
     */
    static public String BIZ_SERVICE_TCP_CONNECTION_ERROR = "10000020";
    
    /**
     * Simple Tcp Client 접속종료 오류 코드
     */
    static public String BIZ_SERVICE_TCP_CLOSE_ERROR = "10000021";
    
    /**
     * Simple Tcp Client 메시지 전송 오류 코드
     */
    static public String BIZ_SERVICE_TCP_SENDMESSAGE_ERROR = "10000022";
    
    /**
     * 메시지 Header 공통 오류코드
     */
    static public String MESSAGE_HEADER_ERROR = "10000030";
    
    /**
     * 메시지 Parser 공통 오류코드
     */
    static public String MESSAGE_PARSER_ERROR = "10000031";
    
    /**
     * 메시지 Parser 미지원 기능 오류코드
     */
    static public String MESSAGE_PARSER_NOT_SUPPORTED = "10000032";
    
    /**
     * 메시지 Parser ServiceId 누락 오류코드
     */
    static public String MESSAGE_PARSER_NOT_FOUND_SERVICE_ID = "10000033";
    
    /**
     * Form 메시지 Parse 오류코드
     */
    static public String FORM_MESSAGE_PARSE_ERROR = "10000034";
    
    /**
     * Json 메세지 Parse 오류코드
     */
    static public String JSON_MESSAGE_PARSE_ERROR = "10000035";
    
    /**
     * XML 메세지 Parse 오류코드
     */
    static public String XML_MESSAGE_PARSE_ERROR = "10000036";
    
    /**
     * Echo 메세지 Parse 오류코드
     */
    static public String ECHO_MESSAGE_PARSE_ERROR = "10000037";
    
    /**
     * Stress Test 메시지 Parse 오류코드
     */
    static public String STRESS_TEST_MESSAGE_PARSE_ERROR = "10000038";
    
    /**
     * 공공자전거 메세지 Parse 오류코드
     */
    static public String BICYCLE_MESSAGE_PARSE_ERROR = "10000039";
    
    // /
    /**
     * Form 메시지 Build 오류코드
     */
    static public String FORM_MESSAGE_BUILD_ERROR = "10000040";
    
    /**
     * Json 메세지 Build 오류코드
     */
    static public String JSON_MESSAGE_BUILD_ERROR = "10000041";
    
    /**
     * XML 메세지 Build 오류코드
     */
    static public String XML_MESSAGE_BUILD_ERROR = "10000042";
    
    /**
     * Echo 메세지 Build 오류코드
     */
    static public String ECHO_MESSAGE_BUILD_ERROR = "10000043";
    
    /**
     * Stress Test 메시지 Build 오류코드
     */
    static public String STRESS_TEST_MESSAGE_BUILD_ERROR = "10000044";
    
    /**
     * 공공자전거 메세지 Build 오류코드
     */
    static public String BICYCLE_MESSAGE_BUILD_ERROR = "10000045";
    
    /**
     * 채널 공통 오류코드
     */
    static public String CHANNEL_ERROR = "10000100";
    
    /**
     * 채널 제어 공통 오류코드
     */
    static public String CHANNEL_CONROL_ERROR = "10000101";
    
    /**
     * 대상 채널이 DB에 없을 경우
     */
    static public String CHANNEL_CONTROL_NOT_EXIST_ERROR = "10000102";
    
    /**
     * 채널 제어에서 채널 시작시 오류코드
     */
    static public String CHANNEL_CONTROL_START_ERROR = "10000103";
    
    /**
     * 채널 제어에서 채널 정지시 오류코드
     */
    static public String CHANNEL_CONTROL_STOP_ERROR = "10000104";
    
    /**
     * 채널 제어에서 채널 상태 조회시 오류코드
     */
    static public String CHANNEL_CONTROL_STATUS_ERROR = "10000105";
    
    /**
     * 채널 제어에서 채널 재시작시 오류코드
     */
    static public String CHANNEL_CONTROL_RELOAD_ERROR = "10000106";
    
    /**
     * 채널 제어에서 채널 추가시 오류코드
     */
    static public String CHANNEL_CONTROL_ADD_ERROR = "10000107";
    
    /**
     * 채널 제어에서 이미 정지중인 채널 오류코드
     */
    static public String CHANNEL_CONTROL_ALREADY_STOPPED = "10000108";
    
    /**
     * 채널 제어에서 채널 삭제시 오류코드
     */
    static public String CHANNEL_CONTROL_REMOVE_ERROR = "10000109";
    
    /**
     * 존재하지 않는 채널 오류코드(Servlet 채널)
     */
    static public String CHANNEL_NOT_FOUND = "10000110";
    
    /**
     * 시작되지 않은 채널 오류코드
     */
    static public String CHANNEL_NOT_STARTED_ERROR = "10000111";
    
    /**
     * 채널 Load 오류코드
     */
    static public String CHANNEL_LOAD_ERROR = "10000112";
    
    /**
     * 채널 Unload 오류코드
     */
    static public String CHANNEL_UNLOAD_ERROR = "10000113";
    
    /**
     * 채널 Decode 오류
     */
    static public String CHANNEL_DECODE_ERROR = "10000114";
    
    /**
     * 채널 Encode 오류코드
     */
    static public String CHANNEL_ENCODE_ERROR = "10000115";
    
    /**
     * 채널 미지원 기능 오류코드
     */
    static public String CHANNEL_NOT_SUPPORTED = "10000116";
    
    /**
     * 채널 아웃바운드 Sender 오류코드
     */
    static public String CHANNEL_OUTBOUND_SENDER_ERROR = "10000150";
    
    /**
     * 채널 Logging 오류코드
     */
    static public String CHANNEL_LOGGING_ERROR = "10000199";
    
    /**
     * 기본 오류코드
     */
    static public String TELEGRAM_MESSAGE_PARSE_ERROR = "10000300";// 전문 메시지 Parse오류
    static public String TELEGRAM_MESSAGE_BUILD_ERROR = "10000301";// 전문 메시지 Build오류
    static public String TELEGRAM_CLASS_NOT_FOUND_ERROR = "10000302";// Class를 찾을 수 없습니다.[{0}]
    static public String TELEGRAM_CLASS_INSTANCE_ERROR = "10000303";// Class Instance 생성 실패 하였습니다.[{0}]
    static public String TELEGRAM_CLASS_ACCESS_ERROR = "10000304";// Class Instance 접근실패 하였습니다.[{0}]
    static public String TELEGRAM_METHOD_ACCESS_ERROR = "10000305";// 메소드 접근실패 하였습니다.[{0}]
    static public String TELEGRAM_METHOD_ARGUMENT_ERROR = "10000306";// 메소드 파라미터 접근실패 하였습니다.[{0}]
    static public String TELEGRAM_METHOD_INVOKE_ERROR = "10000307";// 메소드 실행실패 하였습니다.[{0}
    static public String TELEGRAM_STREAM_WRITE_ERROR = "10000308";// 스트림을 쓰는중 에러가 발생하였습니다.
    static public String TELEGRAM_ESSENTIAL_FIELD_VALUE_ERROR = "10000309";// 필수값이 입력되지 않았습니다. [{0},{1},{2}]
    static public String TELEGRAM_DYNAMIC_HEADER_NOT_FOUND_ERROR = "10000310";// 다이나믹헤더 cons_cd 가 없습니다.
    static public String TELEGRAM_XML_PARSE_ERROR = "10000311";// XML 파싱처리중 에러가 발생하였습니다.
    static public String TELEGRAM_XML_READ_NODE_ERROR = "10000312";// XML 노드 읽기가 실패 하였습니다.
    static public String TELEGRAM_XML_FILE_NOT_FOUND_ERROR = "10000313";// XML 파일이 존재 하지 않습니다.
    static public String TELEGRAM_XML_IS_NOT_DIRECTORY_ERROR = "10000314";// 디렉토리가 아닙니다.
    static public String TELEGRAM_XML_INTERFACE_DIRECTORY_NOT_FOUND_ERROR = "10000315";// interface 디렉토리가 존재하지 않습니다.
    
    /**
     * E2E 키가 존재하지 않는 오류코드
     */
    static public String E2E_KEY_NOT_EXIST = "10000500";
    
    /**
     * E2E 키 교환 오류코드
     */
    static public String E2E_KEY_EXCHANGE = "10000501";
    
    /**
     * 서비스처리 내부 공통 오류코드
     */
    static public String INTERNAL_ERROR = "99991900";
    
    /**
     * 서비스처리 내부 SQL 오류코드
     */
    static public String INTERNAL_SQL_ERROR = "99991901";
    
    /**
     * 처리되지 않은 오류코드
     */
    static public String UNKNOWN_RROR = "99999999";
    
}
