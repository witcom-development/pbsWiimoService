package org.fincl.miss.server.message.parser.telegram.cust.util;

 
import java.io.Serializable;

import org.fincl.miss.server.message.parser.telegram.Message;
import org.fincl.miss.server.message.parser.telegram.valueobjects.InterfaceIdVo;

/** 
 * - 클래스 설명 - 
 *
 */

public class REL0000001_MessageTran implements InterfaceIdVo , Serializable{
 
 
    private static final long serialVersionUID = 1L;
    public String getId(Message message) {
		// TODO Auto-generated method stub
		return (String)message.getFieldValue("STTL_SYS_COPT.STTL_INTF_ID").toString();
	}

	public void setId(String id, Message message) {
		// TODO Auto-generated method stub
		message.setFieldValue("STTL_SYS_COPT.STTL_INTF_ID",id);
	}
 
    public String getWhbnSttlSrn(Message message){
     // TODO Auto-generated method stub
        return message.getString("STTL_SYS_COPT.WHBN_STTL_SRN");
    }

	public String getGlobalId(Message message) {
		// TODO Auto-generated method stub
		return message.getString("STTL_SYS_COPT.STTL_INTF_ANUN_ID");
	}

	public String getInterfaceId(Message message) {
		// TODO Auto-generated method stub
		return message.getString("STTL_SYS_COPT.STTL_INTF_ID");
	}
 
		public void setGlobalId(String globalID, Message message) {
			// TODO Auto-generated method stub
		message.setFieldValue("STTL_SYS_COPT.STTL_INTF_ANUN_ID", globalID);
	}

	public void setInterfaceId(String interfaceID, Message message) {
		// TODO Auto-generated method stub
		message.setFieldValue("STTL_SYS_COPT.STTL_INTF_ID", interfaceID);
	}

    @Override
    public String getServiceId(Message message) {
        // TODO Auto-generated method stub
        return message.getString("STTL_SYS_COPT.RQST_RCV_SVC_ID");
    }

    @Override
    public void setServiceId(String serviceId, Message message) {
     // TODO Auto-generated method stub
        message.setFieldValue("STTL_SYS_COPT.RQST_RCV_SVC_ID",serviceId);
    }
    public String getTrnmSysDcd(Message message) {         
    // TODO Auto-generated method stub
       return message.getString("STTL_SYS_COPT.TRNM_SYS_DCD");
    }
    public String getRqstRspnDcd(Message message) {         
    // TODO Auto-generated method stub
       return message.getString("STTL_SYS_COPT.RQST_RSPN_DCD");
    }
}
