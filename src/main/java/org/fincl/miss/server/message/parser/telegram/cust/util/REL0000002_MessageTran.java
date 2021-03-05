package org.fincl.miss.server.message.parser.telegram.cust.util;

 
import java.io.Serializable;

import org.fincl.miss.server.message.parser.telegram.Message;
import org.fincl.miss.server.message.parser.telegram.valueobjects.InterfaceIdVo;

/** 
 * - 클래스 설명 - 
 *
 */

public class REL0000002_MessageTran implements InterfaceIdVo , Serializable{

 
	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    public String getId(Message message) {
		// TODO Auto-generated method stub
		return (String)message.getFieldValue("TEST_SYS_COPT.STTL_INTF_ID").toString();
	}

	public void setId(String id, Message message) {
		// TODO Auto-generated method stub
		message.setFieldValue("TEST_SYS_COPT.STTL_INTF_ID",id);
	}

    public String getWhbnSttlSrn(Message message){
        // TODO Auto-generated method stub
           return message.getString("TEST_SYS_COPT.WHBN_STTL_SRN");
    }
    
//	// 로깅을 위해 추가
//		public String getSystemCode(Message message) {
//			// TODO Auto-generated method stub
//			return message.getString("TEST_SYS_COPT.RQST_SYS_DCD");
//		}
//
//		public String getTransactionDate(Message message) {
//			// TODO Auto-generated method stub
//			return message.getString("TEST_SYS_COPT.TRNM_TS").substring(0,8);
//		}
//
//		public String getTransactionTime(Message message) {
//			// TODO Auto-generated method stub
//			return message.getString("TEST_SYS_COPT.TRNM_TS").substring(8,17);
//		}
//
//		public String getHostName(Message message) {
//			// TODO Auto-generated method stub
//			return StringUtil.getHostName();
//		}

        public String getTrnmSysDcd(Message message) {         
         // TODO Auto-generated method stub
            return message.getString("TEST_SYS_COPT.TRNM_SYS_DCD");
        }
        public String getRqstRspnDcd(Message message) {         
         // TODO Auto-generated method stub
            return message.getString("TEST_SYS_COPT.RQST_RSPN_DCD");
        }
        
		public String getGlobalId(Message message) {
			// TODO Auto-generated method stub
			return message.getString("TEST_SYS_COPT.STTL_INTF_ANUN_ID");
		}

		public String getInterfaceId(Message message) {
			// TODO Auto-generated method stub
			return message.getString("TEST_SYS_COPT.STTL_INTF_ID");
		}

//		public String getResult(Message message) {
//			// TODO Auto-generated method stub
//			return message.getString("TEST_SYS_COPT.RSPN_PCRS_DCD");
//		}
//
//		public String getErrorCode(Message message) {
//			// TODO Auto-generated method stub
////			if(message.hasDynamicHeader("MC"))
////				return message.getString("STTL_MSG_COPT.PNMG_INFO.PNMG_CD");
////			else
//				return "";
//		}
//
//		public String getErrorMessage(Message message) {
//			// TODO Auto-generated method stub
////			if(message.hasDynamicHeader("MC"))
////				return message.getString("STTL_MSG_COPT.PNMG_INFO.PNMG_CON");
////			else
//				return "";
//		}
//
//		public String getRequestResponseCode(Message message) {
//			// TODO Auto-generated method stub
//			return message.getString("TEST_SYS_COPT.RQST_RSPN_DCD");
//		}
//
//		public String getMessageVersion(Message message) {
//			// TODO Auto-generated method stub
//			return message.getString("TEST_SYS_COPT.STTL_VER_DSNC");
//		}
//
//		public void setSystemCode(String systemCode, Message message) {
//			// TODO Auto-generated method stub
//			message.setFieldValue("TEST_SYS_COPT.RQST_SYS_DCD", systemCode);
//		}

		public void setGlobalId(String globalID, Message message) {
			// TODO Auto-generated method stub
			message.setFieldValue("TEST_SYS_COPT.STTL_INTF_ANUN_ID", globalID);
		}

		public void setInterfaceId(String interfaceID, Message message) {
			// TODO Auto-generated method stub
			message.setFieldValue("TEST_SYS_COPT.STTL_INTF_ID", interfaceID);
		}

        @Override
        public String getServiceId(Message message) {
            // TODO Auto-generated method stub
            return message.getString("TEST_SYS_COPT.RQST_RCV_SVC_ID");
        }

        @Override
        public void setServiceId(String serviceId, Message message) {
         // TODO Auto-generated method stub
            message.setFieldValue("TEST_SYS_COPT.RQST_RCV_SVC_ID",serviceId);
        }

//		public void setResult(String result, Message message) {
//			// TODO Auto-generated method stub
//			message.setFieldValue("TEST_SYS_COPT.RSPN_PCRS_DCD", result);
//		}
//
//		public void setRequestResponseCode(String reqeustResponseCode,
//				Message message) {
//			// TODO Auto-generated method stub
//			message.setFieldValue("TEST_SYS_COPT.RQST_RSPN_DCD", reqeustResponseCode);
//		}
//
//		public void setMessageVersion(String messageVersion, Message message) {
//			// TODO Auto-generated method stub
//			message.setFieldValue("TEST_SYS_COPT.STTL_VER_DSNC", messageVersion);
//		}
//		
//		public String getBodyVersion(Message message) {
//			// TODO Auto-generated method stub
//			return message.getString("TEST_SYS_COPT.STTL_IDPR_VRSN_SRN");
//		}
//
//		public void setBodyVersion(String bodyVersion, Message message) {
//			// TODO Auto-generated method stub
//			message.setFieldValue("TEST_SYS_COPT.STTL_IDPR_VRSN_SRN", bodyVersion);
//		}
}
