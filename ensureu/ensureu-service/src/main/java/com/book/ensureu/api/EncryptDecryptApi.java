package com.book.ensureu.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.book.ensureu.exception.GenericException;
import com.book.ensureu.util.EncryptDecryptUtil;

@RestController
@RequestMapping("/encrypt")
public class EncryptDecryptApi {

	@Value("${spring.encryption.salt}")
	private String salt;
	
	@RequestMapping(value="/enc", method=RequestMethod.GET)
	public String encryptString(@RequestParam(value="input") final String input) throws GenericException {

		String encryptedValue=null;
		try {
			encryptedValue=EncryptDecryptUtil.jasyptEncryptedPropertyValue(input, salt);
		} catch (GenericException e) {
			throw e;
		}
		return encryptedValue;
	}
	
	
	@RequestMapping(value="/dec", method=RequestMethod.GET)
	public String decryptString(@RequestParam(value="input") final String input) throws GenericException {

		String decryptedValue=null;
		try {
			decryptedValue=EncryptDecryptUtil.jasyptDecryptedPropertyValue(input, salt);
		} catch (GenericException e) {
			throw e;
		}
		return decryptedValue;
	}
	
	
	
}
