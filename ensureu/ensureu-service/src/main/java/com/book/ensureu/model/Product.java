package com.book.ensureu.model;

import java.util.Arrays;

public class Product {

	private String hasExtendedValidity;

	// private Courses[] courses;

	private String recommendedFor;

	private String discountText;

	private String type;

	private String stage;

	private String title;

	private String releaseDate;

	private String _id;

	private String validity;

	private String description;

	private String[] features;

	private String isRecommended;

	private String quantity;

	private String colorHex;

	private String oldCost;

	private String createdby;

	private String cost;

	private String isCustom;

	private String isHidden;

	private String createdOn;

	private String availTill;

	private String category;

	// private Items[] items;

	private String itemCount;

	// private SpecificExams[] specificExams;

	private String minPrice;

	public String getHasExtendedValidity() {
		return hasExtendedValidity;
	}

	public void setHasExtendedValidity(String hasExtendedValidity) {
		this.hasExtendedValidity = hasExtendedValidity;
	}

	/*
	 * public Courses[] getCourses () { return courses; }
	 * 
	 * public void setCourses (Courses[] courses) { this.courses = courses; }
	 */
	public String getRecommendedFor() {
		return recommendedFor;
	}

	public void setRecommendedFor(String recommendedFor) {
		this.recommendedFor = recommendedFor;
	}

	public String getDiscountText() {
		return discountText;
	}

	public void setDiscountText(String discountText) {
		this.discountText = discountText;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getValidity() {
		return validity;
	}

	public void setValidity(String validity) {
		this.validity = validity;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String[] getFeatures() {
		return features;
	}

	public void setFeatures(String[] features) {
		this.features = features;
	}

	public String getIsRecommended() {
		return isRecommended;
	}

	public void setIsRecommended(String isRecommended) {
		this.isRecommended = isRecommended;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getColorHex() {
		return colorHex;
	}

	public void setColorHex(String colorHex) {
		this.colorHex = colorHex;
	}

	public String getOldCost() {
		return oldCost;
	}

	public void setOldCost(String oldCost) {
		this.oldCost = oldCost;
	}

	public String getCreatedby() {
		return createdby;
	}

	public void setCreatedby(String createdby) {
		this.createdby = createdby;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public String getIsCustom() {
		return isCustom;
	}

	public void setIsCustom(String isCustom) {
		this.isCustom = isCustom;
	}

	public String getIsHidden() {
		return isHidden;
	}

	public void setIsHidden(String isHidden) {
		this.isHidden = isHidden;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public String getAvailTill() {
		return availTill;
	}

	public void setAvailTill(String availTill) {
		this.availTill = availTill;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	/*
	 * public Items[] getItems () { return items; }
	 * 
	 * public void setItems (Items[] items) { this.items = items; }
	 */

	public String getItemCount() {
		return itemCount;
	}

	public void setItemCount(String itemCount) {
		this.itemCount = itemCount;
	}

	/*
	 * public SpecificExams[] getSpecificExams () { return specificExams; }
	 * 
	 * public void setSpecificExams (SpecificExams[] specificExams) {
	 * this.specificExams = specificExams; }
	 */

	public String getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(String minPrice) {
		this.minPrice = minPrice;
	}

	@Override
	public String toString() {
		return "Product [hasExtendedValidity=" + hasExtendedValidity + ", recommendedFor=" + recommendedFor
				+ ", discountText=" + discountText + ", type=" + type + ", stage=" + stage + ", title=" + title
				+ ", releaseDate=" + releaseDate + ", _id=" + _id + ", validity=" + validity + ", description="
				+ description + ", features=" + Arrays.toString(features) + ", isRecommended=" + isRecommended
				+ ", quantity=" + quantity + ", colorHex=" + colorHex + ", oldCost=" + oldCost + ", createdby="
				+ createdby + ", cost=" + cost + ", isCustom=" + isCustom + ", isHidden=" + isHidden + ", createdOn="
				+ createdOn + ", availTill=" + availTill + ", category=" + category + ", itemCount=" + itemCount
				+ ", minPrice=" + minPrice + "]";
	}

	
}
