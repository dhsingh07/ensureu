package com.book.ensureu.response.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {

	private int status;
	private String message;
	private T body;
	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public Response<T> setStatus(int status) {
		this.status = status;
		return this;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public Response<T> setMessage(String message) {
		this.message = message;
		return this;
	}
	/**
	 * @return the body
	 */
	public T getBody() {
		return body;
	}
	/**
	 * @param body the body to set
	 */
	public Response<T> setBody(T body) {
		this.body = body;
		return this;
	}
	
	
}
