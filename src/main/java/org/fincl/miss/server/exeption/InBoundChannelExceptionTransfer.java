package org.fincl.miss.server.exeption;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.Charset;

import org.apache.http.entity.ContentType;
import org.aspectj.lang.JoinPoint;
import org.fincl.miss.server.channel.inbound.InBoundServerHandler;
import org.fincl.miss.server.logging.db.ServiceLogging;
import org.fincl.miss.server.logging.db.ServiceLoggingHandler;
import org.fincl.miss.server.message.MessageHandler;
import org.fincl.miss.server.message.MessageHeader;
import org.fincl.miss.server.service.ServiceMessageHeaderContext;
import org.fincl.miss.server.util.EnumCode.ResponseDataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.dkitec.cfood.core.CfoodException;

@ControllerAdvice
public class InBoundChannelExceptionTransfer {
    
    @Autowired
    private MessageHandler messageHandler;
    
    @Autowired
    private ServiceLoggingHandler serviceLoggingHandler;
    
    private static enum ChannelType {
        LISTEN_CHANNEL, SERVELT_CHANNEL
    }
    
    // pointcut 에 정의된 메소드에서 에러가 발생했을 때 transfer 메소드가 호출된다.
    public void transfer(JoinPoint thisJoinPoint, Exception exception) throws ServerException {
        
        Object[] args = thisJoinPoint.getArgs();
        ChannelType channelType = ChannelType.SERVELT_CHANNEL;
        for (Object obj : args) {
            if (obj instanceof io.netty.channel.ChannelHandlerContext) {
                channelType = ChannelType.LISTEN_CHANNEL;
                responseListenError((InBoundServerHandler) thisJoinPoint.getTarget(), (ChannelHandlerContext) obj, exception);
                break;
            }
            //
            // if (obj instanceof ResponseEntity<?>) {
            // channelType = ChannelType.SERVELT_CHANNEL;
            // responseServletError((InBoundServerHandler) thisJoinPoint.getTarget(), (ResponseEntity<?>) obj, exception);
            // break;
            // }
        }
        
        if (channelType == ChannelType.SERVELT_CHANNEL) {
            responseServletError((InBoundServerHandler) thisJoinPoint.getTarget(), exception);
        }
        
        // Log logger = LogFactory.getLog(thisJoinPoint.getTarget().getClass());
        //
        // if (exception instanceof EmpException) {
        // EmpException empEx = (EmpException) exception;
        // logger.error(empEx.getMessage(), empEx);
        // throw empEx;
        // }
        //
        // if (exception instanceof QueryServiceException) {
        // logger.error(messageSource.getMessage("error." + className + "." + opName + ".query", new String[] {}, Locale.getDefault()), exception);
        // throw new EmpException(messageSource.getMessage("error." + className + "." + opName + ".query", new String[] {}, Locale.getDefault()), exception);
        // }
        // else {
        // logger.error(messageSource.getMessage("error." + className + "." + opName, new String[] {}, Locale.getDefault()), exception);
        // throw new EmpException(messageSource.getMessage("error." + className + "." + opName, new String[] {}, Locale.getDefault()), exception);
        // }
    }
    
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<?> inBoundServletExceptionHandler(Exception ex) {
        ResponseEntity<?> responseEntity = null;
        System.out.println("inBoundServletExceptionHandler : " + ex);
        if (ex instanceof ServerException) {
            ServerException serverException = (ServerException) ex;
            responseEntity = new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        else {
            InBoundServletException inBoundServletException = (InBoundServletException) ex;
            responseEntity = responseServletError(inBoundServletException.getInBoundServerHandler(), ex);
        }
        return responseEntity;
    }
    
    private void responseListenError(InBoundServerHandler inBoundServerHandler, ChannelHandlerContext ctx, Exception ex) {
        MessageHeader messageHeader = new MessageHeader(inBoundServerHandler.getHeader());
        
        String resultCode = ErrorConstant.SUCCESS;
        String resultMessage = null;
        byte[] bMessage = null;
        
        if (ex instanceof CfoodException) {
            System.out.println("responseListenError 1 :" + ex);
            if (ex instanceof ServerException) {
                bMessage = ((ServerException) ex).getParserMessage();
            }
            resultCode = ((CfoodException) ex).getCode();
            resultMessage = ex.getMessage();
            
        }
        else {
            System.out.println("responseListenError 2 :" + ex);
            resultCode = ErrorConstant.CHANNEL_ERROR;
            resultMessage = ex.getMessage();
        }
        
        byte[] bError = messageHandler.buildError(inBoundServerHandler.getInBoundChannel(), messageHeader, new CfoodException(resultCode));
        ctx.writeAndFlush(bError);
        
        ServiceLogging serviceLogging = new ServiceLogging();
        
        long endTime = System.currentTimeMillis();
        serviceLogging.setEndTime(endTime);
        serviceLogging.setStartTime(messageHeader.getTimestamp());
        serviceLogging.setExtChannel(inBoundServerHandler.getInBoundChannel());
        serviceLogging.setResultCode(resultCode);
        serviceLogging.setResultMessage(resultMessage);
        // serviceLoggingVO.setReqMessageInterfaceVO(reqMessageInterfaceVO);
        // serviceLoggingVO.setResMessageInterfaceVO(resMessageInterfaceVO);
        serviceLogging.setReqMessage(bMessage);
        serviceLogging.setResMessage(bError);
        
        serviceLoggingHandler.addLog(serviceLogging);
        
        ServiceMessageHeaderContext.remove();
    }
    
    private ResponseEntity<?> responseServletError(InBoundServerHandler inBoundServerHandler, Exception ex) {
        MessageHeader messageHeader = new MessageHeader(inBoundServerHandler.getHeader());
        
        MultiValueMap<String, String> mvm = new LinkedMultiValueMap<String, String>();
        
        if (inBoundServerHandler.getInBoundChannel().getResponseDataTypeEnum() == ResponseDataType.JSON) {
            mvm.add(CONTENT_TYPE, ContentType.create(ContentType.APPLICATION_JSON.getMimeType(), Charset.forName(inBoundServerHandler.getInBoundChannel().getCharsetEnum().toString())).toString());
        }
        else if (inBoundServerHandler.getInBoundChannel().getResponseDataTypeEnum() == ResponseDataType.XML) {
            mvm.add(CONTENT_TYPE, ContentType.create(ContentType.APPLICATION_XML.getMimeType(), Charset.forName(inBoundServerHandler.getInBoundChannel().getCharsetEnum().toString())).toString());
        }
        
        byte[] bMessage = null;
        String resultCode = ErrorConstant.SUCCESS;
        String resultMessage = null;
        System.out.println("responseServletError :" + ex);
        Throwable cause = ex.getCause();
        System.out.println("responseServletError cause :" + cause);
        
        String serviceId = null;
        
        if (cause instanceof ServerException) {
            System.out.println("responseServletError 1 :" + cause);
            ServerException serverException = (ServerException) cause;
            
            bMessage = serverException.getParserMessage();
            resultCode = serverException.getCode();
            resultMessage = serverException.getMessage();
        }
        else if (cause instanceof InBoundServletException) {
            System.out.println("responseServletError 2 :" + cause);
            InBoundServletException inBoundServletException = (InBoundServletException) ex;
            bMessage = inBoundServletException.getParserMessage();
            resultCode = inBoundServletException.getCode();
            resultMessage = inBoundServletException.getMessage();
            // if (ex.getCause() == null) {
            // resultCode = inBoundServletException.getCode();
            // resultMessage = inBoundServletException.getMessage();
            // }
            // else {
            // if (ex.getCause() instanceof ServiceInvokeException) {
            // ServiceInvokeException cause1 = (ServiceInvokeException) ex.getCause();
            // resultCode = cause1.getCode();
            // if (cause1.getCause() != null) {
            // resultMessage = cause1.getCause().getMessage();
            // }
            // else {
            // resultMessage = cause1.getMessage();
            // }
            // }
            // else {
            // resultCode = ErrorConstant.CHANNEL_ERROR;
            // resultMessage = ex.getCause().getMessage();
            // }
            // }
            
        }
        else {
            bMessage = new byte[0];
            resultCode = ErrorConstant.CHANNEL_ERROR;
            resultMessage = ex.getMessage();
        }
        
        System.out.println("resultCode:" + resultCode);
        System.out.println("resultMessage:" + resultMessage);
        
        byte[] bError = messageHandler.buildError(inBoundServerHandler.getInBoundChannel(), messageHeader, new CfoodException(resultCode));
        
        ServiceLogging serviceLogging = new ServiceLogging();
        
        long endTime = System.currentTimeMillis();
        serviceLogging.setEndTime(endTime);
        serviceLogging.setStartTime(messageHeader.getTimestamp());
        serviceLogging.setExtChannel(inBoundServerHandler.getInBoundChannel());
        serviceLogging.setResultCode(resultCode);
        serviceLogging.setResultMessage(resultMessage);
        // serviceLoggingVO.setReqMessageInterfaceVO(reqMessageInterfaceVO);
        // serviceLoggingVO.setResMessageInterfaceVO(resMessageInterfaceVO);
        serviceLogging.setServiceId(serviceId);
        serviceLogging.setReqMessage(bMessage);
        serviceLogging.setResMessage(bError);
        serviceLoggingHandler.addLog(serviceLogging);
        
        ResponseEntity<?> responseEntity = new ResponseEntity<byte[]>(bError, mvm, HttpStatus.OK);
        
        ServiceMessageHeaderContext.remove();
        
        return responseEntity;
    }
    
}