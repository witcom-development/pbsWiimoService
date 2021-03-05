package org.fincl.miss.server.service;

import io.netty.util.concurrent.FastThreadLocal;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PreDestroy;

import org.fincl.miss.core.context.ApplicationContextSupport;
import org.fincl.miss.server.annotation.EnableTraceLogging;
import org.fincl.miss.server.channel.BoundChannel;
import org.fincl.miss.server.channel.ChannelManagerImpl;
import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.MessageParserException;
import org.fincl.miss.server.exeption.ServerException;
import org.fincl.miss.server.exeption.ServiceInvokeException;
import org.fincl.miss.server.logging.db.ServiceLogging;
import org.fincl.miss.server.logging.db.ServiceLoggingHandler;
import org.fincl.miss.server.logging.logger.service.LoggingContextService;
import org.fincl.miss.server.logging.profile.TraceLog;
import org.fincl.miss.server.message.GuidTopicMessage;
import org.fincl.miss.server.message.MessageHandler;
import org.fincl.miss.server.message.MessageHeader;
import org.fincl.miss.server.message.MessageInterfaceVO;
import org.fincl.miss.server.util.EnumCode.SyncType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.dkitec.cfood.core.CfoodException;
import com.hazelcast.core.ITopic;

@Scope("prototype")
@Service("serviceHandler")
public class ServiceHandler {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private ServiceInvoker serviceInvoker;
    
    @Autowired
    private ThreadPoolTaskExecutor invokerTaskExecutor;
    
    @Autowired
    private ChannelManagerImpl channelManager;
    
    @Autowired
    private ServiceRegister serviceRegister;
    
    @Autowired
    private MessageHandler messageHandler;
    
    @Autowired
    private Properties serviceProps;
    
    @Autowired
    private ServiceLoggingHandler serviceLoggingHandler;
    
    @Autowired
    private LoggingContextService loggingContextService;
    
    private AtomicBoolean currentPause = new AtomicBoolean(false);
    
    public static FastThreadLocal<List<TraceLog>> ContextTraceLog = new FastThreadLocal<List<TraceLog>>() {
        @Override
        protected java.util.List<TraceLog> initialValue() throws Exception {
            return new ArrayList<TraceLog>();
        };
    };
    
    // public static ThreadLocal<List<TraceLog>> traceLog = new ThreadLocal<List<TraceLog>>() {
    // @Override
    // protected java.util.List<TraceLog> initialValue() {
    // return new ArrayList<TraceLog>();
    // }
    // };
    
    public ServiceHandler() {
        
    }
    
    /**
     * 페이로드 길이가 포함되어 있음.
     * 
     * @param extChannel
     * @param header
     * @param bMessage
     * @return
     */
    @EnableTraceLogging(isStart = true)
    public byte[] handle(BoundChannel extChannel, Map<String, Object> header, byte[] bMessage) throws ServerException {
       // System.out.println("header::" + header);
        /*
        if (logger.isDebugEnabled()) {
            logger.debug("none [{}]", new String(bMessage));
            logger.debug("default [{}]", new String(bMessage, Charset.defaultCharset()));
            try {
                logger.debug("EUC-KR [{}]", new String(bMessage, "EUC-KR"));
                logger.debug("UTF-8 [{}]", new String(bMessage, "UTF-8"));
                if (extChannel.getCharsetEnum() == null) {
                    logger.debug("CH[{}] [{}]", "null", new String(bMessage));
                }
                else {
                    logger.debug("CH[{}] [{}]", extChannel.getCharsetEnum().toString(), new String(bMessage, extChannel.getCharsetEnum().toString()));
                }
            }
            catch (UnsupportedEncodingException e1) {
                logger.warn(e1.getMessage());
            }
        }
        
        */
        while (currentPause.get()) {
            try {
               // logger.info("ServiceHandler current pause state waiting...");
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        // 현재 처리 카운드 1증가
        // System.out.println("activecount:" + invokerTaskExecutor.getActiveCount() + "==" + invokerTaskExecutor.getKeepAliveSeconds());
        MessageInterfaceVO reqMessageInterfaceVO = null;
        MessageInterfaceVO resMessageInterfaceVO = null;
        System.out.println("+++++++++++++++++++++++++++++++++++++"+header);
        MessageHeader messageHeader = new MessageHeader(header);
        ServiceMessageHeaderContext.setMessageHeader(messageHeader);
        byte[] rMessage = null;
        
        long startTime = System.currentTimeMillis();
        
        String guid = null;
        String serviceId = null;
        String interfaceId = null;
        
        try {
            reqMessageInterfaceVO = messageHandler.parse(extChannel, bMessage);
            
            if (reqMessageInterfaceVO == null) {
                throw new MessageParserException(ErrorConstant.MESSAGE_PARSER_ERROR, "Message parser result null.");
            }
            else {
                guid = reqMessageInterfaceVO.getGuid();
                serviceId = reqMessageInterfaceVO.getServiceId();
                interfaceId = reqMessageInterfaceVO.getInterfaceId();
            }
            
            // logging 처리
            loggingContextService.setContext(reqMessageInterfaceVO.getServiceId(), reqMessageInterfaceVO.getClientId());
            
            reqMessageInterfaceVO.setMessageHeader(messageHeader);
            
            // 만약 PubSub Channel의 경우 수신 메시지를 topic에 publish한다.
            if (extChannel.getSyncTypeEnum() == SyncType.PUBSUB) {
                // PubSub Channel의 경우 응답이 없고, 결국 build호출이 없다.
                if (reqMessageInterfaceVO.isInvokable()) {
                    resMessageInterfaceVO = serviceInvoker.invoke(reqMessageInterfaceVO, messageHeader);
                    resMessageInterfaceVO.setMessageHeader(reqMessageInterfaceVO.getMessageHeader());
                }
                else {
                    GuidTopicMessage topicMessage = new GuidTopicMessage(reqMessageInterfaceVO.getGuid());
                    topicMessage.setPayload(bMessage);
                    ITopic<GuidTopicMessage> pubsubTopic = (ITopic<GuidTopicMessage>) ApplicationContextSupport.getBean("pubSubMessage");
                    pubsubTopic.publish(topicMessage);
                }
                
                rMessage = new byte[0];
            }
            else {
                resMessageInterfaceVO = serviceInvoker.invoke(reqMessageInterfaceVO, messageHeader);
                if (resMessageInterfaceVO == null) {
                    rMessage = new byte[0];
                }
                else {
                    resMessageInterfaceVO.setMessageHeader(reqMessageInterfaceVO.getMessageHeader());
                    // ServiceInvokerTask serviceInvokerTask = ApplicationContextSupport.getBean(ServiceInvokerTask.class, new Object[] { serviceInvoker, reqInterfaceIdVo, messageHeader });
                    // Future<Object> futureTask = invokerTaskExecutor.submit(serviceInvokerTask);
                    // resInterfaceIdVo = (InterfaceIdVo) futureTask.get(30L, TimeUnit.SECONDS);
                    if (extChannel.getSyncTypeEnum() == SyncType.ASYNC) {
                        rMessage = new byte[0];
                    }
                    else if (extChannel.getSyncTypeEnum() == SyncType.SYNC || extChannel.getSyncTypeEnum() == SyncType.PUBSUB) {
                        rMessage = messageHandler.build(extChannel, resMessageInterfaceVO);
                    }
                    
                }
            }
            
            long endTime = System.currentTimeMillis();
            ServiceLogging serviceLogging = new ServiceLogging();
            
            serviceLogging.setEndTime(endTime);
            serviceLogging.setStartTime(startTime);
            serviceLogging.setExtChannel(extChannel);
            serviceLogging.setGuid(guid);
            serviceLogging.setServiceId(serviceId);
            serviceLogging.setInterfaceId(interfaceId);
            serviceLogging.setReqMessageInterfaceVO(reqMessageInterfaceVO);
            serviceLogging.setResMessageInterfaceVO(resMessageInterfaceVO);
            serviceLogging.setMessageHeader(messageHeader);
            
            System.out.println("+++++++++++++++++++++++++++++++++++++"+messageHeader);
            
            if (extChannel.getSyncTypeEnum() == SyncType.PUBSUB && !reqMessageInterfaceVO.isInvokable()) {
                // PubSub(FEP)의 경우 InBound채널로 응답을 받기 때문에, 요청 메시지를 응답 데이터에 넣음.
                serviceLogging.setResMessage(bMessage);
                serviceLogging.setResultCode(ErrorConstant.SUCCESS);
            }
            else {
                if (resMessageInterfaceVO == null) {
                    serviceLogging.setResultCode(ErrorConstant.SUCCESS);
                }
                else {
                    serviceLogging.setResultCode(resMessageInterfaceVO.getResultCode());
                }
                
                serviceLogging.setReqMessage(bMessage);
                serviceLogging.setResMessage(rMessage);
            }
            
            serviceLoggingHandler.addLog(serviceLogging);
            
            ServiceMessageHeaderContext.remove();
        }
        catch (ServiceInvokeException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            Throwable cause = e.getCause();
            if (cause instanceof CfoodException) {
                CfoodException cfe = (CfoodException) cause;
                ServerException se = new ServerException(cfe.getCode(), cfe, bMessage);
                throw se;
            }
            else {
                ServerException se = new ServerException(e.getCode(), e, bMessage);
                throw se;
            }
        }
        catch (MessageParserException e) {
            Throwable cause = e.getCause();
            if (cause instanceof CfoodException) {
                CfoodException cfe = (CfoodException) cause;
                ServerException se = new ServerException(cfe.getCode(), cfe, bMessage);
                throw se;
            }
            else {
                ServerException se = new ServerException(e.getCode(), e, bMessage);
                throw se;
            }
        }
        catch (Exception e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            ServerException se = new ServerException(ErrorConstant.SERVICE_INVOKE_ERROR, e.getCause(), bMessage);
            throw se;
        }
        finally {
            
        }
        
        // // resInterfaceIdVo = serviceInvoker.invoke(reqInterfaceIdVo);
        // try {
        // ServiceInvokerTask task = (ServiceInvokerTask) applicationContext.getBean("serviceInvokerTask", new Object[] { serviceInvoker, reqInterfaceIdVo });
        // Future<Object> future = invokerTaskExecutor.submit(task);
        // // Future<Object> future = invokerTaskExecutor.submit(new ServiceInvokerTask(serviceInvoker, "administratorService", reqInterfaceIdVo));
        // resInterfaceIdVo = (InterfaceIdVo) future.get(30, TimeUnit.SECONDS); // service별 타임아웃 설정할까?
        // // System.out.println("ret = " + resInterfaceIdVo);
        // }
        // catch (InterruptedException e) {
        // // TODO Auto-generated catch block
        // System.out.println("kkkk :" + e.getMessage());
        // e.printStackTrace();
        // }
        // catch (TaskRejectedException e) {
        // System.out.println("rrrr [handler에서 실행] :" + e.getMessage());
        // resInterfaceIdVo = serviceInvoker.invoke(reqInterfaceIdVo);
        // }
        // catch (ExecutionException e) {
        // // TODO Auto-generated catch block
        // System.out.println("eeee :" + e.getMessage());
        // e.printStackTrace();
        // }
        // catch (TimeoutException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        
        return rMessage;
    }
    
    public AtomicBoolean getCurrentPause() {
        return currentPause;
    }
    
    public void setCurrentPause(AtomicBoolean currentPause) {
        this.currentPause = currentPause;
    }
    
    @PreDestroy
    private void destroy() {
        // ThreadLocal 변수 관련하여 memory leak 가능성을 위한 처리
        logger.info("ServiceHandler destroy!!");
        ServiceHandler.ContextTraceLog.remove();
        ServiceHandler.ContextTraceLog = null;
        
        ServiceMessageHeaderContext.remove();
    }
    
}
