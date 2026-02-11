package com.ensureu.commons.notification.data.reposne;


public class Response<T> {
private int code;
private String message;
private T body;
private String error;

private Response(ResponseBuilder<T> responseBuilder) {
	super();
	this.code = responseBuilder.code;
	this.message = responseBuilder.message;
	this.error =responseBuilder.error;
	this.body=responseBuilder.body;
}

public int getCode() {
	return code;
}

public String getMessage() {
	return message;
}

public T getBody() {
	return body;
}

public String getError() {
	return error;
}

public static class ResponseBuilder<T>
{
private T body;
private int code;
private String message;

public ResponseBuilder(int code, String message){
	this.code=code;
	this.message=message;
}

public ResponseBuilder<T> setBody(T body) {
	this.body=body;
	return this;
}

private String error;

public ResponseBuilder<T> setError(String error) {
	this.error=error;
	return this;
}

public Response build() {
	Response response=new Response(this);
	return response;
}

}

}
