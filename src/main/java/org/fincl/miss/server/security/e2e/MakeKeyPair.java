package org.fincl.miss.server.security.e2e;

import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

public class MakeKeyPair {
    public static void main(String args[]) {
        try {
            String pub_key = "RSA.pub";
            String pri_key = "RSA.pri";
            
            System.out.println("Seeding random number generator...");
            SecureRandom rand = new SecureRandom();
            rand.nextInt();
            
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA"); // RSA를 사용
            kpg.initialize(512, rand); // 길이는 512, rand를 사용해 초기화
            System.out.println("Generating keys ...");
            KeyPair kp = kpg.generateKeyPair();
            PrivateKey priv = kp.getPrivate();
            PublicKey pub = kp.getPublic();
            
            FileOutputStream outFile = new FileOutputStream(pri_key);
            outFile.write(priv.getEncoded());
            System.out.println("Private key saved:RSA.pri");
            
            outFile = new FileOutputStream(pub_key);
            outFile.write(pub.getEncoded());
            System.out.println("Public key Saved : RSA.pub");
            System.out.println("Key Pair Generate Successful!!!");
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
