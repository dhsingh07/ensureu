package com.book.ensureu.service.provider;

import java.io.IOException;

import com.book.ensureu.web.WebClientException;

public interface ProviderTokenValidate {
	public boolean validateToken(String userId,String token) throws IOException,WebClientException;
	public boolean validateToken(String token) throws IOException,WebClientException;
}
