package org.fincl.miss.server.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.DatatypeConverter;

public class SeedTest {

	public static void main(String[] args) throws IOException {
	//	String userKey = "seoulbicycle.com";
		
	//	SeedCipher seed = new SeedCipher();
		byte[] userKey = {
			     0x73, 0x65, 0x6F, 0x75, 0x6C, 0x62, 0x69, 0x63,
			     0x79, 0x63, 0x6C, 0x65, 0x2E, 0x63, 0x6F, 0x6D
			};
		String original = "438000040119B00600000002101915011043879300";
//		String original = "437C00010119B006000000024590460000000108101915011043879300010000000000000301060106";
		byte[] buf = DatatypeConverter.parseHexBinary(original);
		
		
//		System.out.println(buf.length +" bytes:"+DatatypeConverter.printHexBinary(buf));
//		byte[] encText=seed.encrypt(original, userKey.getBytes("UTF-8"), "UTF-8");  //encText 암호문
//		System.out.println("enc : "+encText.length +" bytes :"+DatatypeConverter.printHexBinary(encText));
		
		byte[] encText1 = KISA_SEED_ECB.SEED_ECB_Encrypt(userKey, buf, 0, buf.length);
		
		System.out.println("enc1 : "+encText1.length +" bytes :"+DatatypeConverter.printHexBinary(encText1));
//		Ciphertext(SEED_ECB_Encrypt)	: 32 a5 78 1a 62 b a9 18 b4 63 2 b3 be 5c 59 89 91 45 9 70 7 e8 77 75 61 2e d1 9a 6b 67 f0 a6 
//		Plaintext(SEED_ECB_Decrypt)	: 43 5f 0 11 2 19 b0 6 0 0 0 1 9d 20 40 0 0 0 1 0 0 9 9 9 92 1 5 0 

//		byte[] baNote = seed.decrypt(encText,userKey.getBytes("UTF-8")); //baNote 원문
		
//		System.out.println("dec : "+baNote.length +" bytes :"+DatatypeConverter.printHexBinary(baNote));
		byte[] baNote1 = KISA_SEED_ECB.SEED_ECB_Decrypt(userKey, encText1, 0, encText1.length); //baNote 원문.
		System.out.println("dec1 : "+baNote1.length +" bytes :"+DatatypeConverter.printHexBinary(baNote1));
//		String desText = new String(baNote,"UTF-8");
//		String desText1 = new String(baNote1,"UTF-8");
		
//		System.out.println("result :"+desText.getBytes().length + " bytes:"+desText);
//		System.out.println("result1 :"+desText1.getBytes().length + " bytes:"+desText1);
		
		byte[] bytes = userKey;
		byte[] ss = {
			0x73, 0x65, 0x6F, 0x75, 0x6C, 0x62, 0x69, 0x63,
			0x79, 0x63, 0x6C, 0x65, 0x2E, 0x63, 0x6F, 0x6D	
		};
		for (int i = 0; i < bytes.length; i++) {
			System.out.printf("[%02X]", bytes[i]);
		}
//		byte[] test = DatatypeConverter.parseHexBinary("845818A1809035F1423AEF1072FD151E445E26E378EF");
		
//		byte[] sst = KISA_SEED_ECB.SEED_ECB_Decrypt(ss, test, 0, test.length); //baNote 원문.
//		byte[] sst = seed.decrypt(encText,ss); //baNote 원문
//		desText = new String(sst,"UTF-8");
//		System.out.println("result  bytes:"+DatatypeConverter.printHexBinary(sst));
		
	}

}
