package org.fincl.miss.server.message.parser.telegram;

public interface TelegramConstant {
    
    //Service ID는 interface와 telegram 두곳에 씌인다.
    //한가지를 선택해서 사용하자.
    public static boolean TELE_GETTER_IF_SERVICE_FLAG = false;//true:if 에서 추출한다. false:전문에서 추출한다.
    
    public static String TELE_HEADER_PAKAGE ="org.fincl.miss.server.message.parser.telegram.cust.header.vo.";
    
    public static String TELE_UTIL_PAKAGE ="org.fincl.miss.server.message.parser.telegram.cust.util.";
    public static String TELE_UTIL_PAKAGE_TAIL ="_MessageTran";
    
    public static String TELE_HEADER_TYPE =  "HEADER";
    public static String TELE_BODY_TYPE   = "BODY";
    
    public static String TELE_FIELD_TYPE_ARRAY = "ARRAY";
    public static String TELE_FIELD_TYPE_CHAR = "STRING";
    public static String TELE_FIELD_TYPE_NUM  = "NUMBER";
    public static String TELE_FIELD_TYPE_DEC  = "DECIMAL";
    
    public static String TELE_HEADER_VO_MAP  = "HeaderVoMap";
    
    
    public static String TELE_TX_TYPE_FEP  = "FEP";
//    public static String TELE_TX_TYPE_EAI  = "EAI";//아직 사용하지않음
//    public static String TELE_TX_TYPE_MCA  = "MCA";//아직 사용하지않음
    public static String TELE_TX_SEND_CODE  = "S";
//    public static String TELE_TX_RECIVE_CODE  = "R";//아직 사용하지않음
    
    
}
