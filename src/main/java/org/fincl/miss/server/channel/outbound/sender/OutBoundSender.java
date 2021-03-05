package org.fincl.miss.server.channel.outbound.sender;

import io.netty.channel.ChannelException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.fincl.miss.server.channel.outbound.OutBoundChannelImpl;
import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.OutBoundSenderException;
import org.fincl.miss.server.exeption.ServerException;
import org.fincl.miss.server.util.EnumCode.AutoStartYn;
import org.fincl.miss.server.util.EnumCode.ChannelStatus;
import org.fincl.miss.server.util.EnumCode.RequestDataType;
import org.fincl.miss.server.util.EnumCode.ResponseDataType;
import org.fincl.miss.server.util.EnumCode.SSLYn;
import org.fincl.miss.server.util.EnumCode.SingleYn;

import com.fasterxml.jackson.xml.XmlMapper;

public abstract class OutBoundSender {
    
    protected OutBoundChannelImpl outBoundChannel;
    
    protected AutoStartYn autoStartYn = AutoStartYn.NO;
    
    protected SingleYn singleYn = SingleYn.NO;
    
    protected SSLYn sslYn = SSLYn.NO;
    
    public OutBoundSender() {
        
    }
    
    public OutBoundChannelImpl getOutBoundChannel() {
        return outBoundChannel;
    }
    
    public void setOutBoundChannel(OutBoundChannelImpl outBoundChannel) {
        this.outBoundChannel = outBoundChannel;
    }
    
    public OutBoundSender(OutBoundChannelImpl outBoundChannel) {
        this.outBoundChannel = outBoundChannel;
        
        this.autoStartYn = AutoStartYn.getEnum(outBoundChannel.getAutoStartYn());
        this.sslYn = SSLYn.getEnum(outBoundChannel.getSslYn());
    }
    
    public Map<String, Object> buildResponseMessage(byte[] bRes) throws ChannelException {
        Map<String, Object> res = null;
        
        String sRes = new String(bRes);
        
        ResponseDataType responseDataType = ResponseDataType.getEnum(outBoundChannel.getResponseDataTypeCode());
        
        if (responseDataType == ResponseDataType.JSON) {
            res = fromJsonToMap(sRes);
        }
        else if (responseDataType == ResponseDataType.XML) {
            res = fromXmlToMap(sRes);
        }
        else {
            if (StringUtils.startsWith(sRes, "<")) { // xml
                res = fromXmlToMap(sRes);
            }
            else if (StringUtils.startsWith(sRes, "[") || StringUtils.startsWith(sRes, "{")) { // json
                res = fromJsonToMap(sRes);
            }
        }
        
        return res;
    }
    
    public String buildRequestMessage(Map<String, Object> mParam) throws ChannelException {
        String req = null;
        
        RequestDataType requestDataType = RequestDataType.getEnum(outBoundChannel.getRequestDataTypeCode());
        
        if (requestDataType == RequestDataType.FORM) {
            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            if (mParam != null) {
                Iterator<String> it = mParam.keySet().iterator();
                while (it.hasNext()) {
                    String sKey = it.next();
                    urlParameters.add(new BasicNameValuePair(sKey, (String) mParam.get("sKey")));
                }
            }
            
            // try {
            // req = EntityUtils.toString(new UrlEncodedFormEntity(urlParameters));
            // }
            // catch (ParseException e) {
            // throw new ServerException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
            // }
            // catch (UnsupportedEncodingException e) {
            // throw new ServerException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
            // }
            // catch (IOException e) {
            // throw new ServerException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
            // }
        }
        else if (requestDataType == RequestDataType.JSON) {
            req = fromMapToXml(mParam);
        }
        else if (requestDataType == RequestDataType.XML) {
            req = fromMapToJson(mParam);
        }
        return req;
    }
    
    private Map<String, Object> fromXmlToMap(String xml) throws ServerException {
        Map<String, Object> map = null;
        ObjectMapper mapper = new XmlMapper();
        mapper.configure(Feature.WRAP_EXCEPTIONS, false);
        xml = "<HashMap xmlns=\"\">" + StringUtils.remove(xml, "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>") + "</HashMap>";
        try {
            map = mapper.readValue(xml, HashMap.class);
        }
        catch (JsonParseException e) {
            throw new ChannelException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
        }
        catch (JsonMappingException e) {
            throw new ChannelException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
        }
        catch (IOException e) {
            throw new ChannelException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
        }
        
        return map;
    }
    
    private Map<String, Object> fromJsonToMap(String json) throws ChannelException {
        Map<String, Object> map = null;
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(json);
        try {
            if (StringUtils.startsWith(json, "[") && StringUtils.endsWith(json, "]")) {
                json = "{\"data\" : " + json + "}";
            }
            map = mapper.readValue(json, HashMap.class);
        }
        catch (JsonParseException e) {
            throw new ChannelException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
        }
        catch (JsonMappingException e) {
            throw new ChannelException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
        }
        catch (IOException e) {
            throw new ChannelException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
        }
        
        return map;
    }
    
    private String fromMapToXml(Map<String, Object> mParam) throws ChannelException {
        String req = null;
        ObjectMapper mapper = new XmlMapper();
        try {
            req = mapper.writeValueAsString(mParam);
            req = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + StringUtils.substringBetween(req, "<HashMap xmlns=\"\">", "</HashMap>");
        }
        catch (JsonGenerationException e) {
            throw new ChannelException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
        }
        catch (JsonMappingException e) {
            throw new ChannelException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
        }
        catch (IOException e) {
            throw new ChannelException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
        }
        
        return req;
    }
    
    private String fromMapToJson(Map<String, Object> mParam) throws ChannelException {
        String req = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            req = mapper.writeValueAsString(mParam);
        }
        catch (JsonGenerationException e) {
            throw new ChannelException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
        }
        catch (JsonMappingException e) {
            throw new ChannelException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
        }
        catch (IOException e) {
            throw new ChannelException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
        }
        
        return req;
    }
    
    abstract public void send(byte[] bMessage) throws OutBoundSenderException;
    
    abstract public byte[] sendAndReceive(byte[] bMessage) throws OutBoundSenderException;
    
    abstract public boolean startup() throws OutBoundSenderException;
    
    abstract public boolean shutdown() throws OutBoundSenderException;
    
    abstract public ChannelStatus getStatus() throws OutBoundSenderException;
}
