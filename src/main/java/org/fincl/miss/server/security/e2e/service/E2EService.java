package org.fincl.miss.server.security.e2e.service;

public interface E2EService {
    
    public String getServerPublicKey(String id, String clientPublicKey);
    
    public String getKey(String id);
    
    public byte[] encrypt(String id, byte[] sourceMessage);
    
    public byte[] decrypt(String id, byte[] sourceMessage);
    
}
