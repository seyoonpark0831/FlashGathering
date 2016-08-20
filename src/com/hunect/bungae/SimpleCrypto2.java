package com.hunect.bungae;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;


public class SimpleCrypto2 {
	   
	public static String encrypt1(String seed, String cleartext) throws Exception
	{
		byte[] rawKey = seed.getBytes();
		byte[] result = encrypt2(rawKey, cleartext.getBytes());
		return Base64.encodeToString(result, 0);
	}
	
	public static String decrypt1(String seed, String encrypted) throws Exception
	{
		byte[] rawKey = seed.getBytes();
		byte[] enc = Base64.decode(encrypted, 0);
		byte[] result = decrypt2(rawKey, enc);
		return new String(result);
	}
	
	private static byte[] encrypt2(byte[] raw, byte[] clear) throws Exception
	{
	    SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
		// ECB, CBC, CFB, OFB, CTR
	    cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
	    byte[] encrypted = cipher.doFinal(clear);
		return encrypted;
	}

	private static byte[] decrypt2(byte[] raw, byte[] encrypted) throws Exception
	{
	    SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
	    cipher.init(Cipher.DECRYPT_MODE, skeySpec);
	    byte[] decrypted = cipher.doFinal(encrypted);
		return decrypted;
	}
}