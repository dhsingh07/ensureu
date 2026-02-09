package com.book.ensureu.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "blog_category")
public class BlogCategoryModel implements Serializable {

	private static final long serialVersionUID = 4876250993793868501L;

	@Id
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
		this.createdDate = createdDate != null ? createdDate : System.currentTimeMillis();
	}

	@Override
	public String toString() {
		return "BlogCategoryModel [id=" + id + ", name=" + name + ", createdDate=" + createdDate + "]";
	}

}
