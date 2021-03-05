package org.fincl.miss.server.message.parser.telegram.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.fincl.miss.core.context.ApplicationContextSupport;
import org.fincl.miss.core.server.channel.OutBoundRequest;
import org.fincl.miss.core.server.channel.OutBoundResponse;
import org.fincl.miss.server.channel.BoundChannel;
import org.fincl.miss.server.channel.ChannelManagerImpl;
import org.fincl.miss.server.channel.inbound.InBoundChannelImpl;
import org.fincl.miss.server.channel.outbound.OutBoundChannelImpl;
import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.MessageParserException;
import org.fincl.miss.server.message.parser.telegram.Message;
import org.fincl.miss.server.message.parser.telegram.TelegramConstant;
import org.fincl.miss.server.message.parser.telegram.factory.TelegramBodyFactory;
import org.fincl.miss.server.message.parser.telegram.factory.TelegramInterfaceFactory;
import org.fincl.miss.server.message.parser.telegram.factory.TelegramInterfaceManager;
import org.fincl.miss.server.message.parser.telegram.factory.db.TB_IFS_TLGM_IFVo;
import org.fincl.miss.server.message.parser.telegram.model.FieldGroupModel;
import org.fincl.miss.server.message.parser.telegram.model.FieldNodeModel;
import org.fincl.miss.server.message.parser.telegram.tluser.vo.TlUser;
import org.fincl.miss.server.message.parser.telegram.tluser.vo.TlUserField;
import org.fincl.miss.server.message.parser.telegram.valueobjects.FieldGroupVO;
import org.fincl.miss.server.message.parser.telegram.valueobjects.FieldNodeVO;
import org.fincl.miss.server.message.parser.telegram.valueobjects.FieldVO;
import org.fincl.miss.server.message.parser.telegram.valueobjects.InterfaceIdVo;
import org.fincl.miss.server.test.SimpleTcpClient;
import org.fincl.miss.server.util.EnumCode.Charset;
import org.fincl.miss.server.util.EnumCode.SyncType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class TelegramRemoteUtil {
    
    @Autowired
    ChannelManagerImpl channerManager;
    int g_idx = 0;
    String gIoId = "";
    String TELE_SOURCE = "SOURCE";
    String TELE_TARGET = "TARGET";
    
    public TelegramRemoteUtil() {
        
    }
    
    // 필드를 String으로
    public String parseUserTelegramFieldToString(String ifId, String jSonMessage, String messageTypeString) throws MessageParserException {
        TelegramInterfaceManager telegramInterfaceManager = ApplicationContextSupport.getBean(TelegramInterfaceManager.class);
        TB_IFS_TLGM_IFVo vo = telegramInterfaceManager.getInterfaceInfo(ifId);
        BoundChannel boundChannel = null;
        if (messageTypeString.equals(TELE_SOURCE)) {
            boundChannel = channerManager.getOutBoundChannel(vo.getOutBoundChId());
        }
        else if (messageTypeString.equals(TELE_TARGET)) {// 타발
            boundChannel = channerManager.getInBoundChannel(vo.getInBoundChId());
        }
        
        TelegramSysUtil tu = ApplicationContextSupport.getBean(TelegramSysUtil.class);
        TlUser tlUser = new TlUser();
        tlUser.setFields(jSonMessage);
        
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        // 헤더부 처리
        ArrayList<TlUserField> tlUserFieldList = tlUser.getFields();
        
        Iterator<TlUserField> it = tlUserFieldList.iterator();
        while (it.hasNext()) {
            TlUserField tlUserField = it.next();
            
            if (!tlUserField.getDataType().equals(TelegramConstant.TELE_FIELD_TYPE_ARRAY)) {
                byte[] buff = parseUserValue("", tlUserField.getDataType(), StringUtil.nvl(tlUserField.getData()), tlUserField.getLength(), tlUserField.getScale(), boundChannel.getCharsetEnum());
                try {
                    bao.write(buff);
                }
                catch (IOException e) {
                    throw new MessageParserException(ErrorConstant.TELEGRAM_STREAM_WRITE_ERROR, e);// 스트림을 쓰는중 에러가 발생하였습니다.
                }
            }
        }
        // retByte 에 6byte를 잘라서 새로 정의해준다.
        byte[] retByte = tu.setLengthPrefixPattern(bao.toByteArray());
        return StringUtil.convert(retByte, boundChannel.getCharsetEnum().toString());
    }
    
    // //스트링을 FIED로 이게 어려움
    public String parseUserTelegramStringToField(String ifId, String sMessage, String messageTypeString) throws MessageParserException {
        // IF 이용하여 메세지 파싱 message 생성한다.
        TelegramSysUtil tu = ApplicationContextSupport.getBean(TelegramSysUtil.class);
        
        TelegramInterfaceManager telegramInterfaceManager = ApplicationContextSupport.getBean(TelegramInterfaceManager.class);
        TB_IFS_TLGM_IFVo vo = telegramInterfaceManager.getInterfaceInfo(ifId);
        BoundChannel boundChannel = null;
        int messageType = 0;
        if (messageTypeString.equals(TELE_SOURCE)) {
            boundChannel = channerManager.getOutBoundChannel(vo.getOutBoundChId());
            messageType = Message.SOURCE_OUTBOUND;
            gIoId = vo.getSourceIoId();
        }
        else if (messageTypeString.equals(TELE_TARGET)) {// 타발
            boundChannel = channerManager.getInBoundChannel(vo.getInBoundChId());
            messageType = Message.TARGET_INBOUND;
            gIoId = vo.getTargetIoId();
        }
        tu.parseUserMakeMessage(boundChannel, sMessage.getBytes(), messageType);
        
        LinkedHashMap<String, FieldVO> headerListMap = tu.getMessage().getHeadersFields();
        
        List<TlUserField> tlUserFieldList = new ArrayList<TlUserField>();
        Iterator<?> it = headerListMap.entrySet().iterator();
        g_idx=0;
        int depth=1;
        while (it.hasNext()) {
            Entry<?, ?> entry = (Entry<?, ?>) it.next();
            if (entry.getValue() instanceof FieldNodeVO) {
                setTlUserNodeField((FieldNodeVO) entry.getValue(), tlUserFieldList, depth);
            }
            else {
                setTlUserGroupField((FieldGroupVO) entry.getValue(), tlUserFieldList,depth);
            }
        }
        g_idx=0;
        depth=1;
        LinkedHashMap<String, FieldVO> ioListMap = tu.getMessage().getBodyFields();
        it = ioListMap.entrySet().iterator();
        while (it.hasNext()) {
            Entry<?, ?> entry = (Entry<?, ?>) it.next();
            if (entry.getValue() instanceof FieldNodeVO) {
                setTlUserNodeField((FieldNodeVO) entry.getValue(), tlUserFieldList,depth);
            }
            else {
                setTlUserGroupField((FieldGroupVO) entry.getValue(), tlUserFieldList,depth);
            }
        }
        
        return getJsonListToString(tlUserFieldList);// Json;Json;
        
    }
    
    private void setTlUserNodeField(FieldNodeVO fieldVo, List<TlUserField> tlUserFieldList, int depth) {
        FieldNodeModel model = (FieldNodeModel) fieldVo.getModel();
        TlUserField vo = new TlUserField();
        
        vo.setOrder(++g_idx);
        vo.setField(model.getId());
        vo.setFieldName(model.getName());
        vo.setDataType(model.getType());
        vo.setLength(model.getLength());
        vo.setScale(model.getFrac_len());
        if (TelegramConstant.TELE_HEADER_TYPE.equals(model.getTeleType())) {
            vo.setType(model.getTeleType());
            vo.setIoId(model.getHeaderId());
        }
        else {
            vo.setIoId(gIoId);
            vo.setType("IO");
        }
          
        vo.setData(StringUtil.convert(fieldVo.getBytes(), fieldVo.getCharset().toString()));
        
        vo.setArrayOrder(getArrayOrder(model.getName(),tlUserFieldList));
        
        if(vo.getIoId().equals(model.getParent().getKey())){
            vo.setParentField("");
        }else{
            vo.setParentField(model.getParent().getKey().substring(model.getParent().getKey().lastIndexOf(".")+1));
        }
        
        vo.setDepth(depth);
        
        tlUserFieldList.add(vo);
    }
    //이지혁이가 해달라고함
    private int getArrayOrder(String fieldName, List<TlUserField> tlUserFieldList){
        int cnt=0;
        if(tlUserFieldList.size()==0){
            return cnt;
        }
        for(int i=0;i<tlUserFieldList.size();i++){
            if(tlUserFieldList.get(i).getFieldName().equals(fieldName)){
                cnt++;
            }
        }
        return cnt;
    }
    
    private void setTlUserGroupField(FieldGroupVO fieldVo, List<TlUserField> tlUserFieldList,int depth) {
        FieldGroupModel model = (FieldGroupModel) fieldVo.getModel();
        TlUserField vo = new TlUserField();
        vo.setOrder(++g_idx);
        vo.setField(model.getId());
        vo.setFieldName(model.getName());
        // vo.setDataType(parseConvertType(model.getType()));
        vo.setDataType("ARRAY");
        vo.setLength(model.getLength());
        vo.setDataArrayField(model.getLength_filed_id());// model.getParent().getId()
        
        if (TelegramConstant.TELE_HEADER_TYPE.equals(model.getTeleType())) {
            vo.setType(model.getTeleType());
            vo.setIoId(model.getHeaderId());
        }
        else {
            vo.setIoId(gIoId);
            vo.setType("IO");
        }
        
        
        vo.setArrayOrder(getArrayOrder(model.getName(),tlUserFieldList));        
        if(vo.getIoId().equals(model.getParent().getKey())){
            vo.setParentField("");
        }else{
            vo.setParentField(model.getParent().getKey().substring(model.getParent().getKey().lastIndexOf(".")+1));
        }
        
        vo.setDepth(depth);
        tlUserFieldList.add(vo);
        
        
        depth++;
        List<List<FieldVO>> fieldList = fieldVo.getFieldVOList();
        Iterator<List<FieldVO>> it = fieldList.iterator();
        while (it.hasNext()) {
            List<FieldVO> fieldListVo = (List<FieldVO>) it.next();
            Iterator<FieldVO> it2 = fieldListVo.iterator();
            while (it2.hasNext()) {
                FieldVO fieldVO = (FieldVO) it2.next();
                if (fieldVO instanceof FieldNodeVO) { 
                    setTlUserNodeField((FieldNodeVO) fieldVO, tlUserFieldList,depth);
                }
                else { 
                    setTlUserGroupField((FieldGroupVO) fieldVO, tlUserFieldList,depth);
                }
            }
        }

    }
    
    // 소켓에 날림
    @SuppressWarnings("unused")
    public void sendUserTelegramMessage(String ifId, String sMessage, String messageTypeString, String ip, int port) {
        // IF 이용하여 메세지 파싱 message 생성한다.
        TelegramSysUtil tu = ApplicationContextSupport.getBean(TelegramSysUtil.class);
        
        TelegramInterfaceManager telegramInterfaceManager = ApplicationContextSupport.getBean(TelegramInterfaceManager.class);
        TB_IFS_TLGM_IFVo vo = telegramInterfaceManager.getInterfaceInfo(ifId);
        BoundChannel boundChannel = null;
        int messageType = 0;
        if (messageTypeString.equals(TELE_SOURCE)) {
            boundChannel = channerManager.getOutBoundChannel(vo.getOutBoundChId());
            messageType = Message.SOURCE_OUTBOUND;
            gIoId = vo.getSourceIoId();
        }
        else if (messageTypeString.equals(TELE_TARGET)) {// 타발
            boundChannel = channerManager.getInBoundChannel(vo.getInBoundChId());
            messageType = Message.TARGET_INBOUND;
            gIoId = vo.getTargetIoId();
        }
        tu.parseUserMakeMessage(boundChannel, sMessage.getBytes(), messageType);
        OutBoundChannelImpl outBoundChannel = channerManager.getOutBoundChannel(vo.getOutBoundChId());
        InterfaceIdVo interfaceIdVo = TelegramInterfaceFactory.getMessageID(outBoundChannel.getRelationId());
        OutBoundRequest outBoundRequest = new OutBoundRequest();
        outBoundRequest.setPayload(sMessage.getBytes());
        outBoundRequest.setGuid(interfaceIdVo.getGlobalId(tu.getMessage()));
        outBoundRequest.setInterfaceId(interfaceIdVo.getInterfaceId(tu.getMessage()));
        outBoundRequest.setServiceId(interfaceIdVo.getServiceId(tu.getMessage()));
        
        if (!"".equals(ip) && port > 0) {// 사용자 체널
            SimpleTcpClient tcpClient = new SimpleTcpClient(ip, port);
            if (messageTypeString.equals(TELE_SOURCE)) { // 당발 
                if (outBoundChannel.getSyncTypeEnum() == SyncType.SYNC) {// 001: Sync 003 aSyncFep
                    byte[] gg = tcpClient.sendAndReceive(sMessage.getBytes());
                }
                else if (outBoundChannel.getSyncTypeEnum() == SyncType.PUBSUB) {
                    Message message = tu.getMessage();
                    byte[] gg = tcpClient.sendAndReceive(interfaceIdVo.getGlobalId(message), sMessage.getBytes());// ASync FEP
                }
                else {// async
                    tcpClient.send(sMessage.getBytes());
                }
            }
            else {// 타발
                  // 수신서버의정보를 이용하여 client 생성후 client로 전송한다.
                InBoundChannelImpl inBoundChannel = channerManager.getInBoundChannel(vo.getInBoundChId());
                if (inBoundChannel.getSyncTypeEnum() == SyncType.SYNC) {// 001: Sync 003 aSyncFep
                    byte[] gg = tcpClient.sendAndReceive(sMessage.getBytes());
                    // }else if(extInBoundChannel.getSyncTypeEnum() == SyncType.PUBSUB){ //async fep
                    // tcpClient.sendMessage(messageTypeString.getBytes());
                }
                else {// async or fep
                    tcpClient.send(sMessage.getBytes());
                }
            }
        }
        else {// 기존체널
            if (messageTypeString.equals(TELE_SOURCE)) { // 당발    
                if (outBoundChannel.getSyncTypeEnum() == SyncType.SYNC) {// 001: Sync 003 aSyncFep
                    OutBoundResponse outBoundResponse = outBoundChannel.sendAndReceive(outBoundRequest);
                }
                else if (outBoundChannel.getSyncTypeEnum() == SyncType.PUBSUB) {// ASync FEP
                    OutBoundResponse outBoundResponse = outBoundChannel.sendAndReceive(outBoundRequest);
                }
                else {// async 
                    outBoundChannel.send(outBoundRequest);
                }
            }
            else {// 타발
                  // 수신서버의정보를 이용하여 client 생성후 client로 전송한다.
                InBoundChannelImpl inBoundChannel = (InBoundChannelImpl) channerManager.getInBoundChannel(vo.getInBoundChId());
                SimpleTcpClient tcpClient = new SimpleTcpClient("127.0.0.1", inBoundChannel.getPort()); 
                if (inBoundChannel.getSyncTypeEnum() == SyncType.SYNC) {// 001: Sync 003 aSyncFep
                    byte[] gg = tcpClient.sendAndReceive(sMessage.getBytes());
                 }
                else {// async or fep
                    tcpClient.send(sMessage.getBytes());
                }
            }
        }
    }
    
    // 헤더 정보만 추출한다.
    public String parseUserTelegramHeaderString(String ifId) {
        // 당발 메세지 전송
        TelegramInterfaceManager telegramInterfaceManager = ApplicationContextSupport.getBean(TelegramInterfaceManager.class);
        TB_IFS_TLGM_IFVo vo = telegramInterfaceManager.getInterfaceInfo(ifId);
        
        String channelId = vo.getOutBoundChId();
        
        OutBoundChannelImpl outBoundChannel = channerManager.getOutBoundChannel(channelId);
        TelegramSysUtil tu = ApplicationContextSupport.getBean(TelegramSysUtil.class);
        tu.getSendMessageHeader(ifId, outBoundChannel);
        
        LinkedHashMap<String, FieldVO> headerListMap = tu.getMessage().getHeadersFields();
        
        List<TlUserField> tlUserFieldList = new ArrayList<TlUserField>();
        Iterator<?> it = headerListMap.entrySet().iterator();
        g_idx=0;
        int depth=1;
        while (it.hasNext()) {
            Entry<?, ?> entry = (Entry<?, ?>) it.next();
            if (entry.getValue() instanceof FieldNodeVO) {
                setTlUserNodeField((FieldNodeVO) entry.getValue(), tlUserFieldList,depth);
            }
            else {
                setTlUserGroupField((FieldGroupVO) entry.getValue(), tlUserFieldList,depth);
            }
        }
        
        return getJsonListToString(tlUserFieldList);// Json;
        
    }
    
    private byte[] parseUserValue(String delimiter, String fielddType, String fielddValue, int fieldSize, int fracLen, Charset charset) {
        byte[] fieldBuf = null;
        if (delimiter == null || "".equals(delimiter.trim())) {
            if (fielddType.equals(TelegramConstant.TELE_FIELD_TYPE_CHAR)) {
                byte[] buff = StringUtil.convert(fielddValue, charset.toString());
                if (fieldSize != 0) fieldBuf = FieldUtil.setChar(buff, fieldSize);
                else fieldBuf = FieldUtil.setChar(buff, buff.length);
                
            }
            else if (fielddType.equals(TelegramConstant.TELE_FIELD_TYPE_NUM)) {
                fieldBuf = FieldUtil.setNum(fielddValue, fieldSize);
            }
            else if (fielddType.equals(TelegramConstant.TELE_FIELD_TYPE_DEC)) {
                fieldBuf = FieldUtil.setDec(fielddValue, fieldSize, fracLen);
            }
        }
        else {
            if (fielddType.equals(TelegramConstant.TELE_FIELD_TYPE_CHAR)) {
                byte[] buff = StringUtil.convert(fielddValue, charset.toString());
                fieldBuf = FieldUtil.setChar(buff, fielddValue.getBytes().length);
            }
            else if (fielddType.equals(TelegramConstant.TELE_FIELD_TYPE_NUM)) {
                fieldBuf = FieldUtil.setNum(fielddValue, fielddValue.getBytes().length);
            }
            else if (fielddType.equals(TelegramConstant.TELE_FIELD_TYPE_DEC)) {
                fieldBuf = FieldUtil.setDec(fielddValue, fielddValue.getBytes().length, fracLen);
            }
        }
        
        return fieldBuf;
    }
    
    private String getJsonListToString(List<TlUserField> tlUserFieldList) {
        
        StringBuffer sbuf = new StringBuffer();
        ObjectMapper om = new ObjectMapper();
        
        try {
            sbuf.append(om.writeValueAsString(tlUserFieldList));
        }
        catch (JsonGenerationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sbuf.toString();
    }
    
    // Body IO
    public static void reLoadTelegramBody(String ifId) throws MessageParserException {
        TelegramBodyFactory.reLoadBodyInstance(ifId);
    }
    
    // //Header IO, Header Rel
    public static void reLoadTelegramHeader(String headerId) throws MessageParserException {
        TelegramInterfaceFactory.reLoadMessageID(headerId);
    }
    
}
