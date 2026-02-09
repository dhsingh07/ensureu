package com.book.ensureu.model;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.book.ensureu.dto.CommentsDTO;

@Document(collection = "blog_comments")
public class BlogCommentsModel {

	private String id;
	private String blogId;
	private List<CommentsDTO> comments;

	public String getBlogId() {
		return blogId;
	}

	public void setBlogId(String blogId) {
		this.blogId = blogId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<CommentsDTO> getComments() {
		return comments;
	}

	public void setComments(List<CommentsDTO> comments) {
		this.comments = comments;
	}

	@Override
	public String toString() {
		return "BlogCommentsModel [id=" + id + ", blogId=" + blogId + ", comments=" + comments + "]";
	}

}
