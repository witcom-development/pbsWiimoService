package org.fincl.miss.server.message.parser;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.fincl.miss.server.annotation.EnableTraceLogging;
import org.fincl.miss.server.channel.BoundChannel;
import org.fincl.miss.server.exeption.MessageParserException;
import org.fincl.miss.server.message.MessageInterfaceVO;
import org.fincl.miss.server.message.MessageHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.dkitec.cfood.core.CfoodException;

@Component
public class StressMessageParser extends MessageParser {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @EnableTraceLogging
    @Override
    public MessageInterfaceVO parse(BoundChannel extChannel, byte[] bMessage) throws MessageParserException {
        // // 앞에 1byte는 payload의 길이 필드여서 잘리냄
        // StopWatch stopWatch = new StopWatch();
        // stopWatch.start();
        //
        // Class<MessageVo<?>> parameterVo = serviceRegister.getServiceMethodParameterVo("form");
        // MessageVo<BicycleHeaderVo> vo = null;
        //
        // try {
        // vo = (MessageVo) parameterVo.newInstance();
        // BeanUtils.setProperty(vo, "payload", bMessage);
        // }
        // catch (InstantiationException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // catch (IllegalAccessException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // catch (InvocationTargetException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        //
        // vo.setServiceId("stressService");
        //
        // return vo;
        
        return null;
    }
    
    @EnableTraceLogging
    @Override
    public byte[] build(BoundChannel extChannel, MessageInterfaceVO messageInterfaceVo) throws MessageParserException {
        byte[] bb = null;
        try {
            // System.out.println("aaa: " + interfaceIdVo);
            Map m = BeanUtils.describe(messageInterfaceVo);
            Object ob = PropertyUtils.getProperty(messageInterfaceVo, "payload");
            bb = (byte[]) ob;
        }
        catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // System.out.println("nn :" + new String(bb));
        return bb;
    }
    
    @Override
    public byte[] buildError(BoundChannel extChannel, MessageHeader messageHeader, CfoodException ex) {
        // TODO Auto-generated method stub
        return null;
    }
}
