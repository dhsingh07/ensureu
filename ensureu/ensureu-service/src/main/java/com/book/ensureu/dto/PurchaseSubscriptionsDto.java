package com.book.ensureu.dto;

import java.util.List;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.PurchaseStatus;
import com.book.ensureu.constant.SubscriptionType;
import com.book.ensureu.constant.TestType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class PurchaseSubscriptionsDto {

	private Long id;
	
	private List<Long> listOfSubscriptionIds ;
	
	private String userId;
	
	private Long validity;
			
	private PaperType paperType;
	
	private TestType testType;
	
	private PaperCategory paperCategory;
	
	private PaperSubCategory paperSubCategory;
	
	private String description;
	
	private Long  createdDate;
	
	private PurchaseStatus parchaseStatus;
	
	private SubscriptionType subscriptionType;
	
	private Double actualPrice;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	

	public List<Long> getListOfSubscriptionIds() {
		return listOfSubscriptionIds;
	}

	public void setListOfSubscriptionIds(List<Long> listOfSubscriptionIds) {
		this.listOfSubscriptionIds = listOfSubscriptionIds;
	}

	public Long getValidity() {
		return validity;
	}

	public void setValidity(Long validity) {
		this.validity = validity;
	}

	public PaperType getPaperType() {
		return paperType;
	}

	public void setPaperType(PaperType paperType) {
		this.paperType = paperType;
	}

	public TestType getTestType() {
		return testType;
	}

	public void setTestType(TestType testType) {
		this.testType = testType;
	}

	public PaperCategory getPaperCategory() {
		return paperCategory;
	}

	public void setPaperCategory(PaperCategory paperCategory) {
		this.paperCategory = paperCategory;
	}

	public PaperSubCategory getPaperSubCategory() {
		return paperSubCategory;
	}

	public void setPaperSubCategory(PaperSubCategory paperSubCategory) {
		this.paperSubCategory = paperSubCategory;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Long createdDate) {
		this.createdDate = createdDate;
	}

	public PurchaseStatus getParchaseStatus() {
		return parchaseStatus;
	}

	public void setParchaseStatus(PurchaseStatus parchaseStatus) {
		this.parchaseStatus = parchaseStatus;
	}

	public SubscriptionType getSubscriptionType() {
		return subscriptionType;
	}

	public void setSubscriptionType(SubscriptionType subscriptionType) {
		this.subscriptionType = subscriptionType;
	}

	public Double getActualPrice() {
		return actualPrice;
	}

	public void setActualPrice(Double actualPrice) {
		this.actualPrice = actualPrice;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "PurchaseSubscriptionsDto [id=" + id + ", listOfSubscriptionIds=" + listOfSubscriptionIds + ", userId=" + userId
				+ ", validity=" + validity + ", paperType=" + paperType + ", testType=" + testType + ", paperCategory="
				+ paperCategory + ", paperSubCategory=" + paperSubCategory + ", description=" + description
				+ ", createdDate=" + createdDate + ", parchaseStatus=" + parchaseStatus + ", subscriptionType="
				+ subscriptionType + ", actualPrice=" + actualPrice + "]";
	}
	
	
}
