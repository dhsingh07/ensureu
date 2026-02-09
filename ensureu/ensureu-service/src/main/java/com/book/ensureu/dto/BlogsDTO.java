package com.book.ensureu.dto;

import java.io.Serializable;
import java.util.List;

public class BlogsDTO implements Serializable {

	private static final long serialVersionUID = 5637522702019666168L;

	private String id;
	private String description;
	private String title;
	private BlogCategoryDTO category;
	private List<String> tags;
	private String permalink;
	private String thumbnailUrl;
	private String author;
	private String userId;
	private Long createdDate;
	private Long updatedDate;
	private String body;
	private Long views;
	private short priority = 10;
	private List<String> likes;

	public short getPriority() {
		return priority;
	}

	public void setPriority(short priority) {
		if (priority > 10 || priority < 1)
			priority = 10;
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

	public BlogCategoryDTO getCategory() {
		return category;
	}

	public void setCategory(BlogCategoryDTO category) {
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
		this.createdDate = createdDate;
	}

	public Long getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Long updatedDate) {
		this.updatedDate = updatedDate;
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

	@Override
	public String toString() {
		return "BlogsDTO [id=" + id + ", description=" + description + ", title=" + title + ", category=" + category
				+ ", tags=" + tags + ", permalink=" + permalink + ", thumbnailUrl=" + thumbnailUrl + ", author="
				+ author + ", userId=" + userId + ", createdDate=" + createdDate + ", updatedDate=" + updatedDate
				+ ", body=" + body + ", views=" + views + ", priority=" + priority + ", likes=" + likes + "]";
	}

}
