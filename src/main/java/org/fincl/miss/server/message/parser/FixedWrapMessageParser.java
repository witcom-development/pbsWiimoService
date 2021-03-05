package org.fincl.miss.server.message.parser;

import java.util.ArrayList;
import java.util.List;

import org.fincl.miss.server.channel.BoundChannel;
import org.fincl.miss.server.exeption.MessageParserException;
import org.fincl.miss.server.message.MessageHeader;
import org.fincl.miss.server.message.MessageInterfaceVO;

import com.dkitec.cfood.core.CfoodException;

public abstract class FixedWrapMessageParser extends MessageParser {
    
    protected static String requestRoot = "request";
    
    protected static String responseRoot = "response";
    
    protected static enum RequestFixedField {
        ID("id"), SERVICE_ID("serviceId"), PARAM("param");
        
        private final String name;
        
        RequestFixedField(String name) {
            this.name = name;
        }
        
        public String getValue() {
            return this.name;
        }
    };
    
    protected static enum ResponseFixedField {
        ID("id"), DATA("data"), CODE("code"), MSG("msg");
        
        private final String name;
        
        ResponseFixedField(String name) {
            this.name = name;
        }
        
        public String getValue() {
            return this.name;
        }
    };
    
    public static enum ResponseIgnoreFiled {
        CLASS("class"), ID("id"), INTERFACE_ID("interfaceId"), INVOKABLE("invokable"), GUID("guid"), SERVICE_ID("serviceId"), CLIENT_IP("clientIp"), CLINET_ID("clientId"), HEADER_VO_MAP("headerVoMap"), MESSAGE_HEADER("messageHeader"), RESULT_CODE("resultCode"), RESULT_MESSAGE("resultMessage");
        
        private final String name;
        
        ResponseIgnoreFiled(String name) {
            this.name = name;
        }
        
        public String getValue() {
            return this.name;
        }
        
        public String[] getArrayValues() {
            List<String> list = new ArrayList<String>();
            ResponseIgnoreFiled[] fields = ResponseIgnoreFiled.values();
            for (ResponseIgnoreFiled field : fields) {
                System.out.println("vvvvvvvvvv:" + field.getValue());
            }
            return list.toArray(new String[list.size()]);
        }
    };
    
    abstract public MessageInterfaceVO parse(BoundChannel extChannel, byte[] message) throws MessageParserException;
    
    abstract public byte[] build(BoundChannel extChannel, MessageInterfaceVO messageInterfaceVO) throws MessageParserException;
    
    abstract public byte[] buildError(BoundChannel extChannel, MessageHeader messageHeader, CfoodException ex);
    
}
