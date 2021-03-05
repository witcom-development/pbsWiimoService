package org.fincl.miss.server.message.parser.telegram.cust.util;
 

import java.util.Properties;
import java.util.Set;

import org.fincl.miss.server.message.parser.telegram.Message;
import org.fincl.miss.server.message.parser.telegram.MessageHeader;
import org.fincl.miss.server.message.parser.telegram.valueobjects.FieldGroupVO;
 
/** 
 * 
 */

public class REL0000002_MessageSendUtil implements MessageHeader {
 
    public void printEnv() {
        Properties props = System.getProperties();
        Set<Object> set = props.keySet();
        for (Object obj : set) {
            String key = (String) obj;
            System.out.println(key + ": '" + props.getProperty(key) + "'");
        }
    }
  
    @Override
    public void makeHeader(Message message) {
  //      String interfaceID = (String) message.getHeaderFieldValue("TEST_SYS_COPT", "STTL_INTF_ID");
//
//        String timeStamp = DateUtil.getCurrentDate("yyyyMMddHHmmssSSS");
        // <<시스템공통부>> 
        message.setHeaderFieldValue("TEST_SYS_COPT","STTL_CMRS_YN", "N"); // 표준전문압축여부
        
        message.setHeaderFieldValue("TEST_SYS_COPT","TEST_LIST_LENGTH", "02");  
 
        message.setHeaderFieldValue("TEST_SYS_COPT","TEST_LIST.TITLE",  "제11",0); 
        message.setHeaderFieldValue("TEST_SYS_COPT","TEST_LIST.SUBTITLE", "부11",0); 
        message.setHeaderFieldValue("TEST_SYS_COPT","TEST_LIST.ETC", "기11",0); 
         
        message.setHeaderFieldValue("TEST_SYS_COPT","TEST_LIST.TITLE", "제목",1); 
        message.setHeaderFieldValue("TEST_SYS_COPT","TEST_LIST.SUBTITLE", "부제",1); 
        message.setHeaderFieldValue("TEST_SYS_COPT","TEST_LIST.ETC", "기타",1); 
        
        
        message.setHeaderFieldValue("TEST_SYS_COPT","TEST_LIST2_LENGTH", "02");
        
        
        message.setHeaderFieldValue("TEST_SYS_COPT","TEST_LIST2.TITLE2","제22",0);
        message.setHeaderFieldValue("TEST_SYS_COPT","TEST_LIST2.SUBTITLE2","부22",0); 
        
        message.setHeaderFieldValue("TEST_SYS_COPT","TEST_LIST2.TEST_LIST3_LENGTH", "01",0);  

        
        FieldGroupVO TEST_LIST2 = (FieldGroupVO)message.getHeaderFieldGroupVO("TEST_SYS_COPT","TEST_LIST2");     
        FieldGroupVO TEST_LIST3_0 = (FieldGroupVO)TEST_LIST2.getFieldVO("TEST_LIST3", 0);
         
        TEST_LIST3_0.setValue("TITLE3", "SUB1", 0);
        TEST_LIST3_0.setValue("SUBTITLE3", "SUB2", 0);
  
        message.setHeaderFieldValue("TEST_SYS_COPT","TEST_LIST2.LIST2LAST", "LAST",0);  
  
        message.setHeaderFieldValue("TEST_SYS_COPT","TEST_LIST2.TITLE2","제33",1);
        message.setHeaderFieldValue("TEST_SYS_COPT","TEST_LIST2.SUBTITLE2","제44",1); 
        
         
        
        message.setHeaderFieldValue("TEST_SYS_COPT","TEST_LIST2.TEST_LIST3_LENGTH", "02",1);  
            
        FieldGroupVO TEST_LIST3_1 = (FieldGroupVO)TEST_LIST2.getFieldVO("TEST_LIST3", 1);
         
        TEST_LIST3_1.setValue("TITLE3", "SUB3", 0);
        TEST_LIST3_1.setValue("SUBTITLE3", "SUB4", 0);
        TEST_LIST3_1.setValue("TITLE3", "SUB5", 1);
        TEST_LIST3_1.setValue("SUBTITLE3", "SUB6", 1);

        
        message.setHeaderFieldValue("TEST_SYS_COPT","TEST_LIST2.LIST2LAST", "LAST",1);  
        
        
        message.setHeaderFieldValue("TEST_SYS_COPT","LAST_FIELD", "LAST");
        
        message.setHeaderFieldValue("TEST_SYS_COPT","TRNM_SYS_DCD", "KFC");
        message.setHeaderFieldValue("TEST_SYS_COPT","RQST_RSPN_DCD", "S");
         
        message.setHeaderFieldValue("TEST_SYS_COPT","STTL_INTF_ANUN_ID", "TESTGUID0000000"); 
        message.setHeaderFieldValue("TEST_SYS_COPT","RQST_RCV_SVC_ID", "SVC00001"); 

        
    }
    @Override
    public void makeErrHeader(Message message) {
        // TODO Auto-generated method stub
        makeHeader(message);
        
        message.setHeaderFieldValue("TEST_SYS_COPT","LAST_FIELD", "ERR");
    }

    @Override
    public void makeNextHeader(Message message) {
        // TODO Auto-generated method stub
        
    }
}
