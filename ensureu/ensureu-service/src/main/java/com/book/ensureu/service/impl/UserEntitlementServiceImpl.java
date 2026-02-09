package com.book.ensureu.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.book.ensureu.constant.*;
import com.book.ensureu.service.CounterService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.book.ensureu.model.UserEntitlement;
import com.book.ensureu.repository.SubscriptionRepository;
import com.book.ensureu.repository.UserEntitlementRepository;
import com.book.ensureu.response.dto.Response;
import com.book.ensureu.service.UserEntitlementService;
import com.book.ensureu.service.impl.helper.SubscriptionServiceImplHelper;

@Service
@Slf4j
public class UserEntitlementServiceImpl implements UserEntitlementService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserEntitlementServiceImpl.class.getName());

	@Autowired
	private UserEntitlementRepository userEntitlementRepository;
	
	@Autowired
	private SubscriptionRepository subscriptionRepository;

	@Autowired
	private CounterService counterService;

	@Value("${testSeries.validity:86400000}")
	private int testSeriesValidity;

	@Override
	public Response<List<UserEntitlement>> getUserEntitlement(String userId, Boolean active) {
		LOGGER.info(" in service method getUserEntitlement for userId: " + userId);
		Response<List<UserEntitlement>> response = new Response<>();
		try {
			// Query both SUBSCRIPTION and FREE_SUBSCRIPTION
			List<EntitlementType> subscriptionTypes = Arrays.asList(EntitlementType.SUBSCRIPTION, EntitlementType.FREE_SUBSCRIPTION);
			List<UserEntitlement> list = userEntitlementRepository.findByUserIdAndActiveAndEntitlementTypeIn(userId, active, subscriptionTypes);
			response.setBody(list).setStatus(200).setMessage("ok");
		} catch (Exception e) {
			LOGGER.error(" Exception occured while fetching entitlement details for userId: " + userId + " msg:"
					+ e.getMessage());
			response.setStatus(500).setMessage("error occured ");
		}
		return response;
	}

	@Override
	public Response<List<UserEntitlement>> getUserEntitlement(String userId, PaperType paperType, Boolean active) {

		LOGGER.info(" in service method getUserEntitlement for userId & searchType :{} ", userId+paperType);
		Response<List<UserEntitlement>> response = new Response<>();
		try {
			// Query both SUBSCRIPTION and FREE_SUBSCRIPTION
			List<EntitlementType> subscriptionTypes = Arrays.asList(EntitlementType.SUBSCRIPTION, EntitlementType.FREE_SUBSCRIPTION);
			List<UserEntitlement> list = userEntitlementRepository.findByUserIdAndActiveAndEntitlementTypeIn(userId, active, subscriptionTypes);
			
			List<Long> listOfIds = new LinkedList<>();
			list.forEach((userObj)->{
				listOfIds.add(userObj.getSubscriptionId());
			});
			List<Long> listOfDesiredSubIds = subscriptionRepository.findByIdInAndPaperType(listOfIds , paperType);
			List<UserEntitlement> listOfUserEntitlement = new LinkedList<>();
			list.forEach((userObj)->{
				if(listOfDesiredSubIds.contains(userObj.getSubscriptionId())) {
					listOfUserEntitlement.add(userObj);
				}
			});
			response.setBody(list).setStatus(200).setMessage("ok");
		} catch (Exception e) {
			LOGGER.error(" Exception occured while fetching entitlement details for userId: " + userId + " msg:"
					+ e.getMessage());
			response.setStatus(500).setMessage("error occured ");
		}
		return response;
	}

	@Override
	public Response<List<UserEntitlement>> getUserEntitlement(String userId, PaperCategory paperCategory,
			Boolean active) {
		LOGGER.info(" in service method getUserEntitlement for userId & searchType :{} ", userId+paperCategory);
		Response<List<UserEntitlement>> response = new Response<>();
		try {
			// Query both SUBSCRIPTION and FREE_SUBSCRIPTION
			List<EntitlementType> subscriptionTypes = Arrays.asList(EntitlementType.SUBSCRIPTION, EntitlementType.FREE_SUBSCRIPTION);
			List<UserEntitlement> list = userEntitlementRepository.findByUserIdAndActiveAndEntitlementTypeIn(userId, active, subscriptionTypes);
			response.setBody(list).setStatus(200).setMessage("ok");
		} catch (Exception e) {
			LOGGER.error(" Exception occured while fetching entitlement details for userId: " + userId + " msg:"
					+ e.getMessage());
			response.setStatus(500).setMessage("error occured ");
		}
		return response;
	}

	@Override
	public Response<List<UserEntitlement>> getUserEntitlement(String userId, PaperSubCategory paperSubCategory,
			Boolean active) {
		LOGGER.info(" in service method getUserEntitlement for userId & searchType : {}", userId+paperSubCategory);
		Response<List<UserEntitlement>> response = new Response<>();
		try {
			// Query both SUBSCRIPTION and FREE_SUBSCRIPTION
			List<EntitlementType> subscriptionTypes = Arrays.asList(EntitlementType.SUBSCRIPTION, EntitlementType.FREE_SUBSCRIPTION);
			List<UserEntitlement> list = userEntitlementRepository.findByUserIdAndActiveAndEntitlementTypeIn(userId, active, subscriptionTypes);
			response.setBody(list).setStatus(200).setMessage("ok");
		} catch (Exception e) {
			LOGGER.error(" Exception occured while fetching entitlement details for userId: " + userId + " msg:"
					+ e.getMessage());
			response.setStatus(500).setMessage("error occured ");
		}
		return response;
	}
	
	
	
	@Override
	public void updateUserEntitles() {
		LOGGER.info(" inside method updateUserEntitles ");
		// no need now as based on subscription type we are making entry in userEntitlement 
		
		/*Long timeInMills = System.currentTimeMillis();
		Long timeToAddSub = 24 * 60 * 60 * 1000l;
		List<UserEntitlement> listOfUserEntitlement = userEntitlementRepository.findAllByActiveAndByValidityIn(true,
				timeInMills, timeInMills + timeToAddSub);
		List<UserEntitlement> listOfUserEntitleToAdd = new LinkedList<>();
		listOfUserEntitlement.forEach((userEntitleObj)->{
			int month = (int) userEntitleObj.getSubscriptionType().getVal();
			if(userEntitleObj.getSubscriptionType().getVal() > SubscriptionType.MONTHLY.getVal()) {
				if((userEntitleObj.getValidity() - userEntitleObj.getCreatedDate() < (timeToAddSub*30*month))) {
					UserEntitlement newEntitlement = subscriptionServiceImplHelper.
							createUserEntitle(userEntitleObj.getUserId(), null, null, userEntitleObj,false);
					listOfUserEntitleToAdd.add(newEntitlement);
				}
			}
		});
*/
		
		
	}

	public void createUserEntitlement(UserEntitlement userEntitlement){
		log.debug("[createUserEntitlement][UserEntitlementServiceImpl] userEntitlement {}",userEntitlement);
		userEntitlementRepository.save(userEntitlement);
	}


	@Override
	public void createUserEntitlement(String userId, String testSeriesId, EntitlementType entitlementType) {
		log.debug("[createUserEntitlement][UserEntitlementServiceImpl] userId {}, testSeriesId {}, entitlementType {}",userId,testSeriesId,entitlementType);
		Date date = new Date();
        long validity = date.getTime() + testSeriesValidity* ApplicationConstant.MonthMillis;
        long id = counterService.increment(CounterEnum.USERENTITLEMENT);
		UserEntitlement userEntitlement = UserEntitlement.builder()
				.id(id)
				.userId(userId)
				.testSeriesId(testSeriesId)
				.entitlementType(entitlementType)
				.crDate(date)
				.createdDate(date.getTime())
				.validity(validity)
				.active(true)
				.build();
		createUserEntitlement(userEntitlement);
	}
}
