package com.book.ensureu.security.util;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class OptoinsRequest {

	public static void setOptionsOnRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");

		Enumeration<String> headersEnum = request.getHeaders("Access-Control-Request-Headers");
		StringBuilder headers = new StringBuilder();
		String delim = "";
		while (headersEnum.hasMoreElements()) {
			headers.append(delim).append(headersEnum.nextElement());
			delim = ", ";
		}
		response.setHeader("Access-Control-Allow-Headers", headers.toString());

		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			response.setStatus(200);
		}
	}
	
}
