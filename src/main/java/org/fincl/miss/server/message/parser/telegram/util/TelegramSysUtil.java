package org.fincl.miss.server.message.parser.telegram.util;

import org.fincl.miss.core.context.ApplicationContextSupport;
import org.fincl.miss.core.server.channel.OutBoundRequest;
import org.fincl.miss.core.server.channel.OutBoundResponse;
import org.fincl.miss.server.channel.BoundChannel;
import org.fincl.miss.server.channel.ChannelManagerImpl;
import org.fincl.miss.server.channel.outbound.OutBoundChannelImpl;
import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.MessageParserException;
import org.fincl.miss.server.message.MessageInterfaceVO;
import org.fincl.miss.server.message.parser.telegram.Message;
import org.fincl.miss.server.message.parser.telegram.MessageHeader;
import org.fincl.miss.server.message.parser.telegram.MessageManager;
import org.fincl.miss.server.message.parser.telegram.TelegramConstant;
import org.fincl.miss.server.message.parser.telegram.factory.TelegramInterfaceFactory;
import org.fincl.miss.server.message.parser.telegram.factory.TelegramInterfaceManager;
import org.fincl.miss.server.message.parser.telegram.factory.TelegramModelInitialize;
import org.fincl.miss.server.message.parser.telegram.factory.db.TB_IFS_TLGM_IFVo;
import org.fincl.miss.server.message.parser.telegram.model.MessageModel;
import org.fincl.miss.server.message.parser.telegram.valueobjects.InterfaceIdVo;
import org.fincl.miss.server.message.parser.telegram.valueobjects.InterfaceMessageVo;
import org.fincl.miss.server.service.ServiceRegister;
import org.fincl.miss.server.service.metadata.ServiceMetadata;
import org.fincl.miss.server.service.metadata.impl.ServiceMetadataResolverImpl;
import org.fincl.miss.server.util.EnumCode.Charset;
import org.fincl.miss.server.util.EnumCode.SyncType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class TelegramSysUtil {
    @Autowired
    private ApplicationContext applicationContext; // ????????? ApplicationContext
    
    @Autowired
    ChannelManagerImpl channerManager;
    private String LENGTH_PREFIX_PATTERN = "000000";
    
    public TelegramSysUtil() {
        
    }
    
    private Message message = null;
    private String headerId = null;
    private InterfaceIdVo interfaceIdVo = null;
    private MessageModel model = null;
    private String channelId = null;
    private Charset charset = null;
    private MessageManager manager;
    
    public void parseUserMakeMessage(BoundChannel boundChannel, byte[] recvData, int messageType) throws MessageParserException {
        
        this.channelId = boundChannel.getChannelId();
        charset = boundChannel.getCharsetEnum();
        String relationId = boundChannel.getRelationId();
 
        recvData = StringUtil.convert(new String(recvData),charset.toString());
        
        MessageManager manager = new MessageManager(relationId);
        this.message = manager.getMessage(recvData, messageType, charset);
        
    }
    
    public Object getMakeInterfaceIdVoFep(BoundChannel boundChannel, byte[] recvData) throws MessageParserException {
        // FEP??? ???????????? ????????????.
        Object retInstance = null;
        this.channelId = boundChannel.getChannelId();
        
        charset = channerManager.getInBoundChannel(this.channelId).getCharsetEnum();
        
        reciveHeaderMessage(boundChannel, recvData);
        
        Class<?> interfaceCls = InterfaceMessageVo.class;
        // 1.???????????? ????????? ???????????? ???????????? ????????????.
        retInstance = TelegramVoUtil.convertMessageToInterface(interfaceCls, this, boundChannel);
        
        // invoke ???????????? FEP ??????????????? ?????? parsing ????????????.
        if (((MessageInterfaceVO) retInstance).isInvokable()) {
            
            Class<?> interfaceCls2 = null;
            
            ServiceMetadataResolverImpl metadataResolver = applicationContext.getBean(ServiceMetadataResolverImpl.class);
            metadataResolver.initialize();
            ServiceMetadata serviceMetadata = metadataResolver.resolve(((MessageInterfaceVO) retInstance).getServiceId());
            interfaceCls2 = serviceMetadata.getParamClass();// serviceRegister.getBizClassLoader().loadClass(loadClassNm);
            
            retInstance = TelegramVoUtil.convertMessageToInterface(interfaceCls2, this, boundChannel);
            
        }
        
        System.out.println("source::" + message);
        System.out.println("source length::" + recvData.length);
        
        return retInstance;
    }
    
    // ????????????
    public Object getMakeInterfaceIdVo(BoundChannel channelInbound, byte[] recvData) throws MessageParserException {
        
        Object retInstance = null;
        this.channelId = channelInbound.getChannelId();
        
        charset = channerManager.getInBoundChannel(this.channelId).getCharsetEnum();
        
        reciveMessage(channelInbound, recvData, Message.TARGET_INBOUND);
        
        String serviceId = getServiceId();// ??? ????????????Service Class??? ????????????.
        // this.message.printLog();
        
        Class<?> interfaceCls = null;
        
        ServiceMetadataResolverImpl metadataResolver = applicationContext.getBean(ServiceMetadataResolverImpl.class);
        metadataResolver.initialize();
        ServiceMetadata serviceMetadata = metadataResolver.resolve(serviceId);
        
        interfaceCls = serviceMetadata.getParamClass();// serviceRegister.getBizClassLoader().loadClass(loadClassNm);
        
        System.out.println("serviceId=" + serviceId);
        
        retInstance = TelegramVoUtil.convertMessageToInterface(interfaceCls, this, channelInbound);
        
        System.out.println("source::" + message);
        System.out.println("source length::" + recvData.length);
        
        return retInstance;
    }
    
    public Object getMakeSendInterfaceIdVo(BoundChannel boundChannel, byte[] recvData, Class<?> interfaceCls) throws MessageParserException {
        // Sync ??????
        Object retInstance = null;
        
        charset = channerManager.getOutBoundChannel(this.channelId).getCharsetEnum();
        reciveSyncMessage(channelId, recvData);
        String serviceId = getServiceId();// ??? ????????????Service Class??? ????????????.
        
        System.out.println("serviceId=" + serviceId);
        
        retInstance = TelegramVoUtil.convertMessageToInterface(interfaceCls, this, boundChannel);
        
        System.out.println("source::" + message);
        System.out.println("source length::" + recvData.length);
        
        return retInstance;
    }
    
    private void reciveSyncMessage(String channelId, byte[] recvData) throws MessageParserException {
        // Sync??????
        
        String relationId = channerManager.getOutBoundChannel(this.channelId).getRelationId();
        
        MessageManager manager = new MessageManager(relationId);
        this.message = manager.getMessage(recvData, Message.SOURCE_OUTBOUND, charset);
        model = TelegramModelInitialize.getMessageModel(manager.getMessage_id());
        
        interfaceIdVo = (InterfaceIdVo) getClassInstance(model.getMessageIDClass());
        
    }
    
    // ?????? ??????
    private Message reciveHeaderMessage(BoundChannel channelInbound, byte[] recvData) throws MessageParserException {
        
        String relationId = channelInbound.getRelationId();
        
        MessageManager manager = new MessageManager(relationId);
        this.message = manager.getHeaderMessage(recvData, charset);
        
        model = TelegramModelInitialize.getMessageModel(manager.getMessage_id());
        
        interfaceIdVo = (InterfaceIdVo) getClassInstance(model.getMessageIDClass());
        
        return this.message;
    }
    
    // ?????? ??????
    private Message reciveMessage(BoundChannel channelInbound, byte[] recvData, int messageType) throws MessageParserException {
        
        String relationId = channelInbound.getRelationId();
        
        MessageManager manager = new MessageManager(relationId);
        this.message = manager.getMessage(recvData, messageType, charset);
        
        model = TelegramModelInitialize.getMessageModel(manager.getMessage_id());
        
        interfaceIdVo = (InterfaceIdVo) getClassInstance(model.getMessageIDClass());
        
        return this.message;
    }
    
    public String getTrnmSysDcd() {
        return interfaceIdVo.getTrnmSysDcd(this.message);
    }
    
    public String getRqstRspnDcd() {
        return interfaceIdVo.getRqstRspnDcd(this.message);
    }
    
    public String getGlobalId() {
        return interfaceIdVo.getGlobalId(this.message);
    }
    
    public String getWhbnSttlSrn() {
        return interfaceIdVo.getWhbnSttlSrn(this.message);
    }
    
    public String getId() {
        return interfaceIdVo.getId(this.message);
    }
    
    public String getInterfaceId() {
        return interfaceIdVo.getInterfaceId(this.message);
    }
    
    public String getServiceId() {
        return interfaceIdVo.getServiceId(this.message);
    }
    
    public Message getReciveMessage() {
        return this.message;
    }
    
    public String getHeaderId() {
        return headerId;
    }
    
    public MessageModel getModel() {
        return model;
    }
    
    public byte[] getMakeMessageOutBoundByte(BoundChannel channelOutbound, MessageInterfaceVO messageInterfaceVO, int messageType) throws MessageParserException {
        // ?????? ????????? ?????? ???????????? ????????????. FEP??? ???????????? ?????????.
        byte[] retByte = null;
        this.channelId = channelOutbound.getChannelId();
        
        charset = channelOutbound.getCharsetEnum();
        
        message = getSendMessageSync(messageInterfaceVO, channelOutbound, messageType);
        
        TelegramVoUtil.convertVoToMessage(messageInterfaceVO, message);// body ??????
        
        // message??? ?????? ?????? ????????????.???
        // message.essentialValueCheck();
        
        retByte = message.getMessageBytes();
        
        // retByte ??? 6byte??? ????????? ?????? ???????????????.
        retByte = setLengthPrefixPattern(retByte);
        
        return retByte;
    }
    
    // public byte[] getMakeUserMessageOutBoundByte( ExtChannel extChannelOutbound) throws MessageParserException{
    //
    // byte[] retByte= null;
    // this.channelId =extChannelOutbound.getChannelId();
    // String relationId="";
    // try {
    // charset = extChannelOutbound.getCharsetEnum();
    // relationId =extChannelOutbound.getRelationId();
    // MessageManager manager = new MessageManager(relationId);
    //
    // this.message = manager.getTestMessage(charset);
    //
    // MessageHeader instance = (MessageHeader)getClassInstance(TelegramConstant.UTIL_PAKAGE+relationId+"_MessageSendUtil");
    //
    // instance.makeHeader(message);
    //
    // retByte = message.getMessageHeaderBytes();
    //
    // retByte = setLengthPrefixPattern(retByte);
    //
    //
    // }catch(MessageParserException e){
    // e.printStackTrace();
    // }
    // return retByte;
    //
    //
    // }
    
    public Message getSendMessageHeader(String ifId, BoundChannel channelOutbound) throws MessageParserException {
        
        manager = new MessageManager(channelOutbound.getRelationId());
        this.message = manager.getMessage(ifId, 0, channelOutbound.getCharsetEnum());
        MessageHeader instance = (MessageHeader) getClassInstance(TelegramConstant.TELE_UTIL_PAKAGE + channelOutbound.getRelationId() + "_MessageSendUtil");
        instance.makeHeader(message);
        
        return message;
    }
    
    private Message getSendMessageSync(MessageInterfaceVO messageInterfaceVO, BoundChannel channelOutbound, int messageType) throws MessageParserException {
        String relationId = "";
        // ?????? ????????? ?????? ???????????? ????????????. FEP??? ???????????? ?????????.
        relationId = channelOutbound.getRelationId();
        
        manager = new MessageManager(relationId);
        this.message = manager.getMessage(messageInterfaceVO.getInterfaceId(), messageType, charset);
        
        // TelegramHazelInstance.getTelegramModuleyMap().get("MODEL");
        // String[] arrDynamicHeader = messageInterfaceVO.getUseDynamicHeaderId();
        // if(arrDynamicHeader!=null){
        // for(int i=0;i<arrDynamicHeader.length;i++){
        // message.addDynamicHeader(arrDynamicHeader[i]);
        // }
        // }
        
        MessageHeader instance = (MessageHeader) getClassInstance(TelegramConstant.TELE_UTIL_PAKAGE + relationId + "_MessageSendUtil");
        
        instance.makeNextHeader(message);
        return message;
    }
    
    public MessageInterfaceVO SendMessage(MessageInterfaceVO messageInterfaceVO, Class<?> retClass) throws Exception {
        // ?????? ????????? ??????
        MessageInterfaceVO retMessageInterfaceVO = null;
        
        TelegramInterfaceManager telegramInterfaceManager = ApplicationContextSupport.getBean(TelegramInterfaceManager.class);
        TB_IFS_TLGM_IFVo vo = telegramInterfaceManager.getInterfaceInfo(messageInterfaceVO.getInterfaceId());
        
        channelId = vo.getOutBoundChId();
        
        OutBoundChannelImpl outBoundChannel = channerManager.getOutBoundChannel(channelId);
        byte[] retByte = getMakeMessageOutBoundByte((BoundChannel) outBoundChannel, messageInterfaceVO, Message.SOURCE_OUTBOUND);
        
        // ????????? ???????????? VO??? ?????????.
        InterfaceIdVo interfaceIdVo = TelegramInterfaceFactory.getMessageID(outBoundChannel.getRelationId());
        OutBoundRequest outBoundRequest = new OutBoundRequest();
        outBoundRequest.setPayload(retByte);
        outBoundRequest.setGuid(interfaceIdVo.getGlobalId(this.message));
        outBoundRequest.setInterfaceId(interfaceIdVo.getInterfaceId(this.message));
        outBoundRequest.setServiceId(interfaceIdVo.getServiceId(this.message));
        // Sync?????????
        if (outBoundChannel.getSyncTypeEnum() == SyncType.SYNC) {// 001: Sync 003 aSyncFep
            // byte[] reciveMsg = outBoundChannel.sendAndReceive(retByte);// Sync
            OutBoundResponse outBoundResponse = outBoundChannel.sendAndReceive(outBoundRequest);
            retMessageInterfaceVO = (MessageInterfaceVO) getMakeSendInterfaceIdVo(outBoundChannel, outBoundResponse.getPayload(), retClass);
        }
        if (outBoundChannel.getSyncTypeEnum() == SyncType.PUBSUB) { 
            // int i=51;
            // String globalId = new String(retByte).substring(i,i+34);
            // System.out.println(globalId); 
            // byte[] reciveMsg = outBoundChannel.sendAndReceive(interfaceIdVo.getGlobalId(this.message), retByte);// ASync FEP
            OutBoundResponse outBoundResponse = outBoundChannel.sendAndReceive(outBoundRequest); 
            retMessageInterfaceVO = (MessageInterfaceVO) getMakeSendInterfaceIdVo(outBoundChannel, outBoundResponse.getPayload(), retClass);
        }
        else {
            // outBoundChannel.send(retByte);// ASync
            outBoundChannel.send(outBoundRequest);
            return null;// ????????? ??????
        }
        
        // retByte
        return retMessageInterfaceVO;
    }
    
    public byte[] getMakeErrorMessageOutBoundByte(BoundChannel boundChannel) throws MessageParserException {
        // ???????????? ????????????
        byte[] retByte = null;
        this.channelId = boundChannel.getChannelId();
        String relationId = "";
        try {
            charset = boundChannel.getCharsetEnum();
            relationId = boundChannel.getRelationId();
            MessageManager manager = new MessageManager(relationId);
            
            this.message = manager.getErrMessage(charset);
            
            MessageHeader instance = (MessageHeader) getClassInstance(TelegramConstant.TELE_UTIL_PAKAGE + relationId + "_MessageSendUtil");
            
            instance.makeErrHeader(message);
            
            // message.essentialValueCheck();
            
            retByte = message.getMessageHeaderBytes();
            
            retByte = setLengthPrefixPattern(retByte);
            
        }
        catch (MessageParserException e) {
            e.printStackTrace();
        }
        return retByte;
    }
    
    public byte[] setLengthPrefixPattern(byte[] msgByte) {
        // retByte ??? 6byte??? ????????? ?????? ???????????????.
        int totMsgSize = 0;
        totMsgSize = msgByte.length - LENGTH_PREFIX_PATTERN.length();
        byte[] temp = new byte[totMsgSize];
        System.arraycopy(msgByte, LENGTH_PREFIX_PATTERN.length(), temp, 0, temp.length);
        String totLength = StringUtil.lpad(totMsgSize, LENGTH_PREFIX_PATTERN.length(), '0');
        System.arraycopy(totLength.getBytes(), 0, msgByte, 0, LENGTH_PREFIX_PATTERN.length());
        
        return msgByte;
    }
    
    public static Object getClassInstance(Class<?> pvResultClass) throws MessageParserException {
        try {
            return pvResultClass.newInstance();
        }
        catch (InstantiationException e) {
            throw new MessageParserException(ErrorConstant.TELEGRAM_CLASS_INSTANCE_ERROR, "className=[" + pvResultClass.getName() + "]", e);// Class Instance ?????? ?????? ???????????????.[{0}]
        }
        catch (IllegalAccessException e) {
            throw new MessageParserException(ErrorConstant.TELEGRAM_CLASS_ACCESS_ERROR, "className=[" + pvResultClass.getName() + "]", e);// Class Instance ???????????? ???????????????.[{0}]
        }
    }
    
    public static Object getClassInstance(String className) throws MessageParserException {
        Class<?> cls = getClass(className);
        return getClassInstance(cls);
        
    }
    
    public static Class<?> getClass(String className) throws MessageParserException {
        Class<?> cls = null;
        
        try {
            
            ServiceRegister serviceRegister = ApplicationContextSupport.getBean(ServiceRegister.class);
            
            cls = Class.forName(className, true, serviceRegister.getBizClassLoader());
        }
        catch (ClassNotFoundException e) {
            throw new MessageParserException(ErrorConstant.TELEGRAM_CLASS_NOT_FOUND_ERROR, "className=[" + className + "]", e);// Class??? ?????? ??? ????????????.[{0}]
        }
        return cls;
    }
    
    public boolean isUseDynamicHeader(String dynamicHeaderId) {
        return model.getDynamic_header(dynamicHeaderId).isUseHeadModel();
    }
    
    public Message getMessage() {
        return this.message;
    }
    
 
}
