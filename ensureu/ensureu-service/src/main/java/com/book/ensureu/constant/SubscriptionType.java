package com.book.ensureu.constant;

import java.util.LinkedList;
import java.util.List;

public enum SubscriptionType {
	MONTHLY(1), HALFYEARLY(6), FORTNIGHT(.5), YEARLY(12), 
	QUATERLY(3),DAY(0);

	private double val;

	private SubscriptionType(double num) {
		this.val = num;
	}

	public double getVal() {
		return val;
	}
	
	public static List<SubscriptionType> getAll(){
		List<SubscriptionType> listOfSubscriptionType = new LinkedList<>();
		listOfSubscriptionType.add(DAY);
		//listOfSubscriptionType.add(FORTNIGHT);
		listOfSubscriptionType.add(MONTHLY);
		listOfSubscriptionType.add(QUATERLY);
		listOfSubscriptionType.add(HALFYEARLY);
		//listOfSubscriptionType.add(YEARLY);
		return listOfSubscriptionType;
	}

}
