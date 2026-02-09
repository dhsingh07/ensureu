package com.book.ensureu.model;

import java.io.Serializable;

public class ExamsModel implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3975395144919643099L;
	private Long id;
	private String name;
	private String description;
	private String imageIcon;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getImageIcon() {
		return imageIcon;
	}
	public void setImageIcon(String imageIcon) {
		this.imageIcon = imageIcon;
	}
	@Override
	public String toString() {
		return "ExamsModel [id=" + id + ", name=" + name + ", description=" + description + ", imageIcon=" + imageIcon
				+ "]";
	}
	
	
	
}
