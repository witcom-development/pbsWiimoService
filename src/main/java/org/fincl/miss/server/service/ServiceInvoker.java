/*
 * Copyright 2002-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fincl.miss.server.service;

import java.lang.reflect.InvocationTargetException;

import org.fincl.miss.server.annotation.EnableTraceLogging;
import org.fincl.miss.server.channel.ChannelManagerImpl;
import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.ServiceInvokeException;
import org.fincl.miss.server.message.MessageHeader;
import org.fincl.miss.server.message.MessageInterfaceVO;
import org.fincl.miss.server.message.MessageVO;
import org.fincl.miss.server.service.metadata.NotRegisteredServiceException;
import org.fincl.miss.server.service.metadata.ServiceMetadata;
import org.fincl.miss.server.service.metadata.ServiceRequest;
import org.fincl.miss.server.service.metadata.UnserviceableTimeException;
import org.fincl.miss.server.service.metadata.UnusableServiceException;
import org.fincl.miss.server.service.metadata.impl.ServiceRequestImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * EmployeeSearchService.java: This is the default employee search service
 * 
 * @author Vigil Bose
 */
@Scope("prototype")
@Component
public class ServiceInvoker {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private ServiceRegister serviceRegister;
    
    @Autowired
    private ChannelManagerImpl channelManager;
    
    @EnableTraceLogging
    public MessageInterfaceVO invoke(MessageInterfaceVO interfaceIdVo, MessageHeader messageHeader) throws ServiceInvokeException {
        
        Object rt = null;
        
        try {
            ServiceRequest serviceRequest = new ServiceRequestImpl(messageHeader.getClientIp(), messageHeader.getTimestamp());
            ServiceMetadata serviceMetadata = serviceRegister.getServiceMetadata(interfaceIdVo.getServiceId(), serviceRequest);
            Object bizServiceClass = serviceRegister.getBizApplicationContext().getBean(serviceMetadata.getHandlerClass());
            rt = serviceMetadata.getHandlerMethod().invoke(bizServiceClass, interfaceIdVo);
        }
        catch (IllegalAccessException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new ServiceInvokeException(ErrorConstant.SERVICE_INVOKE_ERROR, e.getCause());
        }
        catch (IllegalArgumentException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new ServiceInvokeException(ErrorConstant.SERVICE_INVOKE_ERROR, e.getCause());
        }
        catch (InvocationTargetException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new ServiceInvokeException(ErrorConstant.SERVICE_INVOKE_ERROR, e.getCause());
        }
        catch (NotRegisteredServiceException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new ServiceInvokeException(e.getCode(), e);
        }
        catch (UnusableServiceException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new ServiceInvokeException(e.getCode(), e);
        }
        catch (UnserviceableTimeException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new ServiceInvokeException(e.getCode(), e);
        }
        return (MessageVO) rt;
    }
}
