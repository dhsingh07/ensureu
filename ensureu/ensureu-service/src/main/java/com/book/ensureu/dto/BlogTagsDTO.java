package com.book.ensureu.dto;

import java.io.Serializable;

public class BlogTagsDTO implements Serializable {

	private static final long serialVersionUID = 4876250993793868501L;

	private String id;
	private String name;
	private Long createdDate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Long createdDate) {
		this.createdDate = createdDate;
	}

	@Override
	public String toString() {
		return "BlogTagsDTO [id=" + id + ", name=" + name + ", createdDate=" + createdDate + "]";
	}

}
