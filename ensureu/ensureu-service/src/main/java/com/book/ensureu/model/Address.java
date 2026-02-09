package com.book.ensureu.model;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Address implements Serializable{
/**
	 * 
	 */
private static final long serialVersionUID = -8037832264376353010L;

private String houseNumber;
private String addressLine1;
private String addressLine2;
private String city;
private String state;
private String country;

public String getAddressLine1() {
	return addressLine1;
}
public void setAddressLine1(String addressLine1) {
	this.addressLine1 = addressLine1;
}
public String getAddressLine2() {
	return addressLine2;
}
public void setAddressLine2(String addressLine2) {
	this.addressLine2 = addressLine2;
}
public String getCity() {
	return city;
}
public void setCity(String city) {
	this.city = city;
}
public String getState() {
	return state;
}
public void setState(String state) {
	this.state = state;
}
public String getCountry() {
	return country;
}
public void setCountry(String country) {
	this.country = country;
}
public String getHouseNumber() {
	return houseNumber;
}
public void setHouseNumber(String houseNumber) {
	this.houseNumber = houseNumber;
}

@Override
public String toString() {
	return "Address [houseNumber=" + houseNumber + ", addressLine1="
			+ addressLine1 + ", addressLine2=" + addressLine2 + ", city="
			+ city + ", state=" + state + ", country=" + country + "]";
}


}
