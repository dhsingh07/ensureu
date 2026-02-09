package com.book.ensureu.dto;

import org.springframework.data.annotation.Id;

public class CommentsDTO {

	@Id
	private String id;
	private String body;
	private String createdBy;
	private Long createdDate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Long getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Long createdDate) {
		this.createdDate = System.currentTimeMillis();
	}

	@Override
	public String toString() {
		return "CommentsDTO [id=" + id + ", body=" + body + ", createdBy=" + createdBy + ", createdDate=" + createdDate
				+ "]";
	}

}
