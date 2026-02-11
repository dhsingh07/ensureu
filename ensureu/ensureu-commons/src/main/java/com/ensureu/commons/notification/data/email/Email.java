package com.ensureu.commons.notification.data.email;

import java.util.Collection;

public interface Email {

	String getFromAddress();

	String getDefaultFromAddress();

	Collection<String> getToAddress();

	Collection<String> getCcAddrress();

	Collection<String> getBccAddress();

	String getSubject();

	String getBody();

	void addToAddress(String toAddress);

	void addCcAddress(String ccAddress);

	void AddBccAddress(String bccAddress);

}
