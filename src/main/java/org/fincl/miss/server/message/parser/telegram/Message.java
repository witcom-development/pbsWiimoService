package org.fincl.miss.server.message.parser.telegram;

import java.util.LinkedHashMap;
import java.util.Set;

import org.fincl.miss.server.exeption.MessageParserException;
import org.fincl.miss.server.message.parser.telegram.valueobjects.FieldVO;
import org.fincl.miss.server.util.EnumCode.Charset;



/** 
 * - 전문을 나타내는 자바 인터페이스 - 
 *
 */
public interface Message {
//    public static int IO_INBOUND  = 0;
//    public static int IO_OUTBOUND = 1; 
    
    public static int SOURCE_INBOUND  = 0;
    public static int SOURCE_OUTBOUND = 1;
    public static int TARGET_INBOUND  = 2;
    public static int TARGET_OUTBOUND = 3;
    
    public String getMessageManagerId();
    
    public void essentialValueHeaderCheck() throws MessageParserException;
    public void essentialValueCheck() throws MessageParserException;

    
    public Charset getCharset();
    public void setCharset(Charset charset);
    
    public String getMessageId();
    public int getMessageType();
    
    public FieldVO getFieldVO(String key);
    public FieldVO getFieldGroupVO(String key);
    public FieldVO getHeaderFieldGroupVO(String headerId,String key);
    
    public void setFieldValue(String key, Object obj);
    public void setHeaderFieldValue(String headerId, String key, Object obj);
    public void setHeaderFieldValue(String headerId, String key, Object obj,int index);
//    public void setHeaderFieldValue(String headerId, String key, Object obj,int index1,int index2);
    
    public Object getFieldValue(String key);
    public Object getHeaderFieldValue(String headerId, String key);
    public Object getHeaderFieldValue(String headerId, String key, int index);
    public Object getHeaderFieldValue(String header,String groupName, int gIndex,String key, int sIndex);
 

    public void setFieldValue(String key, Object obj, int index);
//    public void setFieldValue(String groupName, int gIndex, String key, int index, Object obj);
//    public void setFieldValue(String groupName1, int index1, String groupName2, int index2, String key,int index3,Object obj);
    public Object getFieldValue(String key, int index);
    public Object getFieldValue(String groupName, int gIndex, String key, int sIndex);
    
    public String getString(String key);
    public String getString(String key, int index);
    public String getStringTrim(String key);
    public String getStringTrim(String key, int index);
    public String getStringTrim(String key, String nvl);
    public String getStringTrim(String key, int index, String nvl);
    
    public boolean hasDynamicHeader(String cons_cd);
//    public List<String> selectDynamicHeaderString();
    public void addDynamicHeader(String cons_cd) throws MessageParserException;
//    public void removeDynamicHeader(String cons_cd) throws DynamicHeaderNotFoundException , Exception;
    public Set<String> getAllKeys();
    public LinkedHashMap<String, FieldVO> getAllFields();
    public LinkedHashMap<String, FieldVO> getHeadersFields();
//    public LinkedHashMap<String, FieldVO> getDynamicHeadersFields(String cons_cd);
    public LinkedHashMap<String, FieldVO> getBodyFields();
    public void printLog() throws MessageParserException;    
//    public void copyDynamicHeader(String cons_cd, Message source);
    
    public byte[] getMessageHeaderBytes() throws MessageParserException;
    
    public byte[] getMessageBytes() throws MessageParserException;
    public String getMessageString() throws MessageParserException;  
 
    
    public byte[] getHeaders()throws MessageParserException;
    public String getHeadersString() throws MessageParserException;
    
//    public byte[] getHeadersWithoutDynamicHeaders() throws Exception;
//    public String getHeadersWithoutDynamicHeadersString() throws Exception;
    
    // 고정헤더든 다이내믹헤더든... 이름으로 가져오기
    public String getHeaderString(String headerName) throws MessageParserException;  
    
    public byte[] getBody()throws MessageParserException;
    public String getBodyString() throws MessageParserException; 
    public void setBodyBytes(byte[] bodyMessage)throws MessageParserException;
    
        
//    // 로그처리관련 메소드 추가
//    public String getSystemCode();
//    public String getTransactionDate();
//    public String getTransactionTime();
//    public String getHostName();
//    public String getGlobalID();
//    public String getInterfaceID(); 
//    public String getResult();
//    public String getErrorCode();
//    public String getErrorMessage();
//    public String getRequestResponseCode();
//    public String getMessageVersion();
//     
    public String getBodyDefineVersion();
//    public String getBodyRealVersion();
//    
//    // 박광흠과장 요청 - 전문 담당자 정보 가져오는 메소드
//    public String getWriterName();
    
}
