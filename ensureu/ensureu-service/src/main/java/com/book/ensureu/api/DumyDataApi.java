package com.book.ensureu.api;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.book.ensureu.constant.CounterEnum;
import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.SubscriptionType;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.common.model.PriceMetaData;
import com.book.ensureu.model.Subscription;
import com.book.ensureu.model.UserPass;
import com.book.ensureu.repository.PriceMetaDataRepository;
import com.book.ensureu.repository.SubscriptionRepository;
import com.book.ensureu.repository.UserPassRepository;
import com.book.ensureu.response.dto.Response;
import com.book.ensureu.service.CounterService;

@RestController
@RequestMapping("/add")
public class DumyDataApi {

	@Autowired
	PriceMetaDataRepository priceMetaDataRepository;

	@Autowired
	SubscriptionRepository subscriptionRepository;

	@Autowired
	UserPassRepository userPassRepository;

	@Autowired
	CounterService counterService;

	@CrossOrigin
	@PostMapping("/priceMetaData")
	Response<String> savePriceMetaData(@RequestBody List<PriceMetaData> list) {
		list.forEach(temp -> {
			if (temp.getId() == null) {
				Long count = counterService.increment(CounterEnum.PRICEMETADATA);
				temp.setId(count);
			}
		});
		priceMetaDataRepository.saveAll(list);

		return new Response<String>().setStatus(200).setMessage("saved successfully");

	}

	@CrossOrigin
	@PostMapping("/subscriptionData")
	Response<String> saveSubscription(@RequestBody List<Subscription> list) {
		list.forEach(temp -> {
			if (temp.getId() == null) {
				Long count = counterService.increment(CounterEnum.SUBSCRIPTION);
				temp.setId(count);
			}
		});
		subscriptionRepository.saveAll(list);

		return new Response<String>().setStatus(200).setMessage("Subscriptions saved successfully");

	}

	@CrossOrigin
	@PostMapping("/userPassData")
	Response<String> saveUserPass(@RequestBody List<UserPass> list) {
		list.forEach(temp -> {
			if (temp.getId() == null) {
				Long count = counterService.increment(CounterEnum.USERPASS);
				temp.setId(count);
			}
		});
		userPassRepository.saveAll(list);

		return new Response<String>().setStatus(200).setMessage("UserPass saved successfully");

	}

	@CrossOrigin
	@GetMapping("/newPriceMetaData")
    public void addPriceMetaDataUsingCode() {
		List<PriceMetaData> listOfPriceMetaData = new LinkedList<>();
		PaperSubCategory.getList().forEach((subCategoryObj)->{
			SubscriptionType.getAll().forEach((subType)->{
				Long count = counterService.increment(CounterEnum.PRICEMETADATA);
				PaperCategory paperCategory = PaperSubCategory.getParent(subCategoryObj);
				int num ;
				if(subType.getVal() < 1) {
				num = 1;
				}else {
				num =(int) (15*subType.getVal());	
				}
				PriceMetaData priceMetaData =
						new PriceMetaData(count,200.0, 10.0, 180.0, 
						9.0, 10.0,
						PaperCategory.getParent(paperCategory), paperCategory, subCategoryObj, subType,num);
				listOfPriceMetaData.add(priceMetaData);
			});
		});
		
		if(!listOfPriceMetaData.isEmpty())
			priceMetaDataRepository.saveAll(listOfPriceMetaData);
		
	}
	
	@CrossOrigin
	@GetMapping("/newPaidSubscription")
	public void addPaidSubscriptionDataUsingCode() {
		int totalCount =24; // can also be read through property file
		List<Subscription> listOfSubs = new LinkedList<>();
		
		Long oneMonthMillis = 30*24*60*60*1000l;
		String description = "need to add";
		List<PaperSubCategory> listOfSubCat = PaperSubCategory.getList();
		for (PaperSubCategory subCategoryObj : listOfSubCat) {
			Long timeInMillis = System.currentTimeMillis();
			PaperCategory paperCategory = PaperSubCategory.getParent(subCategoryObj); 
			for(int i =0; i<totalCount; i++) {
				
				Long count = counterService.increment(CounterEnum.SUBSCRIPTION);
				Long validity = timeInMillis+oneMonthMillis;
                Subscription subObj =null;// new Subscription(count, count, validity, 0, null,
						//PaperCategory.getParent(paperCategory), TestType.PAID, paperCategory, subCategoryObj,
						//description, timeInMillis, null, Subscription.SubscriptionState.ACTIVE);
				listOfSubs.add(subObj);
				timeInMillis = validity;
				
			}
		}
		
	
		if(!listOfSubs.isEmpty())
		subscriptionRepository.saveAll(listOfSubs);	
		
	}
	

	@CrossOrigin
	@GetMapping("/newFreeSubscription")
	public void addFreeSubscriptionDataUsingCode() {
		int totalCount =24; // can also be read through property file
		List<Subscription> listOfSubs = new LinkedList<>();
		Long timeInMillis = System.currentTimeMillis();
		Long oneMonthMillis = 30*24*60*60*1000l;
		String description = "Free subscription des.";
		List<PaperSubCategory> listOfSubCat = PaperSubCategory.getList();
		for (PaperSubCategory subCategoryObj : listOfSubCat) {
			PaperCategory paperCategory = PaperSubCategory.getParent(subCategoryObj); 
			for(int i =0; i<totalCount; i++) {
				
				Long count = counterService.increment(CounterEnum.SUBSCRIPTION);
				Long validity = timeInMillis+oneMonthMillis;
                Subscription subObj = null; //new Subscription(count, count, validity, 0, null,
						//PaperCategory.getParent(paperCategory), TestType.FREE, paperCategory, subCategoryObj,
						//description, timeInMillis, null, Subscription.SubscriptionState.ACTIVE);
				listOfSubs.add(subObj);
				timeInMillis = validity;
				
			}
		}
		
	
		if(!listOfSubs.isEmpty())
		subscriptionRepository.saveAll(listOfSubs);	
		
	}
	
}
