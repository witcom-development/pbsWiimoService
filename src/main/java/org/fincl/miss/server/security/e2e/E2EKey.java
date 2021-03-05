package org.fincl.miss.server.security.e2e;

import java.io.Serializable;

public class E2EKey implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 6884888207128355432L;
    
    /**
     * 클라이언트 구분 Unique ID
     */
    private String id;
    
    /**
     * 암/복호화 키(base64)
     */
    private String secretKey;
    
    public E2EKey() {
        
    }
    
    public E2EKey(String id, String secretKey) {
        this.id = id;
        this.secretKey = secretKey;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getSecretKey() {
        return secretKey;
    }
    
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
    
}
