package com.book.ensureu.service;

import java.util.concurrent.ExecutionException;

public interface OtpService {
	
	public String generateOTP(String userName,int length) throws ExecutionException;

	public String getUserGeneratedOTP(String userName) throws ExecutionException;
	
	public boolean validateOtp(String userName,String otp) throws ExecutionException;

	public void inValidateOTP(String userName) throws ExecutionException;

	public String generateOTP(String userName)throws ExecutionException;
	
}
