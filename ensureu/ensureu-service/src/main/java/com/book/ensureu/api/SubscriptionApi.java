package com.book.ensureu.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.PurchaseStatus;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.dto.PaperInfo;
import com.book.ensureu.dto.PaperPackageDto;
import com.book.ensureu.dto.PurchaseSubscriptionsDto;
import com.book.ensureu.dto.SubscribedDto;
import com.book.ensureu.model.JwtUser;
import com.book.ensureu.model.UserPass;
import com.book.ensureu.response.dto.Response;
import com.book.ensureu.security.UserPrincipalService;
import com.book.ensureu.service.SubscriptionService;
import com.book.ensureu.service.UserPassService;

@RestController
@RequestMapping("/subscription")
public class SubscriptionApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionApi.class.getName());

	@Autowired
	private SubscriptionService subscriptionService;

	@Autowired
	private UserPassService userPassService;

	@Autowired
	private UserPrincipalService userPrincipal;

	@CrossOrigin
	@RequestMapping(value = "/getAllType/{testType}", method = RequestMethod.GET)
	public Response<PaperPackageDto> getSubscriptionList(@PathVariable(value = "testType") String testType) {
		LOGGER.info(" inside getSubscriptionList method tesType ");
		Response<PaperPackageDto> response = new Response<>();

		try {
			PaperPackageDto paperPackageDto = subscriptionService.getSubscriptionList(TestType.valueOf(testType));
			response.setBody(paperPackageDto).setStatus(200);
		} catch (Exception e) {
			LOGGER.error("Exception occurred while getting  subscription for getAllType" + e.getMessage());
			response.setStatus(500).setMessage("Exception Occurred");
		}
		return response;
	}

	@CrossOrigin
	@RequestMapping(value = "/subscribe", method = RequestMethod.POST)
	public Response<String> subscribe(@RequestBody SubscribedDto subscribedDto) {
		JwtUser jwtUser = null;
		Response<String> response = new Response<>();
		try {
			jwtUser = userPrincipal.getCurrentUserDetails();
			String userId = jwtUser.getUsername();
			LOGGER.info(" inside subscribe method , userId: {}", userId);
			subscriptionService.subscribe(userId, subscribedDto);
			response.setStatus(200).setMessage("ok");

		} catch (Exception e) {
			LOGGER.error("Exception occurred while subscription {}", e.getMessage());
			response.setStatus(500).setMessage("Exception Occurred");
		}
		return response;
	}

	@CrossOrigin
	@RequestMapping(value = "/getUserSubscription/paperType/{paperType}", method = RequestMethod.GET)
	public Response<List<SubscribedDto>> getSubscriptionListForUser(
			@PathVariable(value = "paperType", required = true) String paperType,
			@RequestParam(value = "testType", required = false) String testType) {

		JwtUser jwtUser = userPrincipal.getCurrentUserDetails();
		String userId = jwtUser.getUsername();
		LOGGER.info("Inside getSubscriptionListForUser method userId: {}", userId);
		Response<List<SubscribedDto>> response = new Response<>();
		try {
			List<SubscribedDto> listOfDto = null;
			if (testType != null) {
				listOfDto = subscriptionService.getSubscriptionListForUser(userId, PaperType.valueOf(paperType),
						TestType.valueOf(testType));
			} else {
				listOfDto = subscriptionService.getSubscriptionListForUser(userId, PaperType.valueOf(paperType),
						TestType.PAID);
			}

			response.setBody(listOfDto).setStatus(200);
		} catch (Exception e) {
			LOGGER.error("Exception occured while getting userSubscription {}", e.getMessage());
			response.setStatus(500);
			response.setMessage(e.getMessage());
		}
		return response;
	}

	
	  @CrossOrigin
	  
	  @PostMapping("/getUserPaperSubscription") public Response<List<PaperInfo>>
	  getPaperInfoSubscription(@RequestHeader("createdDate") Long createdDate,
	  
	  @RequestHeader("validity") Long validity,
	  
	  @RequestHeader("active") boolean active,
	  
	  @RequestHeader("paperType") String paperType,
	  
	  @RequestHeader("paperCategory") String paperCategory,
	  
	  @RequestHeader("testType") String testType) { Response<List<PaperInfo>>
	  response = new Response<>(); try { JwtUser jwtUser =
	  userPrincipal.getCurrentUserDetails(); String userId = jwtUser.getUsername();
	  LOGGER.info("Inside getPaperInfoSubscription method userId: {}", userId);
	  
	  List<PaperInfo> listOfPaperInfo =
	  subscriptionService.getPaperInfoListForUser(userId, createdDate, validity,
	  active, PaperType.valueOf(paperType), PaperCategory.valueOf(paperCategory), TestType.valueOf(testType));
	  response.setBody(listOfPaperInfo).setStatus(200); } catch (Exception e) {
	  LOGGER.error("Exception occurred while getting userPaperInfoSubscription {}",
	  e.getMessage()); response.setStatus(500);
	  response.setMessage(e.getMessage()); }
	  
	  return response; }
	 

	// @Scheduled(cron = "0 0 0 * * *")
	@RequestMapping(value = "/movePapers", method = RequestMethod.GET)
	public void movePaperToSubscription() {
		LOGGER.info(" inside cron movePaperToSubscription method: {}", 1);
		subscriptionService.movePaperToSubscription();
	}

	@CrossOrigin
	@RequestMapping(value = "/getAllPass", method = RequestMethod.GET)
	public Response<List<UserPass>> getPassList() {
		LOGGER.info(" inside getPassList method {}", 2);
		List<UserPass> list = userPassService.getAllActivePass();

		return new Response<List<UserPass>>().setStatus(200).setBody(list).setMessage("fetch successfully ");

	}

	@CrossOrigin
	@RequestMapping(value = "/getAllUserSubscription/paperType/{paperType}", method = RequestMethod.GET)
	public Response<List<SubscribedDto>> getAllSubscriptionListForUser(
			@PathVariable(value = "paperType", required = true) String paperType,
			@RequestParam(value = "testType", required = false) String testType) {

		JwtUser jwtUser = userPrincipal.getCurrentUserDetails();
		String userId = jwtUser.getUsername();
		LOGGER.info("Inside getSubscriptionListForUser method userId: {}", userId);

		Response<List<SubscribedDto>> response = new Response<>();
		try {
			List<SubscribedDto> listOfDto = null;
			if (testType != null) {
				listOfDto = subscriptionService.getAllSubscriptionListForUser(userId, PaperType.valueOf(paperType),
						TestType.valueOf(testType));
			} else {
				listOfDto = subscriptionService.getAllSubscriptionListForUser(userId, PaperType.valueOf(paperType),
						TestType.PAID);
			}

			response.setBody(listOfDto).setStatus(200).setMessage("Success");
		} catch (Exception e) {
			LOGGER.error("Exception occurred while getting userSubscription {}", e.getMessage());
			response.setStatus(500);
			response.setMessage(e.getMessage());
		}

		return response;
	}

	@CrossOrigin
	@RequestMapping(value = "/purchaseSubscriptions", method = RequestMethod.POST)
	public Response<String> savePurchaseSubscriptionsForUser(
			@RequestBody PurchaseSubscriptionsDto purchaseSubscriptionsDto) {

		JwtUser jwtUser = userPrincipal.getCurrentUserDetails();
		String userId = jwtUser.getUsername();
		LOGGER.info("Inside savePurchaseSubscriptionsForUser method userId: {}, dto: {}", userId, purchaseSubscriptionsDto);

		Response<String> response = new Response<>();
		try {
			if (purchaseSubscriptionsDto != null) {
				// Convert PurchaseSubscriptionsDto to SubscribedDto and call subscribe
				// This will create PurchaseSubscriptions AND UserEntitlement
				SubscribedDto subscribedDto = new SubscribedDto();

				// Get subscription ID - from id field or from listOfSubscriptionIds
				Long subscriptionId = purchaseSubscriptionsDto.getId();
				if (subscriptionId == null && purchaseSubscriptionsDto.getListOfSubscriptionIds() != null
						&& !purchaseSubscriptionsDto.getListOfSubscriptionIds().isEmpty()) {
					subscriptionId = purchaseSubscriptionsDto.getListOfSubscriptionIds().get(0);
				}
				subscribedDto.setId(subscriptionId);

				subscribedDto.setPaperType(purchaseSubscriptionsDto.getPaperType());
				subscribedDto.setPaperCategory(purchaseSubscriptionsDto.getPaperCategory());
				subscribedDto.setPaperSubCategory(purchaseSubscriptionsDto.getPaperSubCategory());
				subscribedDto.setTestType(purchaseSubscriptionsDto.getTestType() != null
						? purchaseSubscriptionsDto.getTestType() : TestType.PAID);
				subscribedDto.setSubscriptionType(purchaseSubscriptionsDto.getSubscriptionType());
				subscribedDto.setValidity(purchaseSubscriptionsDto.getValidity());
				subscribedDto.setListOfSubscriptionIds(purchaseSubscriptionsDto.getListOfSubscriptionIds());

				LOGGER.info("Calling subscribe for PAID subscription, userId: {}, subscriptionId: {}, paperSubCategory: {}, subscriptionType: {}",
						userId, subscriptionId, subscribedDto.getPaperSubCategory(), subscribedDto.getSubscriptionType());

				// Call subscribe which creates PurchaseSubscriptions + UserEntitlement
				subscriptionService.subscribe(userId, subscribedDto);

				response.setBody("Ok").setStatus(200).setMessage("Success");
			} else {
				LOGGER.info("subscribed data can not be null");
				response.setBody("Error").setStatus(400).setMessage("Request body cannot be null");
			}
		} catch (Exception e) {
			LOGGER.error("Exception occurred while save purchaseSubscriptions {}", e.getMessage());
			response.setStatus(500);
			response.setMessage(e.getMessage());
		}

		return response;
	}

	@CrossOrigin
	@RequestMapping(value = "/getSubscriptions/paperType/{paperType}", method = RequestMethod.GET)
	public Response<List<PurchaseSubscriptionsDto>> getSubscriptionsListPurchaseByUser(
			@PathVariable(value = "paperType", required = true) String paperType,
			@RequestParam(value = "testType", required = false) String testType) {

		JwtUser jwtUser = userPrincipal.getCurrentUserDetails();
		String userId = jwtUser.getUsername();
		LOGGER.info("Inside getSubscriptionsListPurchaseByUser method userId: {}", userId);

		Response<List<PurchaseSubscriptionsDto>> response = new Response<>();
		try {
			List<PurchaseSubscriptionsDto> listOfDto = null;
			if (testType != null) {
				listOfDto = subscriptionService.getPurchaseSubscription(userId, PaperType.valueOf(paperType),
						TestType.valueOf(testType));
			} else {
				listOfDto = subscriptionService.getPurchaseSubscription(userId, PaperType.valueOf(paperType),
						TestType.PAID);
			}

			response.setBody(listOfDto).setStatus(200).setMessage("Sucess");
		} catch (Exception e) {
			LOGGER.error("Exception occurred while getting user purchase subscriptions {}", e.getMessage());
			response.setStatus(500);
			response.setMessage(e.getMessage());
		}

		return response;
	}

	@CrossOrigin
	@PostMapping("/getPaperInfo")
	public Response<List<PaperInfo>> getPaperInfoList(@RequestHeader("testType") String testType,

			@RequestBody List<String> paperIds) {
		Response<List<PaperInfo>> response = new Response<>();
		try {
			JwtUser jwtUser = userPrincipal.getCurrentUserDetails();
			String userId = jwtUser.getUsername();
			List<PaperInfo> listOfPaperInfo = subscriptionService.getPaperInfoListForUser(userId, paperIds,
					TestType.valueOf(testType));
			response.setBody(listOfPaperInfo).setStatus(200);
		} catch (Exception e) {
			LOGGER.error("Exception occurred while getting userPaperInfoSubscription {}", e.getMessage());
			response.setStatus(500);
			response.setMessage(e.getMessage());
		}

		return response;
	}

}
