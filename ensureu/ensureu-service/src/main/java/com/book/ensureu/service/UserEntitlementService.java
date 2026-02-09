package com.book.ensureu.service;

import java.util.List;

import com.book.ensureu.constant.EntitlementType;
import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.model.UserEntitlement;
import com.book.ensureu.response.dto.Response;

public interface UserEntitlementService {

	public Response<List<UserEntitlement>> getUserEntitlement(String userId, Boolean active);
	
	public Response<List<UserEntitlement>> getUserEntitlement(String userId, PaperType paperType, Boolean active);
	
	public Response<List<UserEntitlement>> getUserEntitlement(String userId, PaperCategory paperCategory, Boolean active);
	
	public Response<List<UserEntitlement>> getUserEntitlement(String userId, PaperSubCategory paperSubCategory, Boolean active);

	public void updateUserEntitles();

	void createUserEntitlement(String userId, String testSeriesId, EntitlementType entitlementType);
}
