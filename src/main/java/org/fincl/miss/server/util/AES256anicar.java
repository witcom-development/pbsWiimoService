package org.fincl.miss.server.util;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

public class AES256anicar {
    
    //public static String key = "CryptKeyForAES256OfDKITechnology";
	public static String key = "CryptKeyForAES25";

    /**
     * hex to byte[] : 16진수 문자열을 바이트 배열로 변환한다.
     * 
     * @param hex    hex string
     * @return
     */
    public static byte[] hexToByteArray(String hex) {
        if (hex == null || hex.length() == 0) {
            return null;
        }

        byte[] ba = new byte[hex.length() / 2];
        for (int i = 0; i < ba.length; i++) {
            ba[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return ba;
    }

    /**
     * byte[] to hex : unsigned byte(바이트) 배열을 16진수 문자열로 바꾼다.
     * 
     * @param ba        byte[]
     * @return
     */
    public static String byteArrayToHex(byte[] ba) {
        if (ba == null || ba.length == 0) {
            return null;
        }

        StringBuffer sb = new StringBuffer(ba.length * 2);
        String hexNumber;
        for (int x = 0; x < ba.length; x++) {
            hexNumber = "0" + Integer.toHexString(0xff & ba[x]);

            sb.append(hexNumber.substring(hexNumber.length() - 2));
        }
        return sb.toString();
    } 

    /**
     * AES 방식의 암호화
     * 
     * @param message
     * @return
     * @throws Exception
     */
    public static String encrypt(String message) throws Exception {
    	
        // use key coss2
        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "AES");

        // Instantiate the cipher
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

        byte[] encrypted = cipher.doFinal(message.getBytes());
        return byteArrayToHex(encrypted);
    }

    /**
     * AES 방식의 복호화
     * 
     * @param message
     * @return
     * @throws Exception
     */
    public static String decrypt(String encrypted) throws Exception {

        // use key coss2
        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] original = cipher.doFinal(hexToByteArray(encrypted));
        String originalString = new String(original);
        return originalString;
    }
    
    public static String sha256(String password) {
    	MessageDigest digest;
		
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            String output = Hex.encodeHexString(hash);
            return output;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }  	
    }
    
    public static void main(String[] args)
    {
        try {
        	String str ="spbdb1234!@#$";
        	System.out.println("START");
            String encrypt = encrypt(str);
            System.out.println("origin str = "+str);
            System.out.println("encrypt str = "+encrypt);
            
            str ="spb_back";
            encrypt = encrypt(str);
            System.out.println("origin str = "+str);
            System.out.println("encrypt str = "+encrypt);
            
            str ="beacon123!@#";
            encrypt = encrypt(str);
            System.out.println("origin str = "+str);
            System.out.println("encrypt str = "+encrypt);
            
            String decrypt = decrypt(encrypt);
            System.out.println("decrypt str = "+decrypt);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

}

