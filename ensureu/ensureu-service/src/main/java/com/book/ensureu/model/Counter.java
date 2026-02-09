package com.book.ensureu.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection ="counter")
public class Counter {
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getCounter() {
		return counter;
	}

	public void setCounter(Long counter) {
		this.counter = counter;
	}

	@Id
	private String name;
	
	private Long counter;
	
	public Counter(String name, Long counter){
		this.name=name;
		this.counter=counter;
		
	}
	
}
