package org.fincl.miss.server.message.parser.telegram.cust.util;
 

import java.util.Properties; 

import org.fincl.miss.core.context.ApplicationContextSupport;
import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.MessageParserException;
import org.fincl.miss.server.message.parser.telegram.Message;
import org.fincl.miss.server.message.parser.telegram.MessageHeader;
import org.fincl.miss.server.message.parser.telegram.TelegramConstant;
import org.fincl.miss.server.message.parser.telegram.factory.TelegramInterfaceManager;
import org.fincl.miss.server.message.parser.telegram.factory.db.TB_IFS_TLGM_IFVo;
import org.fincl.miss.server.message.parser.telegram.util.DateUtil;
import org.fincl.miss.server.message.parser.telegram.util.SequenceUtil;
import org.fincl.miss.server.message.parser.telegram.util.StringUtil;
 
/** 
 * 
 */ 
public class REL0000001_MessageSendUtil implements MessageHeader {
 
    /** 'S'=동기응답대기, 'A'=동기응답대기하지 않음, 'K'=동기응답으로ACK 대기 **/
    private final String SYNC_RSPN_WAIT_DCD = "S";
    /** 'N'=Non XA(Called Commit) , 'Y'=XA설정됨 (Caller Commit) **/
    private final String ETAH_TRN_YN = "N";
    /** 0:통상입력 1:대량입력시작 2:대량입력계속 3:대량입력끝 8:대량출력중지요청 9:대량출력계속요청 **/
    private final String INPT_TMGT_DCD = "0";
 

    /**
     * interface.xml에 시스템(기관)코드, 업무구분코드를 가져올 수 있음, 그래서 Parameter로 interface id를
     * 입력 받아야 함
     * 
     * @param message
     */
    public void makeEAIBasicHeader(Message message, String sttl_xcd) {
        String interfaceID = (String) message.getHeaderFieldValue("STTL_SYS_COPT","STTL_INTF_ID");

        String date = DateUtil.getCurrentDate();
        
        TelegramInterfaceManager telegramInterfaceManager = ApplicationContextSupport.getBean(TelegramInterfaceManager.class);
        
        TB_IFS_TLGM_IFVo vo =  telegramInterfaceManager.getInterfaceInfo(interfaceID);
        


       
          
        String hostname = StringUtil.getHostName();
       
        
        Properties serviceProps = (Properties) ApplicationContextSupport.getBean("serviceProps");
        //systemSeq instance no
        String systemSeq = StringUtil.lpad(serviceProps.getProperty("serverKey", "00"),2,"0");	
       
        
        String sequence  = SequenceUtil.get(systemSeq, 3);
        
        String timeStamp = DateUtil.getCurrentDate("yyyyMMddHHmmssSSS");
        // <<시스템공통부>>
        message.setHeaderFieldValue("STTL_SYS_COPT","STTL_CMRS_YN", "N"); // 표준전문압축여부
        message.setHeaderFieldValue("STTL_SYS_COPT","STTL_ENCP_DCD", "0"); // 표준전문암호화구분코드
        message.setHeaderFieldValue("STTL_SYS_COPT","STTL_LNGG_DCD", "KO"); // 표준전문언어구분코드

        message.setHeaderFieldValue("STTL_SYS_COPT","STTL_VER_DSNC", "D24"); // 표준전문버전구분값
        message.setHeaderFieldValue("STTL_SYS_COPT","WHBN_STTL_WRTN_YMD", date); // 표준전문작성년월일
        
        /**
         * 표준전문D22에서 바뀌내용 적용
         * 대면채널 : 'T' + 점번(4) + 기번(3)
         * 비대면채널 : 망구분(1) + 시스템코드(3) + 일련번호(4)
         * 기타 시스템 : 비대면채널과 동일함
         */
        hostname = "D"+"MOF"+StringUtil.lpad(systemSeq, 4, "0");
        message.setHeaderFieldValue("STTL_SYS_COPT","WHBN_STTL_CRET_SYS_NM", hostname); // 표준전문생성시스템명
        
         
        message.setHeaderFieldValue("STTL_SYS_COPT","WHBN_STTL_SRN", sequence); // 표준전문일련번호        
        message.setHeaderFieldValue("STTL_SYS_COPT","WHBN_STTL_PGRS_DSNC_NO", "0000"); // 표준전문진행구분번호
        message.setHeaderFieldValue("STTL_SYS_COPT","WHBN_STTL_PGRS_NO", "0001"); // 표준전문진행번호        
        
        //message.setHeaderFieldValue("STTL_SYS_COPT","STTL_INTF_ANUN_ID", date+hostname+sequence+"0000"+"0001"); // 표준전문UNIQUEID
        message.setHeaderFieldValue("STTL_SYS_COPT","STTL_INTF_ANUN_ID", "20150617DMOF0000001140140570000000"); // 표준전문UNIQUEID
        
        
        
        
        message.setHeaderFieldValue("STTL_SYS_COPT","STTL_IP", StringUtil.getHostIp()); // 표준전문IP
        message.setHeaderFieldValue("STTL_SYS_COPT","STTL_MAC_ADR_VL", "50B7C3A6A9AD"); // 표준전문MAC주소값
//        message.setHeaderFieldValue("STTL_SYS_COPT","FRST_RQST_SYS_DCD", "MOF"); // 최초요청시스템구분코드 삭제됨???
        message.setHeaderFieldValue("STTL_SYS_COPT","SYS_ENVR_INFO_DCD", "D"); // 시스템환경정보구분코드
        message.setHeaderFieldValue("STTL_SYS_COPT","FRST_RQST_TS", timeStamp); // 최초요청일시
        message.setHeaderFieldValue("STTL_SYS_COPT","STTL_MCTM_USE_YN", "N"); // 표준전문유지시간사용여부
        message.setHeaderFieldValue("STTL_SYS_COPT","STTL_MCTM_SEC_VL", "030"); // 표준전문유지시간초값
        message.setHeaderFieldValue("STTL_SYS_COPT","TRNM_SYS_DCD", "MOF"); // 송신시스템구분코드
        message.setHeaderFieldValue("STTL_SYS_COPT","TRNM_ND_ID", StringUtil.lpad("01", 4, "0")); // 송신노드ID
        message.setHeaderFieldValue("STTL_SYS_COPT","TRNM_TS", timeStamp); // 송신일시
        message.setHeaderFieldValue("STTL_SYS_COPT","RQST_RSPN_DCD", "S"); // 요청응답구분코드
        
        message.setHeaderFieldValue("STTL_SYS_COPT","STTL_IDPR_VRSN_SRN", message.getBodyDefineVersion()); // 표준전문개별부버전일련번호
        
        message.setHeaderFieldValue("STTL_SYS_COPT","RQST_SYS_DCD", "MOF"); // 요청시스템구분코드
        message.setHeaderFieldValue("STTL_SYS_COPT","RQST_SYS_BSWR_DCD", vo.getTxWork()); // 요청시스템업무구분코드
        message.setHeaderFieldValue("STTL_SYS_COPT","STTL_INTF_ID", interfaceID); // 표준전문인터페이스ID
        message.setHeaderFieldValue("STTL_SYS_COPT","SYNC_RSPN_WAIT_DCD", SYNC_RSPN_WAIT_DCD); // 동기응답대기구분코드
        message.setHeaderFieldValue("STTL_SYS_COPT","ETAH_TRN_YN", ETAH_TRN_YN); // XA거래여부
        message.setHeaderFieldValue("STTL_SYS_COPT","INPT_TMGT_DCD", INPT_TMGT_DCD); // 입력전문유형구분코드
        message.setHeaderFieldValue("STTL_SYS_COPT","STTL_RQST_TS", timeStamp); // 표준전문요청일시

        if(TelegramConstant.TELE_GETTER_IF_SERVICE_FLAG){
            message.setHeaderFieldValue("STTL_SYS_COPT","RQST_RCV_SVC_ID",  vo.getServiceId()); // 요청수신서비스ID, IF에서 추출
        }
        
        

        
        // message.setHeaderFieldValue("STTL_SYS_COPT","RSPN_RCV_SVC_ID" , ""); //
        // 응답수신서비스ID
        // message.setHeaderFieldValue("STTL_SYS_COPT","EROR_RSPN_SVC_ID" , ""); //
        // 오류응답서비스ID
        // message.setHeaderFieldValue("STTL_SYS_COPT","RSPN_RCV_ND_ID" , ""); //
        // 응답수신노드ID
        // message.setHeaderFieldValue("STTL_SYS_COPT","RSPN_SYS_DCD" , ""); //
        // 응답시스템구분코드
        // message.setHeaderFieldValue("STTL_SYS_COPT","STTL_RSPN_TS" , ""); // 표준전문응답일시
        // message.setHeaderFieldValue("STTL_SYS_COPT","RSPN_PCRS_DCD" , ""); //
        // 응답처리결과구분코드
        // message.setHeaderFieldValue("STTL_SYS_COPT","OTPT_TMGT_DCD" , ""); //
        // 출력전문유형구분코드
        // message.setHeaderFieldValue("STTL_SYS_COPT","EROC_SYS_DCD" , ""); //
        // 오류발생시스템구분코드
        // message.setHeaderFieldValue("STTL_SYS_COPT","STTL_ERCD" , ""); // 표준전문오류코드
        //message.setHeaderFieldValue("STTL_SYS_COPT","RQST_CHPT_DCD", rooibos.getFRSTRQSTSYSDCD()); // 요청채널유형구분코드
        message.setHeaderFieldValue("STTL_SYS_COPT","RQST_CHPT_DCD", vo.getTxSystem()); // 요청채널유형구분코드
        message.setHeaderFieldValue("STTL_SYS_COPT","RQST_CHPT_DTLS_DCD", vo.getTxWork()); // 요청채널유형세부구분코드
        message.setHeaderFieldValue("STTL_SYS_COPT","RQST_CHBS_DCD", vo.getTxWork()); // 요청채널업무구분코드
        // message.setHeaderFieldValue("STTL_SYS_COPT","RQST_CHNL_SSN_ID" , ""); //
        // 요청채널세션ID
        message.setHeaderFieldValue("STTL_SYS_COPT","SSO_USE_YN", "N"); // SSO사용여부
//        message.setHeaderFieldValue("STTL_SYS_COPT","EAI_ND_ID", "0000"); // EAI노드ID
//        message.setHeaderFieldValue("STTL_SYS_COPT","MCA_ND_ID", "0000"); // MCA노드ID
//        message.setHeaderFieldValue("STTL_SYS_COPT","FEP_ND_ID", "0000"); // 대외계노드ID

        /*
         * if(rooibos.getCluster() != null)
         * message.setHeaderFieldValue("STTL_SYS_COPT","STTL_TRN_GLBL_ID" ,
         * date+IBKPostNgsCommons
         * .getHostName()+SequenceUtil.get(rooibos.getCluster().getId(),2)); //
         * 표준전문일련번호 else message.setHeaderFieldValue("STTL_SYS_COPT","STTL_TRN_GLBL_ID"
         * , date+IBKPostNgsCommons.getHostName()+SequenceUtil.get("00",2)); //
         * 표준전문거래글로벌ID
         */
//        message.setHeaderFieldValue("STTL_SYS_COPT","STTL_CTNT_NO", "00"); // 표준전문연속번호
        // message.setHeaderFieldValue("STTL_SYS_COPT","SYS_COPT_FLOP" , ""); //
        // 시스템공통부예비필드
        // <<거래공통부>>
        message.setHeaderFieldValue("STTL_TRN_COPT","TRN_CHNL_DCD", "OLT"); // 거래채널구분코드

        message.setHeaderFieldValue("STTL_TRN_COPT","TRRQ_BSWR_DCD", "MOF"); // 거래요청업무구분코드

        message.setHeaderFieldValue("STTL_TRN_COPT","STTL_XCD", sttl_xcd); // 표준전문거래코드
        // message.setHeaderFieldValue("STTL_TRN_COPT.STTL_RQST_FUNC_DSNC_ID", ""); //
        // 표준전문요청기능구분ID
        // message.setHeaderFieldValue("STTL_TRN_COPT.STTL_RSPN_FUNC_DSNC_ID", ""); //
        // 표준전문응답기능구분ID
        // message.setHeaderFieldValue("STTL_TRN_COPT.STTL_EROR_FUNC_DSNC_ID", ""); //
        // 표준전문오류기능구분ID
        // message.setHeaderFieldValue("STTL_TRN_COPT.INPT_SCRE_NO" , ""); // 입력화면번호
        message.setHeaderFieldValue("STTL_TRN_COPT","CNCL_DCD", "1"); // 취소구분코드
        message.setHeaderFieldValue("STTL_TRN_COPT","CTNT_TRN_DCD", "0"); // 연속거래구분코드
        // message.setHeaderFieldValue("STTL_TRN_COPT.INPT_TRN_SRN" , ""); // 입력거래일련번호
        message.setHeaderFieldValue("STTL_TRN_COPT","BLNG_FNCM_CD", "001"); // 소속금융회사코드
        // message.setHeaderFieldValue("STTL_TRN_COPT.TCSL_INFO_TRN_KCD" , ""); //
        // 접촉정보거래종류코드
        // message.setHeaderFieldValue("STTL_TRN_COPT.CUS_AROD_INFO_TRN_YN" , ""); //
        // 고객맞춤정보거래여부
        // message.setHeaderFieldValue("STTL_TRN_COPT.EXT_TRN_BSWR_DCD" , ""); //
        // 대외거래업무구분코드
        // message.setHeaderFieldValue("STTL_TRN_COPT.EXT_TRN_INTT_DCD" , ""); //
        // 대외거래기관구분코드
        // message.setHeaderFieldValue("STTL_TRN_COPT.EXT_TRN_UNQ_ID" , ""); //
        // 대외거래고유ID
        // message.setHeaderFieldValue("STTL_TRN_COPT.EXT_TRN_SSN_ID" , ""); //
        // 대외거래세션ID
        // message.setHeaderFieldValue("STTL_TRN_COPT.TRN_COPT_FLOP" , ""); //
        // 거래공통부예비필드
        // <<대면채널공통부>>
        try {
            if (!message.hasDynamicHeader("MC")) message.addDynamicHeader("MC");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        
        // message.setHeaderFieldValue("STTL_AMGC_COPT.TRMN_INLT_BRCD" ,
        // mc.getTRMN_INLT_BRCD()); // 단말설치부점코드
        // message.setHeaderFieldValue("STTL_AMGC_COPT.TMN" , mc.getTMN()); // 단말번호
        // message.setHeaderFieldValue("STTL_AMGC_COPT.IDCR_SCAN_SRN" , ""); //
        // 신분증스캔일련번호
        // message.setHeaderFieldValue("STTL_AMGC_COPT.TLN" , mc.getTLN()); // 텔러번호
        message.setHeaderFieldValue("STTL_AMGC_COPT","ACIT_RNL_MODE_DCD", "1"); // 계정갱신모드구분코드
                                                                         // 당일모드로
                                                                        // 셋팅한다.
        message.setHeaderFieldValue("STTL_AMGC_COPT","BACL_DCD", "1"); // 마감전후구분코드
        // message.setHeaderFieldValue("STTL_AMGC_COPT.RCKN_YMD" , ""); // 기산년월일
        // message.setHeaderFieldValue("STTL_AMGC_COPT.YNBK_DCD" , ""); // 유통무통구분코드
        // message.setHeaderFieldValue("STTL_AMGC_COPT.CAAL_DCD" , ""); // 현금대체구분코드
        // message.setHeaderFieldValue("STTL_AMGC_COPT.PRUS_SVC_ID" , ""); // 출력용서비스ID
        // message.setHeaderFieldValue("STTL_AMGC_COPT.PRUS_INTF_ID" , ""); //
        // 출력용인터페이스ID
        // message.setHeaderFieldValue("STTL_AMGC_COPT.BNKB_SRN" , mc.getBNKB_SRN());
        // // 통장일련번호
        // message.setHeaderFieldValue("STTL_AMGC_COPT.IC_CHIP_MDIA_KIND_DCD",
        // mc.getIC_CHIP_MDIA_KIND_DCD()); // IC칩매체종류구분코드
        // message.setHeaderFieldValue("STTL_AMGC_COPT.BRCD" , mc.getBRCD()); // 부점코드
        // message.setHeaderFieldValue("STTL_AMGC_COPT.OPTO_EMN" , ""); // 조작자직원번호
        message.setHeaderFieldValue("STTL_AMGC_COPT","SVAT_DCD", "0"); // 책임자승인구분코드
        message.setHeaderFieldValue("STTL_AMGC_COPT","OPAT_DCD", "0"); // 조작자승인구분코드
        message.setHeaderFieldValue("STTL_AMGC_COPT","TRMG_ATHZ_DCD", "0"); // 전달메시지승인구분코드
        // message.setHeaderFieldValue("STTL_AMGC_COPT.WON_CASH_BAL" , ""); // 원화현금잔액
        // message.setHeaderFieldValue("STTL_AMGC_COPT.OBRC_AMT" , ""); // 타점수납금액
        // message.setHeaderFieldValue("STTL_AMGC_COPT.WON_ALTR_DFAM_AMT" , ""); //
        // 원화대체차액금액
        // message.setHeaderFieldValue("STTL_AMGC_COPT.FRCTF_DSCR_YN" , ""); //
        // 외화대체불일치여부

        // <<비대면채널공통부>>
        try {
            if (!message.hasDynamicHeader("NC")) message.addDynamicHeader("NC");
        } catch (MessageParserException e) {
            throw new MessageParserException(ErrorConstant.TELEGRAM_DYNAMIC_HEADER_NOT_FOUND_ERROR, e);  
        }
        /*
         * message.setHeaderFieldValue("STTL_NFCH_COPY.USR_IDNT_NO" , ""); // 이용자식별번호
         * message.setHeaderFieldValue("STTL_NFCH_COPY.LRRN_USR_NO" ,
         * nc.getLRRN_USR_NO()); // 하위이용자번호
         * message.setHeaderFieldValue("STTL_NFCH_COPY.USR_CCTN_MCTL_IP" , ""); //
         * 이용자접속기기IP message.setHeaderFieldValue("STTL_NFCH_COPY.USR_CCTN_MAC_VL" ,
         * ""); // 이용자접속MAC값 message.setHeaderFieldValue("STTL_NFCH_COPY.USR_CCTN_TPN"
         * , ""); // 이용자접속전화번호
         * message.setHeaderFieldValue("STTL_NFCH_COPY.USR_CCTN_MCTL_ID" , ""); //
         * 이용자접속기기ID message.setHeaderFieldValue("STTL_NFCH_COPY.USR_ACNT_CDN" , "");
         * // 이용자계좌카드번호 message.setHeaderFieldValue("STTL_NFCH_COPY.ATCT_CQRCG_NO" ,
         * ""); // 공인인증서고유식별번호
         * message.setHeaderFieldValue("STTL_NFCH_COPY.PWD_VRFC_DCD" , ""); //
         * 비밀번호검증구분코드 message.setHeaderFieldValue("STTL_NFCH_COPY.PWD_ENCP_YN" ,
         * nc.getPWD_ENCP_YN()); // 비밀번호암호화여부
         * message.setHeaderFieldValue("STTL_NFCH_COPY.SECU_MDIA_DCD" , ""); //
         * 보안매체구분코드 message.setHeaderFieldValue("STTL_NFCH_COPY.SECU_MDIA_NO" , "");
         * // 보안매체번호 message.setHeaderFieldValue("STTL_NFCH_COPY.SCCD_SQN1" , ""); //
         * 보안카드순번1 message.setHeaderFieldValue("STTL_NFCH_COPY.SCCD_PWD1" ,
         * nc.getSCCD_PWD1()); // 보안카드비밀번호1
         * message.setHeaderFieldValue("STTL_NFCH_COPY.SCCD_SQN2" , ""); // 보안카드순번2
         * message.setHeaderFieldValue("STTL_NFCH_COPY.SCCD_PWD2" ,
         * nc.getSCCD_PWD2()); // 보안카드비밀번호2
         * message.setHeaderFieldValue("STTL_NFCH_COPY.TRAN_PWD" , nc.getTRAN_PWD());
         * // 이체비밀번호 message.setHeaderFieldValue("STTL_NFCH_COPY.ACNT_PWD" ,
         * nc.getACNT_PWD()); // 계좌비밀번호
         * message.setHeaderFieldValue("STTL_NFCH_COPY.TLGR_MSGCD" ,
         * nc.getTLGR_MSGCD()); // 전문메시지코드
         * message.setHeaderFieldValue("STTL_NFCH_COPY.ENTP_DCD" , ""); // 업체구분코드
         * message.setHeaderFieldValue("STTL_NFCH_COPY.SRVR_OTPT_RQST_NBI" , ""); //
         * 서버출력요청건수 message.setHeaderFieldValue("STTL_NFCH_COPY.SRVR_DATA_DLPR_EN" ,
         * ""); // 서버데이터전달부유무
         * message.setHeaderFieldValue("STTL_NFCH_COPY.SRVR_DATA_DLPR_CON" , ""); //
         * 서버데이터전달부내용
         */

    }

//    public void makeEAINextHeader(Message message) {
//        Rooibos rooibos = RooibosConfigLoaderManager.getConfiguration().getConfiguration().getRooibos();
//        String timeStamp = DateUtil.getCurrentDate("yyyyMMddHHmmssSSS");
//        String date = DateUtil.getCurrentDate();
//        
//        
//        String sequence = null;
//        String systemSeq = null;
//        
//        if(System.getProperty("rooibos.seq") != null)
//        {
//        	systemSeq = System.getProperty("rooibos.seq");
//        }
//        else if (rooibos.getCluster() != null)
//        {
//        	systemSeq = rooibos.getCluster().getId();
//        }
//        else
//        {
//        	systemSeq = "00";
//        	
//        }   
//        sequence = SequenceUtil.get(systemSeq, 3);
//        
//        message.setHeaderFieldValue("STTL_SYS_COPT","WHBN_STTL_SRN", sequence); // 표준전문일련번호
//        /*
//         * if(rooibos.getCluster() != null)
//         * message.setHeaderFieldValue("STTL_SYS_COPT","STTL_TRN_GLBL_ID" ,
//         * date+IBKPostNgsCommons
//         * .getHostName()+SequenceUtil.get(rooibos.getCluster().getId(),2)); //
//         * 표준전문일련번호 else message.setHeaderFieldValue("STTL_SYS_COPT","STTL_TRN_GLBL_ID"
//         * , date+IBKPostNgsCommons.getHostName()+SequenceUtil.get("00",2)); //
//         * 표준전문거래글로벌ID
//         */
//        message.setHeaderFieldValue("STTL_SYS_COPT","STTL_INTF_ANUN_ID", date+message.getString("STTL_SYS_COPT","WHBN_STTL_CRET_SYS_NM")+sequence+"0000"+"0001"); // 표준전문UNIQUEID
//        message.setHeaderFieldValue("STTL_SYS_COPT","STTL_RQST_TS", timeStamp); // 표준전문요청일시
//    }
    
//    /**
//     * 타시스템에서 전송받은 전문을 이용하여 또 다른 시스템으로 전송해야 하는 경우
//     * 해당 메소드를 이용하여 헤더를 셋팅함.
//     * 
//     * @param receivedMessage : 타기관으로 부터 전송받은 전문
//     * @param willSendMessage : 또 다른 기관으로 전송해야 하는 전문
//     */
//    public void setLinkHeader (Message receivedMessage, Message willSendMessage, String sttl_xcd) {
//    	
//    	String interfaceID = willSendMessage.getString("STTL_SYS_COPT","STTL_INTF_ID");
//		String rqst_sys_dcd = willSendMessage.getString("STTL_SYS_COPT","RQST_SYS_DCD");	//요청시스템구분코드
//		String rqst_sys_bswr_dcd = willSendMessage.getString("STTL_SYS_COPT","RQST_SYS_BSWR_DCD");	//요청시스템업무구분코드
//		
//    	MappingUtil.copyHeader(receivedMessage,willSendMessage);
//
//    	// 인터페이스 아이디가 다른경우 바꿔준다.
//    	willSendMessage.setHeaderFieldValue("STTL_SYS_COPT","STTL_INTF_ID", interfaceID);
//    	willSendMessage.setHeaderFieldValue("STTL_SYS_COPT","RQST_SYS_DCD", rqst_sys_dcd);	//요청시스템구분코드
//    	willSendMessage.setHeaderFieldValue("STTL_SYS_COPT","RQST_SYS_BSWR_DCD", rqst_sys_bswr_dcd);	//요청시스템업무구분코드
//		
//    	/**
//    	 * 표준전문유지시간의 유효시간(초)
//         * 거래 요청시 표준전문유지시간사용 여부가 'Y' 일 경우, 연계하는 
//         * 시스템에서는 "수신된 표준전문유지시간초값 - 5초"(안) 를 세팅하여 전송한다. 
//    	 */
//    	String ttlYn = willSendMessage.getString("STTL_SYS_COPT","STTL_MCTM_USE_YN");
//    	if("Y".equals(ttlYn)) {
//    		String ttlTime = willSendMessage.getString("STTL_SYS_COPT","STTL_MCTM_SEC_VL");
//    		willSendMessage.setHeaderFieldValue("STTL_SYS_COPT","STTL_MCTM_SEC_VL", ""+(Integer.parseInt(ttlTime)-5));
//    	}
//    	
//    	/**
//    	 * 여러 노드를 거치는 과정에서 GID중복을 회피하기 위한 분기번호 부여
//         * (복합거래시 연동 수동인터페이스 증가시 마다 +1하며, 
//         *  단일거래의 경우 최초요청시 '0000' 으로 설정, 이후 시스템은 유지)
//    	 */
//    	String prgsDsncNo = willSendMessage.getString("STTL_SYS_COPT","WHBN_STTL_PGRS_DSNC_NO");
//    	int iPrgsDsncNo = Integer.parseInt(prgsDsncNo);
//    	willSendMessage.setHeaderFieldValue("STTL_SYS_COPT","WHBN_STTL_PGRS_DSNC_NO",""+(iPrgsDsncNo+1));
//    	
//    	/**
//    	 * 시스템을 거치는 과정의 표준전문글로벌ID를 추적 하기 위함
//         * 타시스템 송신시 진행상황을 누적(+1)하여 송신해야 함. ('0000'~'9999')
//    	 */
//    	String prgsNo = willSendMessage.getString("STTL_SYS_COPT","WHBN_STTL_PGRS_NO");
//    	int iPrgsNo = Integer.parseInt(prgsNo);
//    	willSendMessage.setHeaderFieldValue("STTL_SYS_COPT","WHBN_STTL_PGRS_NO",""+(iPrgsNo+1));
//    	
//    	/**
//    	 * 표준전문작성년월일 + 표준전문생성시스템명 + 표준전문일련번호 + 표준전문진행구분번호
//    	 */
//    	String intfAnunId = willSendMessage.getString("STTL_SYS_COPT","WHBN_STTL_WRTN_YMD") 
//    	                  + willSendMessage.getString("STTL_SYS_COPT","WHBN_STTL_CRET_SYS_NM")
//    	                  + willSendMessage.getString("STTL_SYS_COPT","WHBN_STTL_SRN")
//    	                  + willSendMessage.getString("STTL_SYS_COPT","WHBN_STTL_PGRS_DSNC_NO");
//    	willSendMessage.setHeaderFieldValue("STTL_SYS_COPT","STTL_INTF_ANUN_ID",intfAnunId);
//    	
//    	
//    	/**
//    	 * 표준전문 개별부 버전의 일치여부를 검증하기 위한 필드 값
//    	 */
//    	willSendMessage.setHeaderFieldValue("STTL_SYS_COPT","STTL_IDPR_VRSN_SRN",willSendMessage.getBodyDefineVersion());
//    	
//    	InterfaceVO vo = (InterfaceVO) telegramInterfaceManager.get(interfaceID);
//        Rooibos rooibos = RooibosConfigLoaderManager.getConfiguration().getConfiguration().getRooibos();
//        String timeStamp = DateUtil.getCurrentDate("yyyyMMddHHmmssSSS");
//        String RQST_RCV_SVC_ID = vo.getServiceId();
//        willSendMessage.setHeaderFieldValue("STTL_SYS_COPT","RQST_RCV_SVC_ID", RQST_RCV_SVC_ID); // 요청수신서비스ID
//        if(sttl_xcd != null)
//        	willSendMessage.setHeaderFieldValue("STTL_TRN_COPT.STTL_XCD", sttl_xcd); // 표준전문거래코드
//        
//        willSendMessage.setHeaderFieldValue("STTL_SYS_COPT","TRNM_SYS_DCD", rooibos.getFRSTRQSTSYSDCD()); // 송신시스템구분코드
//        willSendMessage.setHeaderFieldValue("STTL_SYS_COPT","TRNM_ND_ID", StringUtils.leftPad(rooibos.getSTTLSRNID(), 4, '0')); // 송신노드ID
//        willSendMessage.setHeaderFieldValue("STTL_SYS_COPT","TRNM_TS", timeStamp); // 송신일시
//
//    }
    public void makeEAINextHeader(Message message) {
        //소스타겟을 바꾸는등의 작업을 처리하자.
    }
    @Override
    public void makeHeader(Message message) {
        String xcd = message.getStringTrim("STTL_TRN_COPT.STTL_XCD",""); 
        makeEAIBasicHeader(message, xcd);
//        message.printLog();
    }
    
    @Override
    public void makeErrHeader(Message message) {
        message.setHeaderFieldValue("STTL_SYS_COPT","EROR_RSPN_SVC_ID", "ERR");
    }

    @Override
    public void makeNextHeader(Message message) {
        makeHeader(message);
        makeEAINextHeader(message);
        
    }

}
