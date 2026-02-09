package com.book.ensureu.dto;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "blog_comments")
public class BlogCommentsDTO {

	private String id;
	private String blogId;
	private List<CommentsDTO> comments;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBlogId() {
		return blogId;
	}

	public void setBlogId(String blogId) {
		this.blogId = blogId;
	}

	public List<CommentsDTO> getComments() {
		return comments;
	}

	public void setComments(List<CommentsDTO> comments) {
		this.comments = comments;
	}

	@Override
	public String toString() {
		return "BlogCommentsDTO [id=" + id + ", blogId=" + blogId + ", comments=" + comments + "]";
	}

}
