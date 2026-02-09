package com.book.ensureu.util;

import java.util.Base64;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.book.ensureu.exception.GenericException;

public class EncryptDecryptUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(EncryptDecryptUtil.class.getName());

	private static String decryptedPropertyValue;
	private static String saltAddOn = "ensure-2019";
	private static String encryptedPropertyValue;
	private EncryptDecryptUtil() {

	}

	public static void main(String args[]) throws GenericException {
		
		/*String res0 = jasyptEncryptedPropertyValue("assessu123", "awsrdyfi@123");
		System.out.println("encr :" + res0);
		
		String decRes0 = jasyptDecryptedPropertyValue(res0, "awsrdyfi@123");
		System.out.println("dec :" + decRes0);
		
		String res = jasyptEncryptedPropertyValue("g4s@india2019", "awsrdyfi@123");
		System.out.println("encr :" + res);
		
		String decRes = jasyptDecryptedPropertyValue(res, "awsrdyfi@123");
		System.out.println("dec :" + decRes);
		
		String res1 = jasyptEncryptedPropertyValue("assessME#2018", "awsrdyfi@123");
		System.out.println("encr :" + res1);
		String decRes1 = jasyptDecryptedPropertyValue("3c9GSffFdkYfNsuqJ2HGQmM8PUjL4RfW", "awsrdyfi@123");
		System.out.println("dec :" + decRes1);*/
		
		
		String str="{\"_id\":1,\"name\":\"SSC\",\"description\":\"Staff Selection Commission\",\"imageIcon\":\"ssc.png\",\"examsModel\":[{\"_id\":1,\"name\":\"SSC CGL\",\"description\":\"SSC-CGL objective exame\",\"imageIcon\":\"ssc.png\"},{\"_id\":2,\"name\":\"SSC CPO\",\"description\":\"SSC-CPO objective exame\",\"imageIcon\":\"ssc.png\"},{\"_id\":3,\"name\":\"SSC CHSL\",\"description\":\"SSC-CPO objective exame\",\"imageIcon\":\"ssc.png\"}]}";
		String strEncr= jasyptEncryptedPropertyValue(str, "awsrdyfi@123");
		System.out.println("encr json string :" + strEncr);
		String decRes0 = jasyptDecryptedPropertyValue(strEncr, "awsrdyfi@123");
		System.out.println("decrypt json string "+decRes0);
		
	}

	public static String jasyptDecryptedPropertyValue(String encryptedPropertyValue, String salt)
			throws GenericException {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		salt = salt + saltAddOn;
		System.out.println("salt"+salt);
		encryptor.setPassword(salt);
		LOGGER.info("EncryptDecrypt.jasyptPropertyValue1: {} " ,encryptor);
		LOGGER.info("EncryptDecrypt.jasyptPropertyValue vault key {}", salt);
		decryptedPropertyValue = encryptor.decrypt(encryptedPropertyValue);
		LOGGER.info("EncryptDecrypt.jasyptPropertyValue encryted: {}", encryptedPropertyValue);
		LOGGER.info("EncryptDecrypt.jasyptPropertyValue decyrted: {}", decryptedPropertyValue);
		return decryptedPropertyValue;
	}

	public static String jasyptEncryptedPropertyValue(String decryptedPropertyValue, String salt)
			throws GenericException {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		salt = salt + saltAddOn;
		encryptor.setPassword(salt);
		LOGGER.info("EncryptDecrypt.jasyptPropertyValue1:{} ", encryptor);
		LOGGER.info("EncryptDecrypt.jasyptPropertyValue vault key {} ", salt);
		encryptedPropertyValue = encryptor.encrypt(decryptedPropertyValue);
		LOGGER.info("EncryptDecrypt.jasyptPropertyValue encryted:{} ", encryptedPropertyValue);
		LOGGER.info("EncryptDecrypt.jasyptPropertyValue decyrted:{} ", decryptedPropertyValue);
		return encryptedPropertyValue;
	}

	public static String encryptValue(String data) {
		return Base64.getEncoder().encodeToString(data.getBytes());
	}
}
