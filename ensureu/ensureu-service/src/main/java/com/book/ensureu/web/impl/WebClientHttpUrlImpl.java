package com.book.ensureu.web.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.book.ensureu.util.WebClientUtil;
import com.book.ensureu.web.WebClient;
import com.book.ensureu.web.WebClientException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WebClientHttpUrlImpl implements WebClient {

	private static final Logger LOGGER=LoggerFactory.getLogger(WebClientHttpUrlImpl.class.getName());
	
	private ObjectMapper objectMapper=new ObjectMapper();
	
	@Override
	public <T> T get(String path, Map<String, Object> headers, Class<T> returnType)
			throws IOException, WebClientException {
		HttpURLConnection connection=null;
		try {
		connection=WebClientUtil.getHttpUrlConnection(path, "GET");
		connection.setDefaultUseCaches(false);
		WebClientUtil.putAllHeader(headers, connection);
		WebClientUtil.setAuthorization(headers,connection);
		int statusCode = connection.getResponseCode();
		if (HttpStatus.SC_OK <= statusCode
				&& statusCode < HttpStatus.SC_MULTI_STATUS) {
			return objectMapper
					.readValue(connection.getInputStream(), returnType);
		} else {
			throw new WebClientException(connection.getResponseMessage(),
					statusCode);
		}
		}catch(WebClientException ex) {
			LOGGER.error("Exception while http connection..", ex);
			throw ex;
		}finally{
			connection.disconnect();
		}
		
	}

	@Override
	public <T> T post(String path, MediaType mediaType, byte[] data, Map<String, Object> headers, Class<T> returnType)
			throws IOException, WebClientException {
		HttpURLConnection connection=null;
		try {
			connection=WebClientUtil.getHttpUrlConnection(path, "POST");
			connection.setDefaultUseCaches(false);
			connection.setDoOutput(true);
			WebClientUtil.putAllHeader(headers, connection);
			WebClientUtil.setAuthorization(headers, connection);
			OutputStream out=connection.getOutputStream();
			out.write(data);
			out.flush();
			int statusCode = connection.getResponseCode();
			if (HttpStatus.SC_OK <= statusCode
					&& statusCode < HttpStatus.SC_MULTI_STATUS) {
				return objectMapper
						.readValue(connection.getInputStream(), returnType);
			} else {
				throw new WebClientException(connection.getResponseMessage(),
						statusCode);
			}
			
		}catch(WebClientException ex) {
			throw ex;
		}finally {
			connection.disconnect();
		}
	}

	@Override
	public byte[] getRaw(String path, Map<String, Object> headers) throws IOException, WebClientException {
		
		HttpURLConnection connection=null;
		try {
		connection=WebClientUtil.getHttpUrlConnection(path, "GET");
		connection.setDefaultUseCaches(false);
		WebClientUtil.putAllHeader(headers, connection);
		WebClientUtil.setAuthorization(headers,connection);
		int statusCode = connection.getResponseCode();
		System.out.println("statusCode "+ statusCode);
		if (HttpStatus.SC_OK <= statusCode
				&& statusCode < HttpStatus.SC_MULTI_STATUS) {
			ByteArrayOutputStream byteArray=new ByteArrayOutputStream();
			InputStream in=connection.getInputStream();
			byte[] buffer = new byte[1024];
			
			int len;
			// read bytes from the input stream and store them in buffer
			while ((len = in.read(buffer)) != -1) {
				// write bytes from the buffer into output stream
				byteArray.write(buffer, 0, len);
			}
			return byteArray.toByteArray();
		} else {
			throw new WebClientException(connection.getResponseMessage(),
					statusCode);
		}
		}catch(WebClientException ex) {
			LOGGER.error("Exception while http connection..", ex);
			throw ex;
		}finally{
			//connection.disconnect();
		}
		
		
	}

	@Override
	public String post(String path, MediaType mediaType, byte[] data, Map<String, Object> headers)
			throws IOException, WebClientException {
		return null;
	}

	@Override
	public void postNoReturn(String path, String mediaType, byte[] data, Map<String, Object> headers)
			throws IOException, WebClientException {

		try {
			LOGGER.info("Creating Request ....");
			HttpURLConnection connection = WebClientUtil.getHttpUrlConnection(path, "POST");
			WebClientUtil.putAllHeader(headers, connection);
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			if (headers.get("userName") != null) {
				WebClientUtil.setAuthorization(headers, connection);
			}
			OutputStream os = connection.getOutputStream();
			os.write(data);
			os.flush();
			int statusCode = connection.getResponseCode();
			if (HttpStatus.SC_OK <= statusCode
					&& statusCode < HttpStatus.SC_MULTI_STATUS) {
				LOGGER.info("Success ...");
			} else {
				throw new WebClientException(connection.getResponseMessage(),
						statusCode);
			}	

		}catch (IOException e) {
			throw e;
		}
		
	}

}