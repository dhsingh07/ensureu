package com.book.ensureu.util;

import java.time.LocalDate;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

public class HashUtil {

	public static String hashByMD5(String... inputs) {
		StringBuilder stringBuilder = new StringBuilder();
		for (String input : inputs) {
			stringBuilder.append(input);
		}
		byte[] encodedId = Base64.encodeBase64((stringBuilder.toString()).getBytes());
		return DigestUtils.md5Hex(encodedId);
	}
	
	
	public static String setPassphraseEnrichment(String passphrase) {
		// SIMPLIFIED: Return plain passphrase without date for debugging
		// TODO: Re-enable date enrichment after encryption issue is fixed
		// LocalDate date=LocalDate.now();
		// return passphrase+date;
		return passphrase;
	}
	
}
