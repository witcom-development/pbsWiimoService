package org.fincl.miss.server.security.e2e.service.impl;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;

import org.apache.commons.codec.binary.Base64;
import org.fincl.miss.core.util.security.AesUtils;
import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.ServerException;
import org.fincl.miss.server.security.e2e.service.E2EService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.hazelcast.core.IMap;

/**
 * Diffie-Hellman 키 동의 프로토콜
 * 
 * @author Administrator
 * 
 */
@Service
public class E2EServiceImpl implements E2EService {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Resource(name = "e2eKeyRepository")
    private IMap<String, String> e2eKeyRepository;
    
    // // The 1024 bit Diffie-Hellman modulus values used by SKIP
    // private static final byte skip1024ModulusBytes[] = { (byte) 0xF4, (byte) 0x88, (byte) 0xFD, (byte) 0x58, (byte) 0x4E, (byte) 0x49, (byte) 0xDB, (byte) 0xCD, (byte) 0x20, (byte) 0xB4, (byte) 0x9D, (byte) 0xE4, (byte) 0x91, (byte) 0x07, (byte) 0x36, (byte) 0x6B, (byte) 0x33, (byte) 0x6C, (byte) 0x38, (byte) 0x0D, (byte) 0x45, (byte) 0x1D, (byte) 0x0F, (byte) 0x7C, (byte) 0x88, (byte) 0xB3, (byte) 0x1C, (byte) 0x7C, (byte) 0x5B, (byte) 0x2D, (byte) 0x8E, (byte) 0xF6, (byte) 0xF3, (byte) 0xC9, (byte) 0x23, (byte) 0xC0, (byte) 0x43, (byte) 0xF0, (byte) 0xA5, (byte) 0x5B, (byte) 0x18, (byte) 0x8D, (byte) 0x8E, (byte) 0xBB, (byte) 0x55, (byte) 0x8C, (byte) 0xB8, (byte) 0x5D, (byte) 0x38, (byte) 0xD3, (byte) 0x34, (byte) 0xFD, (byte) 0x7C, (byte) 0x17, (byte) 0x57, (byte) 0x43, (byte) 0xA3, (byte) 0x1D, (byte) 0x18, (byte) 0x6C, (byte) 0xDE, (byte) 0x33, (byte) 0x21, (byte) 0x2C, (byte) 0xB5, (byte) 0x2A, (byte) 0xFF, (byte) 0x3C, (byte) 0xE1, (byte) 0xB1, (byte) 0x29, (byte) 0x40,
    // (byte) 0x18, (byte) 0x11, (byte) 0x8D, (byte) 0x7C, (byte) 0x84, (byte) 0xA7, (byte) 0x0A, (byte) 0x72, (byte) 0xD6, (byte) 0x86, (byte) 0xC4, (byte) 0x03, (byte) 0x19, (byte) 0xC8, (byte) 0x07, (byte) 0x29, (byte) 0x7A, (byte) 0xCA, (byte) 0x95, (byte) 0x0C, (byte) 0xD9, (byte) 0x96, (byte) 0x9F, (byte) 0xAB, (byte) 0xD0, (byte) 0x0A, (byte) 0x50, (byte) 0x9B, (byte) 0x02, (byte) 0x46, (byte) 0xD3, (byte) 0x08, (byte) 0x3D, (byte) 0x66, (byte) 0xA4, (byte) 0x5D, (byte) 0x41, (byte) 0x9F, (byte) 0x9C, (byte) 0x7C, (byte) 0xBD, (byte) 0x89, (byte) 0x4B, (byte) 0x22, (byte) 0x19, (byte) 0x26, (byte) 0xBA, (byte) 0xAB, (byte) 0xA2, (byte) 0x5E, (byte) 0xC3, (byte) 0x55, (byte) 0xE9, (byte) 0x2F, (byte) 0x78, (byte) 0xC7 };
    //
    // // The SKIP 1024 bit modulus
    // private static final BigInteger skip1024Modulus = new BigInteger(1, skip1024ModulusBytes);
    //
    // // The base used with the SKIP 1024 bit modulus
    // private static final BigInteger skip1024Base = BigInteger.valueOf(2);
    
    @Override
    public String getServerPublicKey(String id, String clientPublicKey) {
        
        String serverPublicKey = null;
        
        try {
            
            byte[] bClientPublicKey = Base64.decodeBase64(clientPublicKey);
            /*
             * Let's turn over to Bob. Bob has received Alice's public key
             * in encoded format.
             * He instantiates a DH public key from the encoded key material.
             */
            
            /**
             * 클라이언트가 전송한 공개키로 DH 공개키를 생성
             */
            KeyFactory serverKeyFactory = KeyFactory.getInstance("DH");
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(bClientPublicKey);
            PublicKey clientPubKey = serverKeyFactory.generatePublic(x509KeySpec);
            
            /**
             * 클라이언트의 공개키로 부터 DH Parameter를 추출
             */
            DHParameterSpec dhParamSpec = ((DHPublicKey) clientPubKey).getParams();
            
            /**
             * 서버의 개인키, 공개키를 생성
             */
            System.out.println("BOB: Generate DH keypair ...");
            KeyPairGenerator serverKeyPairGen = KeyPairGenerator.getInstance("DH");
            serverKeyPairGen.initialize(dhParamSpec);
            KeyPair serverKeyPair = serverKeyPairGen.generateKeyPair();
            
            /**
             * 서버의 키 동의 객체를 생성 및 초기화
             */
            System.out.println("BOB: Initialization ...");
            KeyAgreement serverKeyAgree = KeyAgreement.getInstance("DH");
            serverKeyAgree.init(serverKeyPair.getPrivate());
            
            /**
             * 클라이언트에서 전송할 서버 공개키를 추출
             */
            byte[] serverPubKeyEnc = serverKeyPair.getPublic().getEncoded();
            
            serverPublicKey = Base64.encodeBase64String(serverPubKeyEnc);
            
            /**
             * 키에 대한 동의 실행
             */
            serverKeyAgree.doPhase(clientPubKey, true);
            
            /**
             * AES 알고리즘에 대한 암호화 키 생성
             */
            SecretKey secretKey = serverKeyAgree.generateSecret("AES");
            
            String encAesSecreyKey = Base64.encodeBase64String(secretKey.getEncoded());
            
            /**
             * Hazelcast에 해당 클라이언트에 대한 암/복호화 정보를 저장
             */
            
            // set은 old값을 리턴하지 않고, put은 old값을 리턴한다. old값이 필요없다면 set이 성능상으로나 뭐로나 좋다.
            // key는 설정값에 의해 30분간만 유효
            e2eKeyRepository.set(id, encAesSecreyKey, 30, TimeUnit.MINUTES);
            
        }
        catch (InvalidKeyException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new ServerException(ErrorConstant.E2E_KEY_EXCHANGE, e);
        }
        catch (IllegalStateException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new ServerException(ErrorConstant.E2E_KEY_EXCHANGE, e);
        }
        catch (NoSuchAlgorithmException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new ServerException(ErrorConstant.E2E_KEY_EXCHANGE, e);
        }
        catch (InvalidAlgorithmParameterException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new ServerException(ErrorConstant.E2E_KEY_EXCHANGE, e);
        }
        catch (InvalidKeySpecException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new ServerException(ErrorConstant.E2E_KEY_EXCHANGE, e);
        }
        
        return serverPublicKey;
    }
    
    @Override
    public String getKey(String id) {
        return e2eKeyRepository.get(id);
    }
    
    @Override
    public byte[] encrypt(String id, byte[] sourceMessage) {
        String encKey = getKey(id);
        System.out.println("encKey [" + encKey + "]");
        byte[] resultMessage = AesUtils.encryptByte(encKey, sourceMessage);
        return resultMessage;
    }
    
    @Override
    public byte[] decrypt(String id, byte[] sourceMessage) {
        String decKey = getKey(id);
        System.out.println("decKey [" + decKey + "]");
        byte[] resultMessage = AesUtils.decryptByte(decKey, sourceMessage);
        return resultMessage;
    }
}
