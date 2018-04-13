package com.easier.writepre.jni;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESUtils {

	public static byte[] aesEncrypt(byte[] source)
			throws GeneralSecurityException, UnsupportedEncodingException {
		// 处理密钥
		byte rawKeyData[] = JniManager.getAESCode().getBytes("utf-8");
		// KeyGenerator kgen = KeyGenerator.getInstance("AES");
		// kgen.init(128, new SecureRandom(rawKeyData));
		// SecretKey secretKey = kgen.generateKey();
		// byte[] enCodeFormat = secretKey.getEncoded();
		SecretKeySpec key = new SecretKeySpec(rawKeyData, "AES");
		// 加密
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(source);
	}

	public static byte[] aesDecrypt(byte[] data)
			throws GeneralSecurityException, UnsupportedEncodingException {
		// 处理密钥
		byte rawKeyData[] = JniManager.getAESCode().getBytes("UTF-8");
		SecretKeySpec key = new SecretKeySpec(rawKeyData, "AES");
		// 解密
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(data);
	}
}
