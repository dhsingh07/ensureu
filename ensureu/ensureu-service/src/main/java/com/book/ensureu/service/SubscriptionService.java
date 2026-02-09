package com.book.ensureu.service;

import java.util.List;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.PurchaseStatus;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.dto.PaperInfo;
import com.book.ensureu.dto.PaperPackageDto;
import com.book.ensureu.dto.PurchaseSubscriptionsDto;
import com.book.ensureu.dto.SubscribedDto;
import com.book.ensureu.model.PurchaseSubscriptions;
import com.book.ensureu.model.UserPass;
import com.book.ensureu.response.dto.Response;
import org.springframework.http.ResponseEntity;

public interface SubscriptionService {

	//public List<SubscriptionDto> getSubscriptionList(TestType testType);
	
	public ResponseEntity<Response> subscribe(String userName, SubscribedDto subscribedDto);

	public List<SubscribedDto> getSubscriptionListForUser(String userName);
	
	public List<SubscribedDto> getSubscriptionListForUser(String userName,PaperType paperType,TestType testType);
	
	public List<PaperInfo> getPaperInfoListForUser(String userName, Long createdDate, 
			Long validity, Boolean active, PaperType paperType, PaperCategory paperCategory, TestType testType);
	
	public void movePaperToSubscription() ;

	public PaperPackageDto getSubscriptionList(TestType testType);
	
	public  Response<UserPass> getUserPassList();

	public List<SubscribedDto> getAllSubscriptionListForUser(String userName);
	
	public List<SubscribedDto> getAllSubscriptionListForUser(String userName,PaperType paperType,TestType testType);
	
	public List<PaperInfo> getPaperInfoListForUser(String userName, List<String> paperIds, TestType testType);
	
	List<PaperInfo> getLastPaperInfoListForUser(String userName, Boolean active,
			PaperType paperType, PaperCategory paperCategory, TestType testType);
	
	List<PurchaseSubscriptionsDto> getPurchaseSubscription(String userId,PaperType paperType,TestType testType);
	
	public void savePurchaseSubscriptions(String userId,PurchaseSubscriptionsDto purchaseSubscriptionsDto,PurchaseStatus purchaseStatus);
	
}
