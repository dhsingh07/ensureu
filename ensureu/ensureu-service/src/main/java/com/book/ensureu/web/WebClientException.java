package com.book.ensureu.web;

public class WebClientException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2957746947639548579L;
	private int statusCode;
	
	public WebClientException(String message, int statusCode) {
		super(message);
		this.statusCode=statusCode;
	}
	
	public WebClientException(String message,int statusCode, Throwable throwable) {
		super(message, throwable);
		this.statusCode=statusCode;
	}
	
	public int getStatusCode() {
		return statusCode;
	}
	
	
}
