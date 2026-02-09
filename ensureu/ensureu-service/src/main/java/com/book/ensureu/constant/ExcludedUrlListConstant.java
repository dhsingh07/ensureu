package com.book.ensureu.constant;

import java.util.ArrayList;
import java.util.List;

public class ExcludedUrlListConstant {

	public static List<String> excludedUrlList;
	
	static {
		excludedUrlList=new ArrayList<>();
		excludedUrlList.add("");
	}
	
	public static boolean checkByPassUrl(StringBuffer requestUrl) {

//		 return true;
		return requestUrl.toString().contains("auth/providertoken") 
				|| requestUrl.toString().contains("auth/token") 
				|| requestUrl.toString().contains("user/create")
				|| requestUrl.toString().contains("course/list") 
				|| requestUrl.toString().contains("/image/questions")
				|| requestUrl.toString().contains("/subscription/getAllType/PAID")
				|| requestUrl.toString().contains("/subscription/getAllType/FREE")
				|| requestUrl.toString().contains("/pass") 
				|| requestUrl.toString().contains("/add")
				|| requestUrl.toString().contains("v2/api-docs") 
				|| requestUrl.toString().contains("/swagger-ui.html")
				|| requestUrl.toString().contains("/swagger-resources")
		        || requestUrl.toString().contains("/otp")
		        || requestUrl.toString().contains("/image")
		        || requestUrl.toString().contains("/actuator")
		        || requestUrl.toString().contains("/pastpaper/user/list/paperType")
		        || requestUrl.toString().contains("/practicepaper/")
		        || requestUrl.toString().contains("/blog/")
		        || requestUrl.toString().contains("/notification/config/");
		
	}
	
}
