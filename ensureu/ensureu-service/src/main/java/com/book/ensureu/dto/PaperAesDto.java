package com.book.ensureu.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class PaperAesDto<T> {

	private String name;
	private T body;
	
	
	public PaperAesDto() {
		super();
	}


	public PaperAesDto(T body) {
		super();
		this.body = body;
	}


	public PaperAesDto(String name, T body) {
		super();
		this.name = name;
		this.body = body;
	}


	public String getName() {
		return name;
	}


	public T getBody() {
		return body;
	}

	
}
