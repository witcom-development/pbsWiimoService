package org.fincl.miss.server.message;

import java.io.Serializable;

public class GuidTopicMessage implements Serializable {
    private static final long serialVersionUID = -8606208987968123666L;
    
    private String id;
    private String guid;
    private byte[] payload;
    
    public GuidTopicMessage(String guid) {
        this.guid = guid;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getGuid() {
        return guid;
    }
    
    public void setGuid(String guid) {
        this.guid = guid;
    }
    
    public byte[] getPayload() {
        return payload;
    }
    
    public void setPayload(byte[] payload) {
        this.payload = payload;
    }
    
}
