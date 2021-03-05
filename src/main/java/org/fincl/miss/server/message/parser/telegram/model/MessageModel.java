package org.fincl.miss.server.message.parser.telegram.model;
 
 
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.fincl.miss.server.exeption.MessageParserException;

/** 
 * - 메시지 전문 구조를 나타내는 모델 - 
 */

public class MessageModel implements Serializable{
    
 
    private static final long serialVersionUID = 1L;

    //    private static final transient Log LOG = LogFactory.getLog(MessageModel.class);
    private boolean AUTO_RELOAD = false;
    
    private String id = null; 
    private String messageIDClass = null;
    private String comSeqClass = null;
        
    private List<HeaderModel> headers = null;
    private List<DynamicHeaderModel> dynamic_headers = null;
    private LinkedHashMap<String, BodyModel> bodys = null;    
    
    public MessageModel()
    {
        headers= new ArrayList<HeaderModel>();
        dynamic_headers = new ArrayList<DynamicHeaderModel>();
        bodys = new LinkedHashMap<String, BodyModel>();
    }
 
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public void addHeader(HeaderModel header) {
        headers.add(header);
    }
    public void addDynamicHeader(DynamicHeaderModel dynamic) {
        dynamic_headers.add(dynamic);
    }
 
    
    public BodyModel getBody(String id, int messageType) {
        BodyModel body = bodys.get(id+messageType);
        
        if(AUTO_RELOAD)
        {
            if(body != null)
            {
                bodys.remove(id+messageType);
                body = null;
            }
        }
        
        if(body == null)
            return setBody(id, messageType);
        else
            return body;
    }
    
    
    public BodyModel getBodyNoSync(String id, int messageType) {
        BodyModel body = bodys.get(id+messageType);     
        if(body == null)
            return setBody(id, messageType);
        else
            return body;
    }
    
    private BodyModel setBody(String id, int messageType) throws MessageParserException {
        String bodyId = id+messageType;
        BodyModel body = bodys.get(bodyId);
        synchronized(bodyId) {
            if(body == null)
            {
                body = new BodyModel();
                body.setId(id);  
                body.setMessageType(messageType);
 
                body.makeFieldModel();
 
                setBody(id, body, messageType);
            }           
        }       
        return body;
    }

    private void setBody(String id, BodyModel body, int messageType) {
        if(bodys.get(id+messageType) == null)
            bodys.put(id+messageType, body);
    }

 

    public List<HeaderModel> getHeaders() {
        return headers;
    }

    public void setHeaders(ArrayList<HeaderModel> headers) {
        this.headers = headers;
    }
    
    public List<DynamicHeaderModel> getDynamic_headers() {
        return dynamic_headers;
    }
    
    public DynamicHeaderModel getDynamic_header(String dynamicheader_id) {
        for(int i=0;i<dynamic_headers.size();i++)
        {
            DynamicHeaderModel model = dynamic_headers.get(i);
            if(model.getId().equals(dynamicheader_id))
                return model;
        }
        return null;
    }

    public void setDynamic_headers(ArrayList<DynamicHeaderModel> dynamic_headers) {
        this.dynamic_headers = dynamic_headers;
    }
    
    public HeaderModel getHeader(String header_id) {
        for(int i=0;i<headers.size();i++)
        {
            HeaderModel model = headers.get(i);
            if(model.getId().equals(header_id))
                return model;
        }
        return null;  
    }

    public LinkedHashMap<String, BodyModel> getBodys() {
        return bodys;
    }

    public void setBodys(LinkedHashMap<String, BodyModel> bodys) {
        this.bodys = bodys;
    }

    public String getMessageIDClass() {
        return messageIDClass;
    }

    public void setMessageIDClass(String messageIDClass) {
        this.messageIDClass = messageIDClass;
    }

    public String getComSeqClass() {
        return comSeqClass;
    }

    public void setComSeqClass(String comSeqClass) {
        this.comSeqClass = comSeqClass;
    }
}

