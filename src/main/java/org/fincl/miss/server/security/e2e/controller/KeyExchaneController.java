package org.fincl.miss.server.security.e2e.controller;

import javax.annotation.Resource;

import org.fincl.miss.core.viewresolver.header.HeaderParamEntity;
import org.fincl.miss.core.viewresolver.header.annotation.HeaderParam;
import org.fincl.miss.server.exeption.ServerException;
import org.fincl.miss.server.security.e2e.service.E2EService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hazelcast.core.IMap;

@RequestMapping("e2e/*")
@Controller
public class KeyExchaneController {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Resource(name = "e2eKeyRepository")
    private IMap<String, String> e2eKeyRepository;
    
    @Autowired
    private E2EService e2eService;
    
    @RequestMapping(value = "keyExchange")
    public ResponseEntity<?> keyExchange(@HeaderParam HeaderParamEntity deviceHeader, @RequestBody String clientPublicKey) {
        System.out.println("deviceHeader:" + deviceHeader);
        String serverPublicKey = "";
        
        try {
            serverPublicKey = e2eService.getServerPublicKey(deviceHeader.getUuid(), clientPublicKey);
        }
        catch (ServerException e) {
            if (logger.isErrorEnabled()) {
                e.printStackTrace();
            }
            throw e;
        }
        
        return new ResponseEntity<String>(serverPublicKey, HttpStatus.OK);
    }
    
    @RequestMapping(value = "test")
    public ResponseEntity<?> test(@HeaderParam HeaderParamEntity deviceHeader, @RequestBody byte[] requestNody) {
        System.out.println("deviceHeader:" + deviceHeader);
        System.out.println("e2eKeyRepository:" + e2eKeyRepository);
        String e2eKey = e2eKeyRepository.get(deviceHeader.getUuid());
        System.out.println("secretKey:" + e2eKey);
        // String aa = AesUtils.decrypt(e2eKey, param.get("data"));
        
        byte[] xx = e2eService.decrypt(deviceHeader.getUuid(), requestNody);
        
        System.out.println(">>" + new String(xx));
        
        // System.out.println("ff [" + new String(Base64.decodeBase64(aa)) + "]");
        
        String res = "강강수얼래";
        byte[] yy = e2eService.encrypt(deviceHeader.getUuid(), res.getBytes());
        
        return new ResponseEntity<byte[]>(yy, HttpStatus.OK);
    }
    
    private String toHexString(byte[] block) {
        StringBuffer buf = new StringBuffer();
        
        int len = block.length;
        
        for (int i = 0; i < len; i++) {
            byte2hex(block[i], buf);
            if (i < len - 1) {
                buf.append(":");
            }
        }
        return buf.toString();
    }
    
    private void byte2hex(byte b, StringBuffer buf) {
        char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        int high = ((b & 0xf0) >> 4);
        int low = (b & 0x0f);
        buf.append(hexChars[high]);
        buf.append(hexChars[low]);
    }
}
