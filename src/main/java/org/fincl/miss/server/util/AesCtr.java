/*
 * @Project Name : miss-service
 * @Package Name : org.fincl.miss.server.util
 * @파일명          : AesCtr.java
 * @작성일          : 2015. 9. 8.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 9. 8.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.server.util;
import org.apache.commons.codec.binary.Base64;

/**
 * @파일명          : AesCtr.java
 * @작성일          : 2015. 9. 8.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 9. 8.   |   ymshin   |  최초작성
 */
public class AesCtr {
	/**
	 * Private constructor, stops initialisation
	 */
	public AesCtr() {
	}

	/**
	 * Encrypts a <code>String</code> using AES encryption in Counter mode of operation
	 * 
	 * @param plaintext source text to be encrypted
	 * @param password the password to use to generate a key
	 * @param nBits number of bits to be used in the key (128, 192, or 256)
	 * @return the encrypted <code>String</code>
	 */
	public  String encrypt(String plaintext, String password, int nBits) {
		Rijndael aes = new Rijndael();

		/** Standard allows 128/192/256 bit keys **/
		if (!(nBits == 128 || nBits == 192 || nBits == 256))
			try {
				throw new Exception("Invalid key size (" + nBits + " bits)");
			} catch (Exception e) {
				e.printStackTrace();
			}

		/** Uses AES itself to encrypt password to get cipher key **/
		int nBytes = nBits / 8;
		byte[] pwBytes = new byte[nBytes];

		/** Uses 1st 16/24/32 chars of password for key **/
		for (int i = 0; i < nBytes; i++) {
			pwBytes[i] = (byte) password.charAt(i);
		}

		/** Creates a 16-byte key (block size) **/
		aes.makeKey(pwBytes, 256, Rijndael.DIR_ENCRYPT);
		byte[] key = aes.encryptBlock(pwBytes, new byte[Rijndael.BLOCK_SIZE]);
		aes.finalize();

		/** Expands key to 16/24/32 bytes long **/
		if (nBytes > 16) {
			byte[] keySlice = new byte[nBytes - 16];
			for (int i = 0; i < nBytes - 16; i++)
				keySlice[i] = key[i];
			key = addByteArrays(key, keySlice);
		}

		/**
		 * Initialises 1st 8 bytes of counter block with nonce (NIST SP800-38A
		 * �B.2): [0-1] = millisec, [2-3] = random, [4-7] = seconds
		 **/
		byte[] counterBlock = new byte[Rijndael.BLOCK_SIZE];

		/** Timestamp: milliseconds since 1-Jan-1970 **/
		long nonce = (new java.util.Date()).getTime();
		int nonceMs = (int) nonce % 1000;
		int nonceSec = (int) Math.floor(nonce / 1000);
		int nonceRnd = (int) Math.floor(Math.random() * 0xffff);

		/** Copies values to counter block **/
		for (int i = 0; i < 2; i++)
			counterBlock[i] = (byte) ((nonceMs >>> i * 8) & 0xff);
		for (int i = 0; i < 2; i++)
			counterBlock[i + 2] = (byte) ((nonceRnd >>> i * 8) & 0xff);
		for (int i = 0; i < 4; i++)
			counterBlock[i + 4] = (byte) ((nonceSec >>> i * 8) & 0xff);

		/** Creates a header for the encrypted text **/
		byte[] ctrTxt = new byte[8];
		for (int i = 0; i < 8; i++)
			ctrTxt[i] = counterBlock[i];

		/** Initialises Rijndael algorithm **/
		aes.makeKey(key, 256, Rijndael.DIR_ENCRYPT);

		/** Calculates number of blocks **/
		int blockCount = (int) Math.ceil(new Float(plaintext.length())
				/ Rijndael.BLOCK_SIZE);

		/** Variable to store encrypted text **/
		byte[] ciphertxt = new byte[plaintext.length()];

		/** Encrypts block by block **/
		for (int b = 0; b < blockCount; b++) {
			/** Initialises last 8 bytes of counter block with block number **/
			for (int c = 0; c < 4; c++)
				counterBlock[15 - c] = (byte) ((b >>> c * 8) & 0xff);
			for (int c = 0; c < 4; c++)
				counterBlock[15 - c - 4] = (byte) 0;

			/** Encrypts counter block **/
			byte[] cipherCntr = aes.encryptBlock(counterBlock,
					new byte[Rijndael.BLOCK_SIZE]);

			/** Block size (size may be reduced on final block) **/
			int blockLength = b < blockCount - 1 ? Rijndael.BLOCK_SIZE
					: (plaintext.length() - 1) % Rijndael.BLOCK_SIZE + 1;

			/** Xor plaintext with ciphered counter byte by byte **/
			for (int i = 0; i < blockLength; i++) {
				ciphertxt[b * Rijndael.BLOCK_SIZE + i] = (byte) (cipherCntr[i] ^ plaintext
						.charAt(b * Rijndael.BLOCK_SIZE + i));
			}
		}

		/** Finalizes Rijndael algorithm **/
		aes.finalize();

		/** Joins header with encrypted text **/
		byte[] ciphertext = addByteArrays(ctrTxt, ciphertxt);

		/** Encodes in Base64 **/
		String ciphertext64 = new String(Base64.encodeBase64(ciphertext));

		return ciphertext64;
	}

	/**
	 * Decrypts a <code>String</code> encrypted by AES in counter mode of
	 * operation
	 * 
	 * @param ciphertext source text to be encrypted
	 * @param password the password to use to generate a key
	 * @param nBits number of bits to be used in the key (128, 192, or 256)
	 * @return the decrypted <code>String</code>
	 */
	public  String decrypt(String ciphertext, String password, int nBits) {
		Rijndael aes = new Rijndael();

		/** Standard allows 128/192/256 bit keys **/
		if (!(nBits == 128 || nBits == 192 || nBits == 256))
			return null;

		/** Decodes from Base64 **/
		byte[] cipherByte = Base64.decodeBase64(ciphertext.getBytes());

		/** Uses AES itself to encrypt password to get cipher key **/
		int nBytes = nBits / 8;
		byte[] pwBytes = new byte[nBytes];

		/** Uses 1st 16/24/32 chars of password for key **/
		for (int i = 0; i < nBytes; i++) {
			pwBytes[i] = (byte) password.charAt(i);
		}

		/** Creates a 16-byte key (block size) **/
		aes.makeKey(pwBytes, 256, Rijndael.DIR_ENCRYPT);
		byte[] key = aes.encryptBlock(pwBytes, new byte[Rijndael.BLOCK_SIZE]);
		aes.finalize();

		/** Expands key to 24/32 bytes long **/
		if (nBytes > 16) {
			byte[] keySlice = new byte[nBytes - 16];
			for (int i = 0; i < nBytes - 16; i++)
				keySlice[i] = key[i];
			key = addByteArrays(key, keySlice);
		}

		/** Recovers nonce from 1st 8 bytes of ciphertext **/
		byte[] counterBlock = new byte[Rijndael.BLOCK_SIZE];
		for (int i = 0; i < 8; i++)
			counterBlock[i] = cipherByte[i];

		/** Initialises Rijndael algorithm **/
		aes.makeKey(key, 256, Rijndael.DIR_ENCRYPT);

		/** Calculates number of blocks **/
		int blockCount = (int) Math.ceil(new Float(cipherByte.length - 8)
				/ Rijndael.BLOCK_SIZE);

		/** Variable to store plain text **/
		byte[] plaintxt = new byte[cipherByte.length - 8];

		/** Decrypts block by block **/
		for (int b = 0; b < blockCount; b++) {
			/** Initialises last 8 bytes of counter block with block number **/
			for (int c = 0; c < 4; c++)
				counterBlock[15 - c] = (byte) ((b >>> c * 8) & 0xff);
			for (int c = 0; c < 4; c++)
				counterBlock[15 - c - 4] = (byte) 0;

			/** Encrypts counter block **/
			byte[] cipherCntr = aes.encryptBlock(counterBlock,
					new byte[Rijndael.BLOCK_SIZE]);

			/** Block size (size may be reduced on final block) **/
			int blockLength = b < blockCount - 1 ? Rijndael.BLOCK_SIZE
					: (cipherByte.length - 9) % Rijndael.BLOCK_SIZE + 1;

			/** Xor plaintext with ciphered counter byte by byte **/
			for (int i = 0; i < blockLength; i++) {
				plaintxt[b * Rijndael.BLOCK_SIZE + i] = (byte) (cipherCntr[i] ^ cipherByte[8
						+ b * Rijndael.BLOCK_SIZE + i]);
			}
		}

		/** Finalizes Rijndael algorithm **/
		aes.finalize();

		/** Creates final string **/
		String plaintext = new String(plaintxt);

		return plaintext;
	}

	/**
	 * Adds two byte arrays together
	 * 
	 * @param first the first array
	 * @param second the second array
	 * @return the unified array
	 */
	public static byte[] addByteArrays(byte[] first, byte[] second) {
		byte[] result = new byte[first.length + second.length];

		System.arraycopy(first, 0, result, 0, first.length);
		System.arraycopy(second, 0, result, first.length, second.length);

		return result;
	}

	/**
	 * Compares two byte arrays for equality
	 * 
	 * @param first the first array
	 * @param second the second array
	 * @return true if the arrays have identical contents
	 */
	public static final boolean areEqual(byte[] first, byte[] second) {
		int aLength = first.length;

		if (aLength != second.length)
			return false;

		for (int i = 0; i < aLength; i++)
			if (first[i] != second[i])
				return false;

		return true;
	}

/**
 * Optimised Java implementation of the Rijndael (AES) block cipher
 * 
 * @author Paulo Barreto <paulo.barreto@terra.com.br>
 * @version 1.0 (May 2001)
 */
 class Rijndael {
	 public Rijndael(){
		 init();
	 }
	/**
	 * Flag to setup the encryption key schedule.
	 */
	public static final int DIR_ENCRYPT = 1;

	/**
	 * Flag to setup the decryption key schedule.
	 */
	public static final int DIR_DECRYPT = 2;

	/**
	 * Flag to setup both key schedules (encryption/decryption).
	 */
	public static final int DIR_BOTH = (DIR_ENCRYPT | DIR_DECRYPT);

	/**
	 * AES block size in bits (N.B. the Rijndael algorithm itself allows for
	 * other sizes).
	 */
	public static final int BLOCK_BITS = 128;

	/**
	 * AES block size in bytes (N.B. the Rijndael algorithm itself allows for
	 * other sizes).
	 */
	public static final int BLOCK_SIZE = (BLOCK_BITS >>> 3);

	/**
	 * Substitution table (S-box).
	 */
	private static final String SS = "\u637C\u777B\uF26B\u6FC5\u3001\u672B\uFED7\uAB76"
			+ "\uCA82\uC97D\uFA59\u47F0\uADD4\uA2AF\u9CA4\u72C0"
			+ "\uB7FD\u9326\u363F\uF7CC\u34A5\uE5F1\u71D8\u3115"
			+ "\u04C7\u23C3\u1896\u059A\u0712\u80E2\uEB27\uB275"
			+ "\u0983\u2C1A\u1B6E\u5AA0\u523B\uD6B3\u29E3\u2F84"
			+ "\u53D1\u00ED\u20FC\uB15B\u6ACB\uBE39\u4A4C\u58CF"
			+ "\uD0EF\uAAFB\u434D\u3385\u45F9\u027F\u503C\u9FA8"
			+ "\u51A3\u408F\u929D\u38F5\uBCB6\uDA21\u10FF\uF3D2"
			+ "\uCD0C\u13EC\u5F97\u4417\uC4A7\u7E3D\u645D\u1973"
			+ "\u6081\u4FDC\u222A\u9088\u46EE\uB814\uDE5E\u0BDB"
			+ "\uE032\u3A0A\u4906\u245C\uC2D3\uAC62\u9195\uE479"
			+ "\uE7C8\u376D\u8DD5\u4EA9\u6C56\uF4EA\u657A\uAE08"
			+ "\uBA78\u252E\u1CA6\uB4C6\uE8DD\u741F\u4BBD\u8B8A"
			+ "\u703E\uB566\u4803\uF60E\u6135\u57B9\u86C1\u1D9E"
			+ "\uE1F8\u9811\u69D9\u8E94\u9B1E\u87E9\uCE55\u28DF"
			+ "\u8CA1\u890D\uBFE6\u4268\u4199\u2D0F\uB054\uBB16";

	private  final byte[] Se = new byte[256];

	private  final int[] Te0 = new int[256], Te1 = new int[256],
			Te2 = new int[256], Te3 = new int[256];

	private  final byte[] Sd = new byte[256];

	private  final int[] Td0 = new int[256], Td1 = new int[256],
			Td2 = new int[256], Td3 = new int[256];

	/**
	 * Round constants
	 */
	private  final int[] rcon = new int[10];

	/**
	 * Number of rounds (depends on key size).
	 */
	private int Nr = 0;

	private int Nk = 0;

	private int Nw = 0;

	/**
	 * Encryption key schedule
	 */
	private int rek[] = null;

	/**
	 * Decryption key schedule
	 */
	private int rdk[] = null;

	/**
	 * Initialization
	 **/
	private void init() {
		int ROOT = 0x11B;
		int s1, s2, s3, i1, i2, i4, i8, i9, ib, id, ie, t;
		for (i1 = 0; i1 < 256; i1++) {
			char c = SS.charAt(i1 >>> 1);
			s1 = (byte) ((i1 & 1) == 0 ? c >>> 8 : c) & 0xff;
			s2 = s1 << 1;
			if (s2 >= 0x100) {
				s2 ^= ROOT;
			}
			s3 = s2 ^ s1;
			i2 = i1 << 1;
			if (i2 >= 0x100) {
				i2 ^= ROOT;
			}
			i4 = i2 << 1;
			if (i4 >= 0x100) {
				i4 ^= ROOT;
			}
			i8 = i4 << 1;
			if (i8 >= 0x100) {
				i8 ^= ROOT;
			}
			i9 = i8 ^ i1;
			ib = i9 ^ i2;
			id = i9 ^ i4;
			ie = i8 ^ i4 ^ i2;

			Se[i1] = (byte) s1;
			Te0[i1] = t = (s2 << 24) | (s1 << 16) | (s1 << 8) | s3;
			Te1[i1] = (t >>> 8) | (t << 24);
			Te2[i1] = (t >>> 16) | (t << 16);
			Te3[i1] = (t >>> 24) | (t << 8);

			Sd[s1] = (byte) i1;
			Td0[s1] = t = (ie << 24) | (i9 << 16) | (id << 8) | ib;
			Td1[s1] = (t >>> 8) | (t << 24);
			Td2[s1] = (t >>> 16) | (t << 16);
			Td3[s1] = (t >>> 24) | (t << 8);
		}

		/** Round constants **/
		int r = 1;
		rcon[0] = r << 24;
		for (int i = 1; i < 10; i++) {
			r <<= 1;
			if (r >= 0x100) {
				r ^= ROOT;
			}
			rcon[i] = r << 24;
		}
	}

	/**
	 * Expands a cipher key into a full encryption key schedule
	 * 
	 * @param cipherKey the cipher key (128, 192, or 256 bits)
	 */
	private void expandKey(byte[] cipherKey) {
		int temp, r = 0;
		for (int i = 0, k = 0; i < Nk; i++, k += 4) {
			rek[i] = ((cipherKey[k]) << 24) | ((cipherKey[k + 1] & 0xff) << 16)
					| ((cipherKey[k + 2] & 0xff) << 8)
					| ((cipherKey[k + 3] & 0xff));
		}
		for (int i = Nk, n = 0; i < Nw; i++, n--) {
			temp = rek[i - 1];
			if (n == 0) {
				n = Nk;
				temp = ((Se[(temp >>> 16) & 0xff]) << 24)
						| ((Se[(temp >>> 8) & 0xff] & 0xff) << 16)
						| ((Se[(temp) & 0xff] & 0xff) << 8)
						| ((Se[(temp >>> 24)] & 0xff));
				temp ^= rcon[r++];
			} else if (Nk == 8 && n == 4) {
				temp = ((Se[(temp >>> 24)]) << 24)
						| ((Se[(temp >>> 16) & 0xff] & 0xff) << 16)
						| ((Se[(temp >>> 8) & 0xff] & 0xff) << 8)
						| ((Se[(temp) & 0xff] & 0xff));
			}
			rek[i] = rek[i - Nk] ^ temp;
		}
		temp = 0;
	}

	/**
	 * Computes the decryption schedule from the encryption schedule
	 */
	private void invertKey() {
		int d = 0, e = 4 * Nr, w;

		/**
		 * Applies the inverse MixColumn transform to all round keys but the first and the last
		 **/
		rdk[d] = rek[e];
		rdk[d + 1] = rek[e + 1];
		rdk[d + 2] = rek[e + 2];
		rdk[d + 3] = rek[e + 3];
		d += 4;
		e -= 4;
		for (int r = 1; r < Nr; r++) {
			w = rek[e];
			rdk[d] = Td0[Se[(w >>> 24)] & 0xff]
					^ Td1[Se[(w >>> 16) & 0xff] & 0xff]
					^ Td2[Se[(w >>> 8) & 0xff] & 0xff]
					^ Td3[Se[(w) & 0xff] & 0xff];
			w = rek[e + 1];
			rdk[d + 1] = Td0[Se[(w >>> 24)] & 0xff]
					^ Td1[Se[(w >>> 16) & 0xff] & 0xff]
					^ Td2[Se[(w >>> 8) & 0xff] & 0xff]
					^ Td3[Se[(w) & 0xff] & 0xff];
			w = rek[e + 2];
			rdk[d + 2] = Td0[Se[(w >>> 24)] & 0xff]
					^ Td1[Se[(w >>> 16) & 0xff] & 0xff]
					^ Td2[Se[(w >>> 8) & 0xff] & 0xff]
					^ Td3[Se[(w) & 0xff] & 0xff];
			w = rek[e + 3];
			rdk[d + 3] = Td0[Se[(w >>> 24)] & 0xff]
					^ Td1[Se[(w >>> 16) & 0xff] & 0xff]
					^ Td2[Se[(w >>> 8) & 0xff] & 0xff]
					^ Td3[Se[(w) & 0xff] & 0xff];
			d += 4;
			e -= 4;
		}
		rdk[d] = rek[e];
		rdk[d + 1] = rek[e + 1];
		rdk[d + 2] = rek[e + 2];
		rdk[d + 3] = rek[e + 3];
	}

	/**
	 * Sets AES key schedule for encryption, decryption, or both
	 * 
	 * @param cipherKey the cipher key (128, 192, or 256 bits)
	 * @param keyBits size of the cipher key in bits
	 * @param direction cipher direction (DIR_ENCRYPT, DIR_DECRYPT, or DIR_BOTH)
	 */
	public void makeKey(byte[] cipherKey, int keyBits, int direction) {

		/** Checks key size **/
		if (keyBits != 128 && keyBits != 192 && keyBits != 256) {
			try {
				throw new Exception("Invalid AES key size (" + keyBits
						+ " bits)");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Nk = keyBits >>> 5;
		Nr = Nk + 6;
		Nw = 4 * (Nr + 1);
		rek = new int[Nw];
		rdk = new int[Nw];

		/** Checks direction **/
		if ((direction & DIR_BOTH) != 0) {
			expandKey(cipherKey);

			if ((direction & DIR_DECRYPT) != 0) {
				invertKey();
			}
		}
	}

	/**
	 * Sets AES key schedule (any cipher direction)
	 * 
	 * @param cipherKey the cipher key (128, 192, or 256 bits)
	 * @param keyBits size of the cipher key in bits
	 */
	public void makeKey(byte[] cipherKey, int keyBits) {
		makeKey(cipherKey, keyBits, DIR_BOTH);
	}

	/**
	 * Encrypts one block (BLOCK_SIZE bytes) of plaintext
	 * 
	 * @param pt plaintext block
	 * @param ct ciphertext block
	 * @return the ciphertext block
	 */
	public byte[] encryptBlock(byte[] pt, byte[] ct) {
		/**
		 * Maps byte array block to cipher state and adds initial round key
		 **/
		int k = 0, v;
		int t0 = ((pt[0]) << 24 | (pt[1] & 0xff) << 16 | (pt[2] & 0xff) << 8 | (pt[3] & 0xff))
				^ rek[0];
		int t1 = ((pt[4]) << 24 | (pt[5] & 0xff) << 16 | (pt[6] & 0xff) << 8 | (pt[7] & 0xff))
				^ rek[1];
		int t2 = ((pt[8]) << 24 | (pt[9] & 0xff) << 16 | (pt[10] & 0xff) << 8 | (pt[11] & 0xff))
				^ rek[2];
		int t3 = ((pt[12]) << 24 | (pt[13] & 0xff) << 16 | (pt[14] & 0xff) << 8 | (pt[15] & 0xff))
				^ rek[3];

		/** Nr-1 full rounds **/
		for (int r = 1; r < Nr; r++) {
			k += 4;
			int a0 = Te0[(t0 >>> 24)] ^ Te1[(t1 >>> 16) & 0xff]
					^ Te2[(t2 >>> 8) & 0xff] ^ Te3[(t3) & 0xff] ^ rek[k];
			int a1 = Te0[(t1 >>> 24)] ^ Te1[(t2 >>> 16) & 0xff]
					^ Te2[(t3 >>> 8) & 0xff] ^ Te3[(t0) & 0xff] ^ rek[k + 1];
			int a2 = Te0[(t2 >>> 24)] ^ Te1[(t3 >>> 16) & 0xff]
					^ Te2[(t0 >>> 8) & 0xff] ^ Te3[(t1) & 0xff] ^ rek[k + 2];
			int a3 = Te0[(t3 >>> 24)] ^ Te1[(t0 >>> 16) & 0xff]
					^ Te2[(t1 >>> 8) & 0xff] ^ Te3[(t2) & 0xff] ^ rek[k + 3];
			t0 = a0;
			t1 = a1;
			t2 = a2;
			t3 = a3;
		}

		/** Last round lacks MixColumn **/
		k += 4;

		v = rek[k];
		ct[0] = (byte) (Se[(t0 >>> 24)] ^ (v >>> 24));
		ct[1] = (byte) (Se[(t1 >>> 16) & 0xff] ^ (v >>> 16));
		ct[2] = (byte) (Se[(t2 >>> 8) & 0xff] ^ (v >>> 8));
		ct[3] = (byte) (Se[(t3) & 0xff] ^ (v));

		v = rek[k + 1];
		ct[4] = (byte) (Se[(t1 >>> 24)] ^ (v >>> 24));
		ct[5] = (byte) (Se[(t2 >>> 16) & 0xff] ^ (v >>> 16));
		ct[6] = (byte) (Se[(t3 >>> 8) & 0xff] ^ (v >>> 8));
		ct[7] = (byte) (Se[(t0) & 0xff] ^ (v));

		v = rek[k + 2];
		ct[8] = (byte) (Se[(t2 >>> 24)] ^ (v >>> 24));
		ct[9] = (byte) (Se[(t3 >>> 16) & 0xff] ^ (v >>> 16));
		ct[10] = (byte) (Se[(t0 >>> 8) & 0xff] ^ (v >>> 8));
		ct[11] = (byte) (Se[(t1) & 0xff] ^ (v));

		v = rek[k + 3];
		ct[12] = (byte) (Se[(t3 >>> 24)] ^ (v >>> 24));
		ct[13] = (byte) (Se[(t0 >>> 16) & 0xff] ^ (v >>> 16));
		ct[14] = (byte) (Se[(t1 >>> 8) & 0xff] ^ (v >>> 8));
		ct[15] = (byte) (Se[(t2) & 0xff] ^ (v));

		return ct;
	}

	/**
	 * Decrypts one block (BLOCK_SIZE bytes) of ciphertext
	 * 
	 * @param ct ciphertext block
	 * @param pt plaintext block
	 * @return the plaintext block
	 */
	public byte[] decryptBlock(byte[] ct, byte[] pt) {
		/**
		 * Maps byte array block to cipher state and adds initial round key
		 **/
		int k = 0, v;
		int t0 = ((ct[0]) << 24 | (ct[1] & 0xff) << 16 | (ct[2] & 0xff) << 8 | (ct[3] & 0xff))
				^ rdk[0];
		int t1 = ((ct[4]) << 24 | (ct[5] & 0xff) << 16 | (ct[6] & 0xff) << 8 | (ct[7] & 0xff))
				^ rdk[1];
		int t2 = ((ct[8]) << 24 | (ct[9] & 0xff) << 16 | (ct[10] & 0xff) << 8 | (ct[11] & 0xff))
				^ rdk[2];
		int t3 = ((ct[12]) << 24 | (ct[13] & 0xff) << 16 | (ct[14] & 0xff) << 8 | (ct[15] & 0xff))
				^ rdk[3];

		/** Nr-1 full rounds **/
		for (int r = 1; r < Nr; r++) {
			k += 4;
			int a0 = Td0[(t0 >>> 24)] ^ Td1[(t3 >>> 16) & 0xff]
					^ Td2[(t2 >>> 8) & 0xff] ^ Td3[(t1) & 0xff] ^ rdk[k];
			int a1 = Td0[(t1 >>> 24)] ^ Td1[(t0 >>> 16) & 0xff]
					^ Td2[(t3 >>> 8) & 0xff] ^ Td3[(t2) & 0xff] ^ rdk[k + 1];
			int a2 = Td0[(t2 >>> 24)] ^ Td1[(t1 >>> 16) & 0xff]
					^ Td2[(t0 >>> 8) & 0xff] ^ Td3[(t3) & 0xff] ^ rdk[k + 2];
			int a3 = Td0[(t3 >>> 24)] ^ Td1[(t2 >>> 16) & 0xff]
					^ Td2[(t1 >>> 8) & 0xff] ^ Td3[(t0) & 0xff] ^ rdk[k + 3];
			t0 = a0;
			t1 = a1;
			t2 = a2;
			t3 = a3;
		}

		/** Last round lacks MixColumn **/
		k += 4;

		v = rdk[k];
		pt[0] = (byte) (Sd[(t0 >>> 24)] ^ (v >>> 24));
		pt[1] = (byte) (Sd[(t3 >>> 16) & 0xff] ^ (v >>> 16));
		pt[2] = (byte) (Sd[(t2 >>> 8) & 0xff] ^ (v >>> 8));
		pt[3] = (byte) (Sd[(t1) & 0xff] ^ (v));

		v = rdk[k + 1];
		pt[4] = (byte) (Sd[(t1 >>> 24)] ^ (v >>> 24));
		pt[5] = (byte) (Sd[(t0 >>> 16) & 0xff] ^ (v >>> 16));
		pt[6] = (byte) (Sd[(t3 >>> 8) & 0xff] ^ (v >>> 8));
		pt[7] = (byte) (Sd[(t2) & 0xff] ^ (v));

		v = rdk[k + 2];
		pt[8] = (byte) (Sd[(t2 >>> 24)] ^ (v >>> 24));
		pt[9] = (byte) (Sd[(t1 >>> 16) & 0xff] ^ (v >>> 16));
		pt[10] = (byte) (Sd[(t0 >>> 8) & 0xff] ^ (v >>> 8));
		pt[11] = (byte) (Sd[(t3) & 0xff] ^ (v));

		v = rdk[k + 3];
		pt[12] = (byte) (Sd[(t3 >>> 24)] ^ (v >>> 24));
		pt[13] = (byte) (Sd[(t2 >>> 16) & 0xff] ^ (v >>> 16));
		pt[14] = (byte) (Sd[(t1 >>> 8) & 0xff] ^ (v >>> 8));
		pt[15] = (byte) (Sd[(t0) & 0xff] ^ (v));

		return pt;
	}

	/**
	 * Destroys all sensitive information in this object
	 */
	public void finalize() {
		if (rek != null) {
			for (int i = 0; i < rek.length; i++) {
				rek[i] = 0;
			}
			rek = null;
		}

		if (rdk != null) {
			for (int i = 0; i < rdk.length; i++) {
				rdk[i] = 0;
			}
			rdk = null;
		}
	}
}
}
