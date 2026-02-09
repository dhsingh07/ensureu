package com.ensureu.commons.exception;

import java.util.Date;

public class ExcecptionMessage {
private String message;
private String content;
private Date timeStamp;


public ExcecptionMessage(String message, Date timeStamp) {
	super();
	this.message = message;
	this.timeStamp = timeStamp;
}
public String getMessage() {
	return message;
}
public void setMessage(String message) {
	this.message = message;
}
public String getContent() {
	return content;
}
public void setContent(String content) {
	this.content = content;
}
public Date getTimeStamp() {
	return timeStamp;
}
public void setTimeStamp(Date timeStamp) {
	this.timeStamp = timeStamp;
}


}
