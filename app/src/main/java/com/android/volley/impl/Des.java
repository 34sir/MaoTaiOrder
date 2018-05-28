package com.android.volley.impl;

import com.android.volley.b;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;


/**
 * @author Sojex
 */
public class Des {
	/** 指定加密算法为DESede */
	private static String ALGORITHM = "DESede";
	private static String ALGORITHM_MODE = "DESede/CBC/PKCS5Padding";
	private final static String iv = "sojexcom";
	private String charset = "utf-8";
	Cipher cipher_en = null;
	Cipher cipher_de = null;

	public Des(){
		this(0);
	}

	public Des(int version){
		this(version==1? b.b: b.a);
	}

	public Des(String k) {
		try {
			// 从原始密匙数据创建一个DESKeySpec对象
			DESedeKeySpec dks;
			dks = new DESedeKeySpec(k.getBytes(charset));

//			GLog.d("foxlee+++++++++++++++++a ",b.a);
//			GLog.d("foxlee+++++++++++++++++b ",b.b);
//			GLog.d("foxlee+++++++++++++++++c ",b.c);
			// 创建�?��密匙工厂，然后用它把DESKeySpec转换成一个SecretKey对象
			SecretKeyFactory keyFactory = SecretKeyFactory
					.getInstance(ALGORITHM);
			SecretKey key = keyFactory.generateSecret(dks);
			// Cipher对象实际完成加密操作
			cipher_en = Cipher.getInstance(ALGORITHM_MODE);
			IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
			// 用密匙初始化Cipher对象
			cipher_en.init(Cipher.ENCRYPT_MODE, key, ips);
			cipher_de = Cipher.getInstance(ALGORITHM_MODE);
			cipher_de.init(Cipher.DECRYPT_MODE, key, ips);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public String encryptStr(String str) throws IllegalBlockSizeException,
            BadPaddingException, UnsupportedEncodingException {
		// 现在，获取数据并加密
		byte data[] = str.getBytes(charset);
		return encryptStr(data);
	}

	public byte[] encryptByte(String str) throws IllegalBlockSizeException,
            BadPaddingException, UnsupportedEncodingException {
		// 现在，获取数据并加密
		return cipher_en.doFinal(str.getBytes(charset));
	}

	public String encryptStr(byte[] data) throws IllegalBlockSizeException,
            BadPaddingException, UnsupportedEncodingException {
		// 正式执行加密操作
		byte[] encryptedData = cipher_en.doFinal(data);
		return Base64Encoder.encode(encryptedData);
	}

	/**
	 * 解密方法
	 *
	 * @param encryptedData
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeySpecException
	 * @throws UnsupportedEncodingException
	 */
	public String decryptStr(String encryptedData)
			throws IllegalBlockSizeException, BadPaddingException,
            UnsupportedEncodingException {
		// 正式执行解密操作
		encryptedData=encryptedData.replace("+","-");
		byte decryptedData[] = cipher_de.doFinal(Base64Decoder.decodeToBytes(encryptedData));
		return new String(decryptedData, charset);
	}

	public byte[] decryptStrtoByte(String encryptedData)
			throws IllegalBlockSizeException, BadPaddingException,
            UnsupportedEncodingException {
		// 正式执行解密操作
		byte[] bytes= Base64Decoder.decodeToBytes(encryptedData);
		bytes =cipher_de.doFinal(bytes);
		return bytes;
	}
}
