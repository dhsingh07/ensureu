package com.book.ensureu.service.impl;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.book.ensureu.service.OtpService;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Service
public class OtpServiceImpl implements OtpService {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(OtpServiceImpl.class);

	@Value("${spring.user.otp.length}")
	private int length;
	
	public static void main(String args[]) {
		String res = new OtpServiceImpl().generateOTP("01234567", 4);
		System.out.println(res);
	}

	// cache based on username and OPT
	private static final Integer EXPIRE_MINS = 15;
	private LoadingCache<String, String> otpCache;

	public OtpServiceImpl() {
		super();
		LOGGER.info("Create a cache {}",1);
		otpCache = CacheBuilder.newBuilder().expireAfterWrite(EXPIRE_MINS, TimeUnit.MINUTES)
				.build(new CacheLoader<String, String>() {
					public String load(String key) {
						return "0";
					}
				});
	}

	@Override
	public String generateOTP(String userName, int length) {

		System.out.println("Generating OTP using random() : ");
		// Using random object
		String userNameProxy="0123456789";
		Random randamObj = new Random();
		char[] otp = new char[length];

		for (int i = 0; i < length; i++) {
			// Use of charAt() method : to get character value
			// Use of nextInt() as it is scanning the value as int
			otp[i] = userNameProxy.charAt(randamObj.nextInt(userNameProxy.length()));
		}
		LOGGER.info("genarted otp {}",String.valueOf(otp));
		try {
			inValidateOTP(userName);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		otpCache.put(userName, String.valueOf(otp));
		LOGGER.info("otpCache otp {}",otpCache);
		return String.valueOf(otp);
	}
	
	@Override
	public String generateOTP(String userName) {
		return generateOTP(userName,length);
	}

	@Override
	public String getUserGeneratedOTP(String userName) throws ExecutionException {
		return otpCache.get(userName);
	}

	@Override
	public void inValidateOTP(String userName) throws ExecutionException {
		otpCache.invalidate(userName);
	}

	@Override
	public boolean validateOtp(String userName, String otp) throws ExecutionException {
		String cachedOtp = getUserGeneratedOTP(userName);
		boolean validOtp = false;
		if (cachedOtp != null && !cachedOtp.isEmpty()) {
			if (cachedOtp.equals(otp)) {
				validOtp = true;
			}
		}
		return validOtp;
	}

}
