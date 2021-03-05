package org.fincl.miss.server.channel.outbound;

import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.fincl.miss.core.context.ApplicationContextSupport;
import org.fincl.miss.core.server.channel.OutBoundChannel;
import org.fincl.miss.core.server.channel.OutBoundRequest;
import org.fincl.miss.core.server.channel.OutBoundResponse;
import org.fincl.miss.server.channel.BoundChannel;
import org.fincl.miss.server.channel.outbound.pool.OutBoundChannelSenderPool;
import org.fincl.miss.server.channel.outbound.sender.OutBoundSender;
import org.fincl.miss.server.channel.outbound.sender.http.OutBoundHttpSender;
import org.fincl.miss.server.channel.outbound.sender.rest.OutBoundRestSender;
import org.fincl.miss.server.channel.outbound.sender.tcp.OutBoundNoneSingleTcpSender;
import org.fincl.miss.server.channel.outbound.sender.tcp.OutBoundSingleTcpSender;
import org.fincl.miss.server.channel.outbound.sender.websocket.OutBoundNoneSingleWebSocketSender;
import org.fincl.miss.server.channel.outbound.sender.websocket.OutBoundSingleWebSocketSender;
import org.fincl.miss.server.channel.service.Channel;
import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.OutBoundSenderException;
import org.fincl.miss.server.exeption.ServerException;
import org.fincl.miss.server.logging.db.ServiceLogging;
import org.fincl.miss.server.logging.db.ServiceLoggingHandler;
import org.fincl.miss.server.message.GuidTopicMessage;
import org.fincl.miss.server.message.GuidTopicMessageListener;
import org.fincl.miss.server.message.MessageHeader;
import org.fincl.miss.server.service.ServiceHandler;
import org.fincl.miss.server.service.ServiceMessageHeaderContext;
import org.fincl.miss.server.util.EnumCode.ChannelStatus;
import org.fincl.miss.server.util.EnumCode.ProtocolType;
import org.fincl.miss.server.util.EnumCode.SingleYn;
import org.fincl.miss.server.util.EnumCode.SyncType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.hazelcast.core.ITopic;

@Scope("prototype")
@Component
public class OutBoundChannelImpl extends BoundChannel implements OutBoundChannel {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private ApplicationContext applicationContext;
    
    private OutBoundChannelSenderPool senderPool;
    
    private String beanName;
    
    @Autowired
    private ServiceLoggingHandler serviceLoggingHandler;
    
    private GenericFutureListener<ChannelFuture> closeListener;
    
    public OutBoundChannelImpl() {
        
    }
    
    public OutBoundChannelImpl(final ServiceHandler serviceHandler, final Channel channelVO) {
        super(serviceHandler, channelVO);
        initSenderPool();
    }
    
    private void initSenderPool() {
        ProtocolType protocolType = ProtocolType.getEnum(getProtocolTypeCode());
        if (protocolType == ProtocolType.HTTP) {
            // senderPool = new OutBoundChannelSenderPool(this, OutBoundHttpSender.class);
            senderPool = ApplicationContextSupport.getBean(OutBoundChannelSenderPool.class, new Object[] { this, OutBoundHttpSender.class, getThreadCount() });
        }
        else if (protocolType == ProtocolType.REST) {
            // senderPool = new OutBoundChannelSenderPool(this, OutBoundRestSender.class);
            senderPool = ApplicationContextSupport.getBean(OutBoundChannelSenderPool.class, new Object[] { this, OutBoundRestSender.class, getThreadCount() });
        }
        else if (protocolType == ProtocolType.SERVLET) {
            senderPool = ApplicationContextSupport.getBean(OutBoundChannelSenderPool.class, new Object[] { this, OutBoundRestSender.class, getThreadCount() });
        }
        else if (protocolType == ProtocolType.TCP) {
            SingleYn singleYn = SingleYn.getEnum(getSingleYn());
            
            if (singleYn == SingleYn.YES) {
                // senderPool = new OutBoundChannelSenderPool(this, OutBoundSingleTcpSender.class);
                System.out.println("out [" + getChannelId() + "] single===");
                senderPool = ApplicationContextSupport.getBean(OutBoundChannelSenderPool.class, new Object[] { this, OutBoundSingleTcpSender.class, getThreadCount() });
            }
            else if (singleYn == SingleYn.NO) {
                System.out.println("out [" + getChannelId() + "] none single===");
                // senderPool = new OutBoundChannelSenderPool(this, OutBoundNoneSingleTcpSender.class);
                senderPool = ApplicationContextSupport.getBean(OutBoundChannelSenderPool.class, new Object[] { this, OutBoundNoneSingleTcpSender.class, getThreadCount() });
                setCloseListener(new GenericFutureListener<ChannelFuture>() {
                    
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        
                        shutdown();
                        
                    }
                });
            }
            else {
                
            }
        }
        else if (protocolType == ProtocolType.WEBSOCKET) {
            SingleYn singleYn = SingleYn.getEnum(getSingleYn());
            
            if (singleYn == SingleYn.YES) {
                // senderPool = new OutBoundChannelSenderPool(this, OutBoundSingleWebSocketSender.class);
                senderPool = ApplicationContextSupport.getBean(OutBoundChannelSenderPool.class, new Object[] { this, OutBoundSingleWebSocketSender.class, getThreadCount() });
            }
            else if (singleYn == SingleYn.NO) {
                // senderPool = new OutBoundChannelSenderPool(this, OutBoundNoneSingleWebSocketSender.class);
                senderPool = ApplicationContextSupport.getBean(OutBoundChannelSenderPool.class, new Object[] { this, OutBoundNoneSingleWebSocketSender.class, getThreadCount() });
                setCloseListener(new GenericFutureListener<ChannelFuture>() {
                    
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        
                        shutdown();
                        
                    }
                });
            }
            else {
                
            }
        }
    }
    
    public OutBoundResponse sendAndReceive(OutBoundRequest outBoundRequest) {
        
        if (senderPool == null) {
            initSenderPool();
        }
        
        Map<String, Object> headers = new HashMap<String, Object>();
        MessageHeader messageHeader = new MessageHeader(headers);
        headers = new HashMap<String, Object>();
        System.out.println(">>" + ServiceMessageHeaderContext.getMessageHeader().get(MessageHeader.ID));
        headers.put(MessageHeader.CORRELATION_ID, ServiceMessageHeaderContext.getMessageHeader().get(MessageHeader.ID));
        // /
        try {
            InetAddress ip = InetAddress.getLocalHost();
            headers.put(MessageHeader.CLIENT_IP, ip.getHostAddress());
        }
        catch (UnknownHostException e) {
            throw new OutBoundSenderException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
        }
        messageHeader.putAll(headers);
        
        OutBoundSender sender = null;
        String resultCode = ErrorConstant.SUCCESS;
        String resultMessage = null;
        
        OutBoundResponse outBoundResponse = new OutBoundResponse();
        
        long startTime = System.currentTimeMillis();
        
        ITopic<GuidTopicMessage> pubSubTopic = null;
        GuidTopicMessageListener guidTopicMessageListener = null;
        String listener = null;
        
        try {
            
            if (getSyncTypeEnum() == SyncType.PUBSUB && StringUtils.isEmpty(outBoundRequest.getGuid())) {
                throw new OutBoundSenderException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, "could not empty guid " + SyncType.PUBSUB + " channel.");
            }
            
            logger.debug("senderPool: {}", senderPool);
            
            if (getSyncTypeEnum() == SyncType.PUBSUB) {
                pubSubTopic = (ITopic<GuidTopicMessage>) ApplicationContextSupport.getBean("pubSubMessage");
                guidTopicMessageListener = new GuidTopicMessageListener(outBoundRequest.getGuid(), getTimeoutSeconds());
                listener = pubSubTopic.addMessageListener(guidTopicMessageListener);
            }
            
            sender = senderPool.borrowObject();
            System.out.println("sender::" + sender);
            
            logger.debug("sendMessage");
            
            if (getSyncTypeEnum() == SyncType.PUBSUB) {
                // PUBSUB 채널은 비동기 채널로 송신 채널과 수신 채널이 다름.
                try {
                    
                    sender.send(outBoundRequest.getPayload());
                    
                    GuidTopicMessage guidTopicMessage = guidTopicMessageListener.getResponse();
                    System.out.println("guidTopicMessage::" + guidTopicMessage);
                    outBoundResponse.setPayload(guidTopicMessage.getPayload());
                }
                catch (OutBoundSenderException ex) {
                    throw ex;
                }
                catch (RuntimeException ex) {
                    if (logger.isDebugEnabled()) {
                        ex.printStackTrace();
                    }
                    throw new OutBoundSenderException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, ex);
                }
                finally {
                    pubSubTopic.removeMessageListener(listener);
                }
                logger.debug("getMessage");
            }
            else {
                outBoundResponse.setPayload(sender.sendAndReceive(outBoundRequest.getPayload()));
            }
        }
        catch (Exception e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            if (e instanceof OutBoundSenderException) {
                resultCode = ((OutBoundSenderException) e).getCode();
                resultMessage = ((OutBoundSenderException) e).getMessage();
                throw (OutBoundSenderException) e;
            }
            else {
                OutBoundSenderException ex = new OutBoundSenderException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
                resultCode = ex.getCode();
                resultMessage = ex.getMessage();
                throw ex;
            }
        }
        finally {
            
            try {
                if (sender != null) {
                    senderPool.returnObject(sender);
                }
            }
            catch (Exception e) {
                if (logger.isDebugEnabled()) {
                    e.printStackTrace();
                }
                OutBoundSenderException ex = new OutBoundSenderException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
                resultCode = ex.getCode();
                resultMessage = e.getMessage();
                throw ex;
            }
            
            long endTime = System.currentTimeMillis();
            
            ServiceLogging serviceLogging = new ServiceLogging();
            
            serviceLogging.setEndTime(endTime);
            serviceLogging.setStartTime(startTime);
            serviceLogging.setExtChannel(this);
            serviceLogging.setServiceId(outBoundRequest.getServiceId());
            serviceLogging.setInterfaceId(outBoundRequest.getInterfaceId());
            serviceLogging.setGuid(outBoundRequest.getGuid());
            serviceLogging.setResultCode(resultCode);
            serviceLogging.setResultMessage(resultMessage);
            serviceLogging.setReqMessage(outBoundRequest.getPayload());
            serviceLogging.setResMessage(outBoundResponse.getPayload());
            serviceLogging.setMessageHeader(messageHeader);
            
            serviceLoggingHandler.addLog(serviceLogging);
        }
        
        return outBoundResponse;
    }
    
    public void send(OutBoundRequest outBoundRequest) {
        if (getSyncTypeEnum() == SyncType.ASYNC) {
            sendAndReceive(outBoundRequest);
        }
        else {
            throw new OutBoundSenderException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, "this channel only " + SyncType.ASYNC + " supported.");
        }
    }
    
    /**
     * 
     * 
     * @param message
     * @return
     */
    // @EnableTraceLogging
    // public byte[] sendAndReceive(String guid, byte[] bMessage) {
    //
    // if (senderPool == null) {
    // initSenderPool();
    // }
    //
    // System.out.println("mkmessageheader:" + ServiceMessageHeaderContext.getMessageHeader());
    // // /
    // Map<String, Object> headers = new HashMap<String, Object>();
    // MessageHeader messageHeader = new MessageHeader(headers);
    // headers = new HashMap<String, Object>();
    // headers.put(MessageHeader.CORRELATION_ID, ServiceMessageHeaderContext.getMessageHeader().get(MessageHeader.ID));
    // // /
    // try {
    // InetAddress ip = InetAddress.getLocalHost();
    // headers.put(MessageHeader.CLIENT_IP, ip.getHostAddress());
    // }
    // catch (UnknownHostException e) {
    // throw new OutBoundSenderException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
    // }
    // messageHeader.putAll(headers);
    //
    // OutBoundSender sender = null;
    // String resultCode = ErrorConstant.SUCCESS;
    // String resultMessage = null;
    // byte[] bRes = null;
    //
    // long startTime = System.currentTimeMillis();
    //
    // ITopic<GuidTopicMessage> pubSubTopic = null;
    // String listener = null;
    //
    // try {
    //
    // if (getSyncTypeEnum() != SyncType.PUBSUB) {
    // throw new OutBoundSenderException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, "this channel is not " + SyncType.PUBSUB + ".");
    // }
    //
    // if (StringUtils.isEmpty(guid)) {
    // throw new OutBoundSenderException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, "could not empty guid.");
    // }
    //
    // logger.debug("senderPool: {}", senderPool);
    //
    // pubSubTopic = (ITopic<GuidTopicMessage>) ApplicationContextSupport.getBean("pubSubMessage");
    // GuidTopicMessageListener guidTopicMessageListener = new GuidTopicMessageListener(guid, getTimeoutSeconds());
    // listener = pubSubTopic.addMessageListener(guidTopicMessageListener);
    //
    // sender = senderPool.borrowObject();
    // System.out.println("sender::" + sender);
    // sender.sendMessage(bMessage);
    // logger.debug("sendMessage");
    // try {
    // GuidTopicMessage guidTopicMessage = guidTopicMessageListener.getResponse();
    // System.out.println("guidTopicMessage::" + guidTopicMessage);
    // bRes = guidTopicMessage.getPayload();
    // }
    // catch (OutBoundSenderException ex) {
    // throw ex;
    // }
    // catch (RuntimeException ex) {
    // if (logger.isDebugEnabled()) {
    // ex.printStackTrace();
    // }
    // throw new OutBoundSenderException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, ex);
    // }
    // finally {
    // pubSubTopic.removeMessageListener(listener);
    // }
    // logger.debug("getMessage");
    // }
    // catch (Exception e) {
    // if (logger.isDebugEnabled()) {
    // e.printStackTrace();
    // }
    // if (e instanceof OutBoundSenderException) {
    // resultCode = ((OutBoundSenderException) e).getCode();
    // resultMessage = ((OutBoundSenderException) e).getMessage();
    // throw (OutBoundSenderException) e;
    // }
    // else {
    // OutBoundSenderException ex = new OutBoundSenderException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
    // resultCode = ex.getCode();
    // resultMessage = ex.getMessage();
    // throw ex;
    // }
    // }
    // finally {
    //
    // try {
    // if (sender != null) {
    // senderPool.returnObject(sender);
    // }
    // }
    // catch (Exception e) {
    // if (logger.isDebugEnabled()) {
    // e.printStackTrace();
    // }
    // OutBoundSenderException ex = new OutBoundSenderException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
    // resultCode = ex.getCode();
    // resultMessage = e.getMessage();
    // throw ex;
    // }
    //
    // long endTime = System.currentTimeMillis();
    //
    // ServiceLogging serviceLogging = new ServiceLogging();
    //
    // serviceLogging.setEndTime(endTime);
    // serviceLogging.setStartTime(startTime);
    // serviceLogging.setExtChannel(this);
    // serviceLogging.setGuid(guid);
    // serviceLogging.setResultCode(resultCode);
    // serviceLogging.setResultMessage(resultMessage);
    // serviceLogging.setReqMessage(bMessage);
    // serviceLogging.setResMessage(bRes);
    //
    // serviceLoggingHandler.addLog(serviceLogging);
    // }
    // return bRes;
    // }
    
    // @EnableTraceLogging
    // public byte[] sendAndReceive(byte[] bMessage) {
    //
    // if (senderPool == null) {
    // initSenderPool();
    // }
    //
    // System.out.println("messageHeader77:" + ServiceMessageHeaderContext.getMessageHeader());
    // // /
    // Map<String, Object> headers = new HashMap<String, Object>();
    // MessageHeader messageHeader = new MessageHeader(headers);
    // headers = new HashMap<String, Object>();
    // headers.put(MessageHeader.CORRELATION_ID, ServiceMessageHeaderContext.getMessageHeader().get(MessageHeader.ID));
    //
    // try {
    // InetAddress ip = InetAddress.getLocalHost();
    // headers.put(MessageHeader.CLIENT_IP, ip.getHostAddress());
    // }
    // catch (UnknownHostException e) {
    // throw new OutBoundSenderException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
    // }
    // messageHeader.putAll(headers);
    //
    // OutBoundSender sender = null;
    //
    // String resultCode = ErrorConstant.SUCCESS;
    // String resultMessage = null;
    // byte[] bRes = null;
    //
    // long startTime = System.currentTimeMillis();
    //
    // try {
    // logger.debug("senderPool: {}", senderPool);
    // System.out.println("getNumActive" + senderPool.getNumActive());
    // System.out.println("getNumIdle" + senderPool.getNumIdle());
    // sender = senderPool.borrowObject();
    // logger.debug("senderPool: sender {}", sender);
    // bRes = sender.sendMessageAndReceive(bMessage);
    //
    // }
    // catch (Exception e) {
    // if (logger.isDebugEnabled()) {
    // e.printStackTrace();
    // }
    // OutBoundSenderException ex = null;
    // if (e.getCause() == null) {
    // ex = new OutBoundSenderException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
    // }
    // else {
    // if (e.getCause().getCause() == null) {
    // ex = new OutBoundSenderException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e.getCause());
    // }
    // else {
    // ex = new OutBoundSenderException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e.getCause().getCause());
    // }
    // }
    // resultCode = ex.getCode();
    // resultMessage = e.getMessage();
    // throw ex;
    // }
    // finally {
    // try {
    // if (sender != null) {
    // senderPool.returnObject(sender);
    // }
    // }
    // catch (Exception e) {
    // if (logger.isDebugEnabled()) {
    // e.printStackTrace();
    // }
    // OutBoundSenderException ex = new OutBoundSenderException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
    // resultCode = ex.getCode();
    // resultMessage = e.getMessage();
    // throw ex;
    // }
    //
    // long endTime = System.currentTimeMillis();
    //
    // ServiceLogging serviceLogging = new ServiceLogging();
    //
    // serviceLogging.setEndTime(endTime);
    // serviceLogging.setStartTime(startTime);
    // serviceLogging.setExtChannel(this);
    // serviceLogging.setResultCode(resultCode);
    // serviceLogging.setResultMessage(resultMessage);
    // serviceLogging.setReqMessage(bMessage);
    // serviceLogging.setResMessage(bRes);
    //
    // serviceLoggingHandler.addLog(serviceLogging);
    // }
    // return bRes;
    // }
    
    // @EnableTraceLogging
    // public void send(byte[] bMessage) {
    //
    // if (senderPool == null) {
    // initSenderPool();
    // }
    //
    // // /
    // Map<String, Object> headers = new HashMap<String, Object>();
    // MessageHeader messageHeader = new MessageHeader(headers);
    // headers = new HashMap<String, Object>();
    // headers.put(MessageHeader.CORRELATION_ID, ServiceMessageHeaderContext.getMessageHeader().get(MessageHeader.ID));
    // // /
    // try {
    // InetAddress ip = InetAddress.getLocalHost();
    // headers.put(MessageHeader.CLIENT_IP, ip.getHostAddress());
    // }
    // catch (UnknownHostException e) {
    // throw new OutBoundSenderException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
    // }
    // messageHeader.putAll(headers);
    //
    // OutBoundSender sender = null;
    //
    // String resultCode = ErrorConstant.SUCCESS;
    // String resultMessage = null;
    //
    // long startTime = System.currentTimeMillis();
    //
    // try {
    // logger.debug("senderPool: {}", senderPool);
    // sender = senderPool.borrowObject();
    // sender.sendMessage(bMessage);
    // }
    // catch (Exception e) {
    // if (logger.isDebugEnabled()) {
    // e.printStackTrace();
    // }
    // OutBoundSenderException ex = new OutBoundSenderException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
    // resultCode = ex.getCode();
    // resultMessage = e.getMessage();
    // throw ex;
    // }
    // finally {
    // try {
    // if (sender != null) {
    // senderPool.returnObject(sender);
    // }
    // }
    // catch (Exception e) {
    // if (logger.isDebugEnabled()) {
    // e.printStackTrace();
    // }
    // OutBoundSenderException ex = new OutBoundSenderException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
    // resultCode = ex.getCode();
    // resultMessage = e.getMessage();
    // throw ex;
    // }
    //
    // long endTime = System.currentTimeMillis();
    //
    // ServiceLogging serviceLogging = new ServiceLogging();
    //
    // serviceLogging.setEndTime(endTime);
    // serviceLogging.setStartTime(startTime);
    // serviceLogging.setExtChannel(this);
    // serviceLogging.setResultCode(resultCode);
    // serviceLogging.setResultMessage(resultMessage);
    // serviceLogging.setReqMessage(bMessage);
    //
    // serviceLoggingHandler.addLog(serviceLogging);
    // }
    // }
    
    @Override
    public boolean startup() throws ServerException {
        
        boolean bResult = false;
        if (senderPool != null) {
            bResult = senderPool.startup();
        }
        return bResult;
    }
    
    @Override
    public boolean shutdown() throws ServerException {
        boolean bResult = false;
        if (senderPool != null) {
            bResult = senderPool.shutdown();
            senderPool = null;
        }
        return bResult;
    }
    
    @Override
    public ChannelStatus getStatus() throws ServerException {
        return senderPool.getStatus();
    }
    
    public GenericFutureListener<ChannelFuture> getCloseListener() {
        return closeListener;
    }
    
    public void setCloseListener(GenericFutureListener<ChannelFuture> closeListener) {
        this.closeListener = closeListener;
    }
    
}
