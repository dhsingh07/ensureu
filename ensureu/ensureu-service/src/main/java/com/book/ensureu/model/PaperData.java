package com.book.ensureu.model;

import java.util.List;

public class PaperData {
	
	 private List<Product> products;

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	@Override
	public String toString() {
		return "PaperData [products=" + products + "]";
	}

	   

}
