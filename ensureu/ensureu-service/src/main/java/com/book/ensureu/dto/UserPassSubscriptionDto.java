package com.book.ensureu.dto;

import com.book.ensureu.constant.SubscriptionType;

public class UserPassSubscriptionDto {

private Long id;
	
	private SubscriptionType subscriptionType;
	
	private Double discountPercentage;
	
	private Double price;
	
	private String description;
	
	private Boolean active;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	
	/**
	 * @return the subscriptionType
	 */
	public SubscriptionType getSubscriptionType() {
		return subscriptionType;
	}

	/**
	 * @param subscriptionType the subscriptionType to set
	 */
	public void setSubscriptionType(SubscriptionType subscriptionType) {
		this.subscriptionType = subscriptionType;
	}

	/**
	 * @return the discountPercentage
	 */
	public Double getDiscountPercentage() {
		return discountPercentage;
	}

	/**
	 * @param discountPercentage the discountPercentage to set
	 */
	public void setDiscountPercentage(Double discountPercentage) {
		this.discountPercentage = discountPercentage;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the active
	 */
	public Boolean getActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(Boolean active) {
		this.active = active;
	}

	/**
	 * @return the price
	 */
	public Double getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(Double price) {
		this.price = price;
	}
   
	
	

	
}
