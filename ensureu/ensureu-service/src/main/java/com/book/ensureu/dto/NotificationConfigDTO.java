package com.book.ensureu.dto;

import java.io.Serializable;
import java.util.Map;

public class NotificationConfigDTO implements Serializable {

	private static final long serialVersionUID = 7121403761731626868L;

	private String id;
	private String category;
	private String subCategory;
	private boolean active;
	private Map<String, NotificationRuleConfigDTO> rules;
	private Long createdDate;
	private String createdBy;
	private Long updatedDate;
	private String updatedBy;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Map<String, NotificationRuleConfigDTO> getRules() {
		return rules;
	}

	public void setRules(Map<String, NotificationRuleConfigDTO> rules) {
		this.rules = rules;
	}

	public Long getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Long createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Long getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Long updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	@Override
	public String toString() {
		return "NotificationConfigDTO [id=" + id + ", category=" + category + ", subCategory=" + subCategory
				+ ", active=" + active + ", rules=" + rules + ", createdDate=" + createdDate + ", createdBy="
				+ createdBy + ", updatedDate=" + updatedDate + ", updatedBy=" + updatedBy + "]";
	}

}
