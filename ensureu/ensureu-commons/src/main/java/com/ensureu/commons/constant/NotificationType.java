package com.ensureu.commons.constant;

public enum NotificationType {

	EMAIL,SMS,BULKSMS,BULKEMAIL,PUSHNOTIFICATION,APPNOTIFICATION;
	
	@Override
	public String toString() {
		switch(this) {
		case SMS: return "SMS";
		case EMAIL: return "EMAIL";
		case BULKSMS : return "BULKSMS";
		case BULKEMAIL : return "BULKEMAIL";
		case PUSHNOTIFICATION : return "PUSHNOTIFCATION";
		case APPNOTIFICATION : return "APPNOTIFICATION";
		default:
			return null;		
		}
	}
	
}
