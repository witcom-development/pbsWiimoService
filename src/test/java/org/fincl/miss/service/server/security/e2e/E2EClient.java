package org.fincl.miss.service.server.security.e2e;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.DHParameterSpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.fincl.miss.core.util.security.AesUtils;
import org.junit.Test;

public class E2EClient {
    
    // The 1024 bit Diffie-Hellman modulus values used by SKIP
    private static final byte skip1024ModulusBytes[] = { (byte) 0xF4, (byte) 0x88, (byte) 0xFD, (byte) 0x58, (byte) 0x4E, (byte) 0x49, (byte) 0xDB, (byte) 0xCD, (byte) 0x20, (byte) 0xB4, (byte) 0x9D, (byte) 0xE4, (byte) 0x91, (byte) 0x07, (byte) 0x36, (byte) 0x6B, (byte) 0x33, (byte) 0x6C, (byte) 0x38, (byte) 0x0D, (byte) 0x45, (byte) 0x1D, (byte) 0x0F, (byte) 0x7C, (byte) 0x88, (byte) 0xB3, (byte) 0x1C, (byte) 0x7C, (byte) 0x5B, (byte) 0x2D, (byte) 0x8E, (byte) 0xF6, (byte) 0xF3, (byte) 0xC9, (byte) 0x23, (byte) 0xC0, (byte) 0x43, (byte) 0xF0, (byte) 0xA5, (byte) 0x5B, (byte) 0x18, (byte) 0x8D, (byte) 0x8E, (byte) 0xBB, (byte) 0x55, (byte) 0x8C, (byte) 0xB8, (byte) 0x5D, (byte) 0x38, (byte) 0xD3, (byte) 0x34, (byte) 0xFD, (byte) 0x7C, (byte) 0x17, (byte) 0x57, (byte) 0x43, (byte) 0xA3, (byte) 0x1D, (byte) 0x18, (byte) 0x6C, (byte) 0xDE, (byte) 0x33, (byte) 0x21, (byte) 0x2C, (byte) 0xB5, (byte) 0x2A, (byte) 0xFF, (byte) 0x3C, (byte) 0xE1, (byte) 0xB1, (byte) 0x29, (byte) 0x40, (byte) 0x18, (byte) 0x11, (byte) 0x8D, (byte) 0x7C, (byte) 0x84, (byte) 0xA7, (byte) 0x0A, (byte) 0x72, (byte) 0xD6, (byte) 0x86, (byte) 0xC4, (byte) 0x03, (byte) 0x19, (byte) 0xC8, (byte) 0x07, (byte) 0x29, (byte) 0x7A, (byte) 0xCA, (byte) 0x95, (byte) 0x0C, (byte) 0xD9, (byte) 0x96, (byte) 0x9F, (byte) 0xAB, (byte) 0xD0, (byte) 0x0A, (byte) 0x50, (byte) 0x9B, (byte) 0x02, (byte) 0x46, (byte) 0xD3, (byte) 0x08, (byte) 0x3D, (byte) 0x66, (byte) 0xA4, (byte) 0x5D, (byte) 0x41, (byte) 0x9F, (byte) 0x9C, (byte) 0x7C, (byte) 0xBD, (byte) 0x89, (byte) 0x4B, (byte) 0x22, (byte) 0x19, (byte) 0x26, (byte) 0xBA, (byte) 0xAB, (byte) 0xA2, (byte) 0x5E, (byte) 0xC3, (byte) 0x55, (byte) 0xE9, (byte) 0x2F, (byte) 0x78, (byte) 0xC7 };
    
    // The SKIP 1024 bit modulus
    private static final BigInteger skip1024Modulus = new BigInteger(1, skip1024ModulusBytes);
    
    // The base used with the SKIP 1024 bit modulus
    private static final BigInteger skip1024Base = BigInteger.valueOf(2);
    
    @Test
    public void testRequest() {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost("http://localhost:9080/miss-service/e2e/keyExchange");
        
        // add header
        post.setHeader("X-Agent-Detail", "UD=mkkang");
        DHParameterSpec dhSkipParamSpec;
        HttpResponse response;
        
        String id = "mkkang";
        try {
            
            // AlgorithmParameterGenerator paramGen = AlgorithmParameterGenerator.getInstance("DH");
            // paramGen.init(512);
            // AlgorithmParameters params = paramGen.generateParameters();
            // dhSkipParamSpec = (DHParameterSpec) params.getParameterSpec(DHParameterSpec.class);
            
            dhSkipParamSpec = new DHParameterSpec(skip1024Modulus, skip1024Base);
            
            System.out.println("ALICE: Generate DH keypair ...");
            KeyPairGenerator aliceKpairGen = KeyPairGenerator.getInstance("DH");
            aliceKpairGen.initialize(dhSkipParamSpec);
            KeyPair aliceKpair = aliceKpairGen.generateKeyPair();
            
            // Alice creates and initializes her DH KeyAgreement object
            System.out.println("ALICE: Initialization ...");
            KeyAgreement aliceKeyAgree = KeyAgreement.getInstance("DH");
            aliceKeyAgree.init(aliceKpair.getPrivate());
            
            // Alice encodes her public key, and sends it over to Bob.
            byte[] alicePubKeyEnc = aliceKpair.getPublic().getEncoded();
            
            String ss = Base64.encodeBase64String(alicePubKeyEnc);
            // //
            
            HttpEntity requestEntity = new StringEntity(ss);
            
            post.setEntity(requestEntity);
            response = client.execute(post);
            
            System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
            
            String aa = EntityUtils.toString(response.getEntity());
            
            System.out.println("aa:" + aa);
            
            X509EncodedKeySpec x509KeySpec;
            byte[] bobPubKeyEnc = Base64.decodeBase64(aa);
            KeyFactory aliceKeyFac = KeyFactory.getInstance("DH");
            x509KeySpec = new X509EncodedKeySpec(bobPubKeyEnc);
            PublicKey bobPubKey = aliceKeyFac.generatePublic(x509KeySpec);
            
            System.out.println("ALICE: Execute PHASE1 ...");
            aliceKeyAgree.doPhase(bobPubKey, true);
            
            SecretKey secretKey = aliceKeyAgree.generateSecret("AES");
            System.out.println(toHexString(secretKey.getEncoded()));
            System.out.println(secretKey.getEncoded().length);
            // int aliceLen = aliceSharedSecret.length;
            
            String message = Base64.encodeBase64String("동해물과백두산이".getBytes());
            System.out.println("ff:" + Base64.encodeBase64String(secretKey.getEncoded()));
            String enc = AesUtils.encrypt(Base64.encodeBase64String(secretKey.getEncoded()), message);
            
            System.out.println("val:" + enc);
            
            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            urlParameters.add(new BasicNameValuePair("data", enc));
            
            post = new HttpPost("http://localhost:9080/miss-service/e2e/test");
            post.setHeader("X-Agent-Detail", "UD=mkkang");
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
            response = client.execute(post);
            
        }
        catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (InvalidAlgorithmParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (InvalidKeySpecException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
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
