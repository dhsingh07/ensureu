package com.book.ensureu.model;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "blogs")
public class BlogsModel implements Serializable {

	private static final long serialVersionUID = 5637522702019666168L;

	@Id
	private String id;
	private String description;
	private String title;
	private BlogCategoryModel category;
	private List<String> tags;
	private String permalink;
	private String thumbnailUrl;
	private String author;
	private String userId;
	private Long createdDate;
	private Long updatedDate;
	private String body;
	private Long views;
	private short priority;
	private List<String> likes;

	@Override
	public String toString() {
		return "BlogsModel [id=" + id + ", description=" + description + ", title=" + title + ", category=" + category
				+ ", tags=" + tags + ", permalink=" + permalink + ", thumbnailUrl=" + thumbnailUrl + ", author="
				+ author + ", userId=" + userId + ", createdDate=" + createdDate + ", updatedDate=" + updatedDate
				+ ", body=" + body + ", views=" + views + ", priority=" + priority + ", likes=" + likes + "]";
	}

	public short getPriority() {
		return priority;
	}

	public void setPriority(short priority) {
		this.priority = priority;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public BlogCategoryModel getCategory() {
		return category;
	}

	public void setCategory(BlogCategoryModel category) {
		this.category = category;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public String getPermalink() {
		return permalink;
	}

	public void setPermalink(String permalink) {
		this.permalink = permalink;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Long getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Long createdDate) {
		this.createdDate = createdDate != null ? createdDate : System.currentTimeMillis();
	}

	public Long getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Long updatedDate) {
		this.updatedDate = updatedDate != null ? updatedDate : System.currentTimeMillis();
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Long getViews() {
		return views;
	}

	public void setViews(Long views) {
		this.views = views;
	}

	public List<String> getLikes() {
		return likes;
	}

	public void setLikes(List<String> likes) {
		this.likes = likes;
	}

}
