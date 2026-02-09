package com.book.ensureu.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class WebClientUtil {

	//GET httpUrlConnection 
	public static HttpURLConnection getHttpUrlConnection(String path,String requestMethod) throws IOException {
		URL url=new URL(path);
		HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
		httpURLConnection.setRequestMethod(requestMethod);
		httpURLConnection.setRequestProperty("Accept", "application/json");
		return httpURLConnection;
		
	}
	
	public static void putAllHeader(Map<? extends String, ?> headers,
			HttpURLConnection conn) {
		for (Map.Entry<? extends String, ?> entry : headers.entrySet()) {
			if(entry.getKey()!="userName" || entry.getKey()!="password"){
			conn.setRequestProperty(entry.getKey(), entry.getValue().toString());
			}
		}
	}
	
	
	public static void setAuthorization(Map<? extends String, ?> headers,
			HttpURLConnection conn)
	{
		if(headers!=null && headers.size()!=0)
		{
			if(headers.get("userName")!=null){
				String userName=(String) headers.get("userName");
				String password=(String)headers.get("password");
				String userpass = userName + ":" + password;
				String basicAuth = "Basic "
						+ javax.xml.bind.DatatypeConverter
								.printBase64Binary(userpass.getBytes());
				conn.setRequestProperty("Authorization", basicAuth);
			}
		}
	}
	
	
}
                                                                                          