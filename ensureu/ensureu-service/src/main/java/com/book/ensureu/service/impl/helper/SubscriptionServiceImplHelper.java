package com.book.ensureu.service.impl.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.book.ensureu.annotation.PurchaseSubscription;
import com.book.ensureu.constant.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.book.ensureu.dto.PaperCategoryDto;
import com.book.ensureu.dto.PaperInfo;
import com.book.ensureu.dto.PaperSubCatogoryDto;
import com.book.ensureu.common.dto.PriceMetaDataDto;
import com.book.ensureu.dto.SubscribedDto;
import com.book.ensureu.dto.SubscriptionDto;
import com.book.ensureu.dto.UserPassSubscriptionDto;
import com.book.ensureu.model.PaidPaperCollection;
import com.book.ensureu.common.model.PriceMetaData;
import com.book.ensureu.model.Subscription;
import com.book.ensureu.model.UserEntitlement;
import com.book.ensureu.repository.PaidPaperCollectionRepository;
import com.book.ensureu.repository.SubscriptionRepository;
import com.book.ensureu.repository.UserEntitlementRepository;
import com.book.ensureu.service.CounterService;
import com.book.ensureu.service.PaperInfoDataService;

@Component
public class SubscriptionServiceImplHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionServiceImplHelper.class.getName());

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserEntitlementRepository entitlementRepository;

    @Autowired
    private CounterService couterService;

    @Autowired
    private PaidPaperCollectionRepository paidPaperCollectionRepository;
    
    @Autowired
    private PaperInfoDataService paperInfoDataService;
    
    @Value("${paper.days.validity:1}")
    private Integer days;

    @Value("${day.milliseconds:86400000}")
    private Long milliseconds;


    /*
     * public void createUserEntitilementAndSave(Long userId, SubscribedDto
     * subscribedDto, Long paperId) { UserEntitlement entitlement = new
     * UserEntitlement();
     * entitlement.setId(couterService.increment(CounterEnum.USERENTITLEMENT));
     * entitlement.setUserId(userId);
     * entitlement.setSubscriptionId(subscribedDto.getId());
     * entitlement.setSubscriptionType(subscribedDto.getSubscriptionType()); //
     * entitlement.setValidity(subscribedDto.getValidity());
     * entitlement.setPaperId(paperId);
     * entitlement.setCreatedDate(System.currentTimeMillis());
     * entitlement.setEntitlementType(ApplicationConstant.SUBSCRIPTION_PASS);
     *
     * entitlementRepository.save(entitlement); }
     */
    private List<SubscriptionDto> convertToSubsDto(List<Subscription> listOfSubs) {

        List<SubscriptionDto> listOfSubsDto = new LinkedList<>();
        if (listOfSubs != null && !listOfSubs.isEmpty()) {
            listOfSubs.forEach((subsObj) -> {
                SubscriptionDto dto = new SubscriptionDto();
                dto.setDescription(subsObj.getDescription());
                dto.setId(subsObj.getId());
                dto.setTestType(subsObj.getTestType().toString());
                dto.setPaperType(subsObj.getPaperType().toString());
                dto.setPaperCategory(subsObj.getPaperCategory().toString());
                dto.setPaperSubCategory(subsObj.getPaperSubCategory().toString());
                dto.setPaperInfoList(subsObj.getListOfPaperInfo());
                dto.setValidity(subsObj.getValidity());
                listOfSubsDto.add(dto);
            });
        }
        return listOfSubsDto;
    }

    /**
     * @param subscription
     * @param listOfPrice
     * @return
     */
    public SubscriptionDto convertToSubscriptionDto(Subscription subscription, List<PriceMetaData> listOfPrice) {
        SubscriptionDto dto = new SubscriptionDto();
        dto.setId(subscription.getId());
        dto.setPaperType(subscription.getPaperType().toString());
        dto.setPaperCategory(subscription.getPaperCategory().toString());
        dto.setPaperSubCategory(subscription.getPaperSubCategory().toString());
        dto.setTestType(subscription.getTestType().toString());
        dto.setDescription(subscription.getDescription());
        dto.setValidity(subscription.getValidity());
        dto.setListOfSubscriptionType(SubscriptionType.getAll());
        dto.setMapOfSubTypeVsPrice(createMapOfSubTypeVsPrice(subscription, listOfPrice));
        dto.setPaperIds(subscription.getPaperIds());
        dto.setPaperInfoList(subscription.getListOfPaperInfo());
        List<Long> listOfSubscriptionId = new LinkedList<>();
        listOfSubscriptionId.add(subscription.getId());
        dto.setListOfSubscriptionIds(listOfSubscriptionId);
        return dto;
    }

    /**
     * @param subscription
     * @param mapOfUserEntitlement
     * @return
     */
    public SubscribedDto convertToSubscribedDto(Subscription subscription,
                                                Map<Long, UserEntitlement> mapOfUserEntitlement) {
        SubscribedDto dto = new SubscribedDto();
        dto.setId(subscription.getId());
        dto.setPaperType(subscription.getPaperType());
        dto.setPaperCategory(subscription.getPaperCategory());
        dto.setPaperSubCategory(subscription.getPaperSubCategory());
        dto.setTestType(subscription.getTestType());
        dto.setDescription(subscription.getDescription());
        dto.setValidity(subscription.getValidity());
        dto.setPaperIds(subscription.getPaperIds());

        // Get paper info from subscription, or fetch from PaidPaperCollection as fallback
        List<PaperInfo> paperInfoList = subscription.getListOfPaperInfo();
        if ((paperInfoList == null || paperInfoList.isEmpty()) && subscription.getPaperSubCategory() != null) {
            // Fallback: fetch papers from PaidPaperCollection by paperSubCategory
            LOGGER.info("No papers in subscription {}, fetching from PaidPaperCollection for subCategory: {}",
                    subscription.getId(), subscription.getPaperSubCategory());
            paperInfoList = fetchPaperInfoFromCollection(subscription.getPaperSubCategory(), subscription.getTestType());
        }
        dto.setListOfPaperInfo(paperInfoList);

        if (subscription.getTestType() != null && subscription.getTestType().equals(TestType.PAID)) {
            UserEntitlement userEntitlement = mapOfUserEntitlement.get(subscription.getId());
            if (userEntitlement != null) {
                dto.setSubscriptionType(userEntitlement.getSubscriptionType());
                addValidityAndCreatedDateInPaperInfoList(dto.getListOfPaperInfo(), userEntitlement);
            }
        } else {
            addValidityAndCreatedDateInPaperInfoList(dto.getListOfPaperInfo(),
                    subscription);
        }


        return dto;
    }

    /**
     * Fetch paper info from PaidPaperCollection by paperSubCategory
     */
    private List<PaperInfo> fetchPaperInfoFromCollection(PaperSubCategory paperSubCategory, TestType testType) {
        List<PaperInfo> paperInfoList = new ArrayList<>();
        try {
            List<PaidPaperCollection> papers = paidPaperCollectionRepository
                    .findByPaperSubCategoryAndTestType(paperSubCategory, testType);
            if (papers != null && !papers.isEmpty()) {
                for (PaidPaperCollection paper : papers) {
                    PaperInfo info = PaperInfo.builder()
                            .id(paper.getId())
                            .paperName(paper.getPaperName())
                            .build();
                    paperInfoList.add(info);
                }
                LOGGER.info("Fetched {} papers from PaidPaperCollection for subCategory: {}",
                        paperInfoList.size(), paperSubCategory);
            }
        } catch (Exception e) {
            LOGGER.error("Error fetching papers from PaidPaperCollection: {}", e.getMessage());
        }
        return paperInfoList;
    }

    private <T> void addValidityAndCreatedDateInPaperInfoList(List<PaperInfo> listOfPaperInfo,
                                                              T t) {

        if (listOfPaperInfo != null && !listOfPaperInfo.isEmpty()) {


            if (t != null && t instanceof UserEntitlement) {
                UserEntitlement userEntitlement = (UserEntitlement) t;
                listOfPaperInfo.forEach((paperInfoObj) -> {
                    paperInfoObj.setValidity(userEntitlement.getValidity());
                    paperInfoObj.setCreatedDate(userEntitlement.getCreatedDate());
                });
            } else if (t != null && t instanceof Subscription) {
                Subscription subscription = (Subscription) t;
                listOfPaperInfo.forEach((paperInfoObj) -> {
                    paperInfoObj.setValidity(subscription.getValidity());
                    paperInfoObj.setCreatedDate(subscription.getCreatedDate());
                });

            }
        }
    }

    public Map<SubscriptionType, PriceMetaDataDto> createMapOfSubTypeVsPrice(Subscription subscription,
                                                                             List<PriceMetaData> listOfPrice) {
        if (null == listOfPrice || listOfPrice.isEmpty())
            return null;

        Map<SubscriptionType, PriceMetaDataDto> map = new HashMap<>();

        for (SubscriptionType typeObj : SubscriptionType.getAll()) {
            for (PriceMetaData priceMetaData : listOfPrice) {
                if (subscription.getPaperType().equals(priceMetaData.getPaperType())
                        && subscription.getPaperCategory().equals(priceMetaData.getPaperCategory())
                        && subscription.getPaperSubCategory().equals(priceMetaData.getPaperSubCategory())
                        && typeObj.equals(priceMetaData.getSubscriptionType())) {
                    map.put(typeObj, convertToPriceMetaDataDto(priceMetaData));

                }
            }
        }

        SubscriptionType.getAll().forEach((typeObj) -> {
            for (PriceMetaData priceMetaData : listOfPrice) {
                if (subscription.getPaperType().equals(priceMetaData.getPaperType())
                        && subscription.getPaperCategory().equals(priceMetaData.getPaperCategory())
                        && subscription.getPaperSubCategory().equals(priceMetaData.getPaperSubCategory())
                        && typeObj.equals(priceMetaData.getSubscriptionType())) {
                    map.put(typeObj, convertToPriceMetaDataDto(priceMetaData));

                }
            }

        });

        return map;
    }

    /**
     * @param priceMetaData
     * @return
     */
    public PriceMetaDataDto convertToPriceMetaDataDto(PriceMetaData priceMetaData) {
        PriceMetaDataDto metaDataDto;
        metaDataDto = new PriceMetaDataDto();
        metaDataDto.setId(priceMetaData.getId());
        metaDataDto.setPrice(priceMetaData.getPrice());
        metaDataDto.setPricePerPaper(priceMetaData.getPricePerPaper());
        metaDataDto.setDiscountedPrice(priceMetaData.getDiscountedPrice());
        metaDataDto.setDiscountedPricePerPaper(priceMetaData.getDiscountedPricePerPaper());
        metaDataDto.setDiscountPercentage(priceMetaData.getDiscountPercentage());
        return metaDataDto;
    }

    /**
     * @param listOfPriceMetaData
     */
    public void addDumyData(List<PriceMetaData> listOfPriceMetaData) {
        if (null != listOfPriceMetaData && listOfPriceMetaData.isEmpty()) {
            PriceMetaData priceMetaData = new PriceMetaData(1l, 200.0, 10.0, 180.0, 8.0, 20.0, PaperType.SSC,
                    PaperCategory.SSC_CHSL, PaperSubCategory.SSC_CHSL_TIER1, SubscriptionType.DAY);
            listOfPriceMetaData.add(priceMetaData);
            priceMetaData = new PriceMetaData(1l, 200.0, 10.0, 180.0, 8.0, 20.0, PaperType.SSC, PaperCategory.SSC_CGL,
                    PaperSubCategory.SSC_CGL_TIER1, SubscriptionType.DAY);
            listOfPriceMetaData.add(priceMetaData);
            priceMetaData = new PriceMetaData(1l, 200.0, 10.0, 180.0, 8.0, 20.0, PaperType.SSC, PaperCategory.SSC_CHSL,
                    PaperSubCategory.SSC_CHSL_TIER2, SubscriptionType.DAY);
            listOfPriceMetaData.add(priceMetaData);
        }
    }

    /**
     * @param testType
     * @return
     */
    public List<Subscription> getSubscriptionByTestType(TestType testType) {
        List<PaperSubCategory> listOfSubCatogory = PaperSubCategory.getList();
        List<PaperSubCategory> listOfEnableSubCategory=new ArrayList<PaperSubCategory>();
        List<Subscription> listOfSubs = null;
        
        // enable and disable the subscription...
        //priority setting for subscription
        Map<PaperSubCategory,List<String>> paperInfoAvalable=paperInfoDataService.getPaperInfoByTestTypeAndPaperSubCategoryAndEnable(testType, listOfSubCatogory, true);
        if(paperInfoAvalable!=null){
        	 listOfEnableSubCategory=new ArrayList(paperInfoAvalable.keySet());
        	  listOfSubs = subscriptionRepository
                      .findByPaperSubCategoryInAndTestTypeOrder(listOfEnableSubCategory, testType, System.currentTimeMillis());
        }else {
        	LOGGER.info("Paper is not enalbed for "+ testType);;	
        }
      
        LOGGER.info("listOfSubscription " + ((listOfSubs != null) ? listOfSubs.size() : " no data found"));
        return listOfSubs;
    }

    public void createUserEntitlementAndSave(String userId, UserPassSubscriptionDto subscribedDto) {

        Long createdDate = System.currentTimeMillis();
        Double daysToAdd = subscribedDto.getSubscriptionType().getVal();
        Long milliSecToAdd = 1l;
        milliSecToAdd = (long) ((milliSecToAdd * 30 * milliseconds) * daysToAdd + createdDate);
        UserEntitlement entitlement = new UserEntitlement();
        entitlement.setId(couterService.increment(CounterEnum.USERENTITLEMENT));
        entitlement.setUserId(userId);
        entitlement.setSubscriptionId(subscribedDto.getId());
        entitlement.setSubscriptionType(subscribedDto.getSubscriptionType());
        entitlement.setValidity(milliSecToAdd);
        entitlement.setCreatedDate(createdDate);
        entitlement.setEntitlementType(EntitlementType.USER_PASS);
        entitlementRepository.save(entitlement);
    }

    @PurchaseSubscription
    public void createUserEntitlementAndSave(String userId, SubscribedDto subscribedDto, String paperId,
                                             UserEntitlement userLastEntitlement) {
        // create entitle based on subscription type & paperId
        List<UserEntitlement> listOfUserEntitle = new LinkedList<>();
        if (paperId != null) {
            createUserEntitlementForPaper(userId, subscribedDto, paperId, userLastEntitlement, listOfUserEntitle);
        } else {
            createUserEntitlementForSubscription(userId, subscribedDto, paperId, userLastEntitlement, listOfUserEntitle);
        }
        entitlementRepository.saveAll(listOfUserEntitle);

    }

    private void createUserEntitlementForPaper(String userId, SubscribedDto subscribedDto, String paperId,
                                               UserEntitlement oldUserEntitlement, List<UserEntitlement> listOfUserEntitle) {
        // TODO
        UserEntitlement entitlement = createUserEntitle(userId, subscribedDto, paperId, oldUserEntitlement,
                true, subscribedDto.getId());
        listOfUserEntitle.add(entitlement);
    }

    /**
     * @param userId
     * @param subscribedDto
     * @param paperId
     * @param oldUserEntitlement
     * @param listOfUserEntitle
     */
    private void createUserEntitlementForSubscription(String userId, SubscribedDto subscribedDto, String paperId,
                                                      UserEntitlement oldUserEntitlement, List<UserEntitlement> listOfUserEntitle) {
        // Handle FREE subscriptions - they don't have a subscriptionType
        if (subscribedDto.getTestType() != null && TestType.FREE.equals(subscribedDto.getTestType())) {
            // For FREE subscriptions, create a single entitlement for the subscription
            UserEntitlement entitlement = createUserEntitleForFree(userId, subscribedDto, oldUserEntitlement);
            listOfUserEntitle.add(entitlement);
            return;
        }

        // For PAID subscriptions, use the subscriptionType to determine entitlement count
        if (subscribedDto.getSubscriptionType() == null) {
            LOGGER.error("SubscriptionType is null for PAID subscription, userId: {}", userId);
            return;
        }

        List<Long> listOfSubscriptionIds = subscribedDto.getListOfSubscriptionIds();
        if (listOfSubscriptionIds == null || listOfSubscriptionIds.isEmpty()) {
            LOGGER.error("listOfSubscriptionIds is empty for PAID subscription, userId: {}", userId);
            return;
        }

        // For PAID subscriptions, create a single UserEntitlement for the subscription
        // The validity is calculated based on subscriptionType (MONTHLY=1 month, QUARTERLY=3 months, etc.)
        UserEntitlement entitlement = createUserEntitleForPaid(userId, subscribedDto, oldUserEntitlement);
        listOfUserEntitle.add(entitlement);

        LOGGER.info("Created PAID UserEntitlement for userId: {}, subscriptionId: {}, subscriptionType: {}, paperSubCategory: {}",
                    userId, subscribedDto.getId(), subscribedDto.getSubscriptionType(), subscribedDto.getPaperSubCategory());
    }

    /**
     * Create UserEntitlement for PAID subscriptions
     * @param userId
     * @param subscribedDto
     * @param oldUserEntitlement
     * @return
     */
    private UserEntitlement createUserEntitleForPaid(String userId, SubscribedDto subscribedDto,
                                                      UserEntitlement oldUserEntitlement) {
        Long createdDate = System.currentTimeMillis();

        // Calculate validity based on subscriptionType
        // MONTHLY=1 month (30 days), QUARTERLY=3 months (90 days), etc.
        double months = subscribedDto.getSubscriptionType().getVal();
        Long validity = createdDate + (long) (months * 30L * milliseconds);

        if (oldUserEntitlement != null && oldUserEntitlement.getValidity() != null
            && oldUserEntitlement.getValidity() > createdDate) {
            // Extend from existing entitlement's validity
            createdDate = oldUserEntitlement.getValidity();
            validity = createdDate + (long) (months * 30L * milliseconds);
        }

        UserEntitlement entitlement = new UserEntitlement();
        entitlement.setId(couterService.increment(CounterEnum.USERENTITLEMENT));
        entitlement.setUserId(userId);
        entitlement.setSubscriptionId(subscribedDto.getId());
        entitlement.setSubscriptionType(subscribedDto.getSubscriptionType());
        entitlement.setValidity(validity);
        entitlement.setCreatedDate(createdDate);
        entitlement.setEntitlementType(EntitlementType.SUBSCRIPTION);
        entitlement.setActive(true);
        entitlement.setPaperType(subscribedDto.getPaperType());
        entitlement.setTestType(subscribedDto.getTestType());
        entitlement.setPaperCategory(subscribedDto.getPaperCategory());
        entitlement.setPaperSubCategory(subscribedDto.getPaperSubCategory());

        return entitlement;
    }

    /**
     * Create UserEntitlement for FREE subscriptions
     * @param userId
     * @param subscribedDto
     * @param oldUserEntitlement
     * @return
     */
    private UserEntitlement createUserEntitleForFree(String userId, SubscribedDto subscribedDto,
                                                      UserEntitlement oldUserEntitlement) {
        Long createdDate = System.currentTimeMillis();
        // Default to YEARLY (12 months) validity for FREE subscriptions
        double yearlyMonths = SubscriptionType.YEARLY.getVal(); // 12 months
        Long validity = createdDate + (long) (yearlyMonths * 30L * milliseconds);

        if (oldUserEntitlement != null && oldUserEntitlement.getValidity() != null
            && oldUserEntitlement.getValidity() > createdDate) {
            // Extend from existing entitlement's validity
            createdDate = oldUserEntitlement.getValidity();
            validity = createdDate + (long) (yearlyMonths * 30L * milliseconds);
        }

        UserEntitlement entitlement = new UserEntitlement();
        entitlement.setId(couterService.increment(CounterEnum.USERENTITLEMENT));
        entitlement.setUserId(userId);
        entitlement.setSubscriptionId(subscribedDto.getId());
        entitlement.setSubscriptionType(SubscriptionType.YEARLY); // Default to YEARLY for FREE
        entitlement.setValidity(validity);
        entitlement.setCreatedDate(createdDate);
        entitlement.setEntitlementType(EntitlementType.FREE_SUBSCRIPTION);
        entitlement.setActive(true);
        entitlement.setPaperType(subscribedDto.getPaperType());
        entitlement.setTestType(subscribedDto.getTestType());
        entitlement.setPaperCategory(subscribedDto.getPaperCategory());
        entitlement.setPaperSubCategory(subscribedDto.getPaperSubCategory());

        LOGGER.info("Created FREE_SUBSCRIPTION UserEntitlement for userId: {}, subscriptionId: {}, paperSubCategory: {}, validity: {} (YEARLY)",
                    userId, subscribedDto.getId(), subscribedDto.getPaperSubCategory(), validity);

        return entitlement;
    }

    /**
     * @param userId
     * @param subscribedDto
     * @param paperId
     * @param oldUserEntitlement
     * @param subscriptionId
     * @return
     */
    public UserEntitlement createUserEntitle(String userId, SubscribedDto subscribedDto, String paperId,
                                             UserEntitlement oldUserEntitlement, boolean flag, Long subscriptionId) {

        long[] lrr = setCreatedDateAndValidity(paperId, oldUserEntitlement, flag);
        Long createdDate = lrr[0];
        Long validity = lrr[1];

        //Double daysToAdd = subscribedDto.getSubscriptionType().getVal();
        // increasing validity by one month only, as we are making entry for every month
        UserEntitlement entitlement = new UserEntitlement();
        entitlement.setId(couterService.increment(CounterEnum.USERENTITLEMENT));
        entitlement.setUserId(userId);
        if (null != subscribedDto) {
            entitlement.setSubscriptionId(subscriptionId);
            entitlement.setSubscriptionType(subscribedDto.getSubscriptionType());
        } else {
            entitlement.setSubscriptionId(oldUserEntitlement.getSubscriptionId());
            entitlement.setSubscriptionType(oldUserEntitlement.getSubscriptionType());
        }
        entitlement.setValidity(validity);
        entitlement.setPaperId(paperId);
        entitlement.setCreatedDate(createdDate);
        entitlement.setEntitlementType(EntitlementType.SUBSCRIPTION);
        entitlement.setActive(true);
        entitlement.setPaperType(subscribedDto.getPaperType());
        entitlement.setTestType(subscribedDto.getTestType());
        entitlement.setPaperCategory(subscribedDto.getPaperCategory());
        entitlement.setPaperSubCategory(subscribedDto.getPaperSubCategory());
        return entitlement;
    }

    private long[] setCreatedDateAndValidity(String paperId, UserEntitlement oldUserEntitlement, boolean flag) {
        long[] lrr = new long[2];
        Long createdDate = null;
        Long validity = 1l;
        if (paperId != null) {
            // TODO : need to consider validity timing of single paper, now it whole month
            validity *= 30 * days * milliseconds;
            if (null != oldUserEntitlement) {
                createdDate = oldUserEntitlement.getValidity();
            } else {
                createdDate = System.currentTimeMillis();
            }
            validity += createdDate;
        } else {
            if (null == oldUserEntitlement) {
                createdDate = System.currentTimeMillis();
                validity = (validity * 30 * milliseconds) + createdDate;

            } else {
                if (flag) {
                    createdDate = oldUserEntitlement.getValidity();
                    validity = (validity * 30 * milliseconds) + createdDate;
                } else {
                    createdDate = oldUserEntitlement.getCreatedDate();
                    validity = (validity * 30 * milliseconds) + oldUserEntitlement.getValidity();
                }
            }
        }
        lrr[0] = createdDate;
        lrr[1] = validity;
        return lrr;
    }

    /**
     * @param userId
     * @param createdDate
     * @param validity
     * @param testType
     * @param paperCategory
     * @param paperType
     * @return
     */
    public List<SubscribedDto> getSubscribedDto(String userId, Boolean active, Long createdDate, Long validity,
                                                PaperType paperType, PaperCategory paperCategory, TestType testType) {
        List<UserEntitlement> userEntitlementList = null;
        // Query both SUBSCRIPTION and FREE_SUBSCRIPTION entitlements
        List<EntitlementType> subscriptionTypes = Arrays.asList(EntitlementType.SUBSCRIPTION, EntitlementType.FREE_SUBSCRIPTION);
        if (createdDate != null && validity != null) {
            userEntitlementList = entitlementRepository
                    .findByUserIdAndActiveAndCreatedDateAndValidityAndPaperTypeAndTestTypeAndEntitlementTypeIn(userId, active, createdDate, validity, paperType, testType, subscriptionTypes);
        } else {
            userEntitlementList = entitlementRepository.findByUserIdAndPaperTypeAndTestTypeAndEntitlementTypeIn(userId, active, paperType, testType, subscriptionTypes);
        }
        // return paid always + optional free
        return getSubscriptionFromUserEntitle(paperType, paperCategory, testType, userEntitlementList);
    }

    /**
     * @param paperType
     * @param paperCategory
     * @param testType
     * @param userEntitlementList
     * @return
     */
    public List<SubscribedDto> getSubscriptionFromUserEntitle(PaperType paperType, PaperCategory paperCategory,
                                                              TestType testType, List<UserEntitlement> userEntitlementList) {
        List<Long> listOfSubsIds = new LinkedList<>();
        List<SubscribedDto> listOfSubDto = new LinkedList<>();
        Map<Long, UserEntitlement> mapOfUserEntitlement = new HashMap<>();
        userEntitlementList.stream().filter((userEntitle) -> {
            return true;
        }).forEach((currObj) -> {
            if (null == currObj.getPaperId()) {
                listOfSubsIds.add(currObj.getSubscriptionId());
                mapOfUserEntitlement.put(currObj.getSubscriptionId(), currObj);
            } else {
                SubscribedDto subDto = new SubscribedDto();
                subDto.setId(currObj.getSubscriptionId());
                subDto.setPaperIds(Arrays.asList(currObj.getPaperId()));
                PaperInfo paperInfo = PaperInfo.builder().
                        id(currObj.getPaperId()).
                        paperName(currObj.getPaperName()).
                        validity(currObj.getValidity()).
                        build();
                subDto.setListOfPaperInfo(Arrays.asList(paperInfo));
                subDto.setSubscriptionType(currObj.getSubscriptionType());
                subDto.setValidity(currObj.getValidity());
                listOfSubDto.add(subDto);
            }
        });

        List<Subscription> subScriptinList = new ArrayList<>();
        List<Subscription> list = new ArrayList<>();
        if (testType != null && TestType.FREE.equals(testType)) {

            if (paperCategory != null) {
                subScriptinList = subscriptionRepository.findByPaperTypeAndPaperCategoryAndTestType(paperType, paperCategory, testType, new Date().getTime(), new Date().getTime());
            } else {
                subScriptinList = subscriptionRepository.findByPaperTypeAndTestType(paperType, testType, new Date().getTime(), new Date().getTime());
            }

        } else {
            list = subscriptionRepository.findByIdIn(listOfSubsIds);
        }
        list.addAll(subScriptinList);

        // convert to subscribe // needs to change this

        list.stream().filter((subObj) -> {
            boolean flag = true;
            if (null != paperType && !subObj.getPaperType().equals(paperType))
                flag = false;

            if (flag && null != testType && !subObj.getTestType().equals(testType))
                flag = false;

            if (flag && null != paperCategory && !subObj.getPaperCategory().equals(paperCategory))
                flag = false;

            return flag;

        }).forEach((subObj) -> {
            if (subObj != null)
                listOfSubDto.add(convertToSubscribedDto(subObj, mapOfUserEntitlement));
        });

        // remove based on filter from final list also

        return listOfSubDto;
    }

    /**
     * @param mapOfPaperCatogory
     * @param mapOfSubscribtionDto
     * @param listOfPriceMetaData
     * @param subObj
     */
    public void addSubCategoryToCategoryDto(Map<PaperCategory, PaperCategoryDto> mapOfPaperCatogory,
                                            Map<PaperSubCategory, SubscriptionDto> mapOfSubscribtionDto, List<PriceMetaData> listOfPriceMetaData,
                                            Subscription subObj) {
        PaperCategoryDto categoryDto = mapOfPaperCatogory.get(subObj.getPaperCategory());
        List<PaperSubCatogoryDto> listOfSubCategoryDto = categoryDto.getListOfSubCategoryDto();
        if (mapOfSubscribtionDto.containsKey(subObj.getPaperSubCategory())) {
            SubscriptionDto subscriptionDto = mapOfSubscribtionDto.get(subObj.getPaperSubCategory());
            // no null check as initialized when added 1st time
            subscriptionDto.getListOfSubscriptionIds().add(subObj.getId());

        } else {
            PaperSubCatogoryDto paperSubCatogoryDto = new PaperSubCatogoryDto();
            SubscriptionDto subscriptionDto = convertToSubscriptionDto(subObj, listOfPriceMetaData);
            paperSubCatogoryDto.setSubscriptionDto(subscriptionDto);
            paperSubCatogoryDto.setPaperSubCategory(subObj.getPaperSubCategory().toString());
            listOfSubCategoryDto.add(paperSubCatogoryDto);
            mapOfSubscribtionDto.put(subObj.getPaperSubCategory(), subscriptionDto);
        }
    }

    /**
     * @param mapOfPaperCatogory
     * @param mapOfSubscribtionDto
     * @param listOfPriceMetaData
     * @param subObj
     * @return
     */
    public PaperCategoryDto addCategoryAndSubCategoryToPaperTypeDto(
            Map<PaperCategory, PaperCategoryDto> mapOfPaperCatogory,
            Map<PaperSubCategory, SubscriptionDto> mapOfSubscribtionDto, List<PriceMetaData> listOfPriceMetaData,
            Subscription subObj) {
        PaperCategoryDto categoryDto = new PaperCategoryDto();
        mapOfPaperCatogory.put(subObj.getPaperCategory(), categoryDto);
        // put category in map category
        categoryDto.setPaperCategory(subObj.getPaperCategory().getCategory());
        List<PaperSubCatogoryDto> listOfSubCategoryDto = new LinkedList<>();
        PaperSubCatogoryDto paperSubCatogoryDto = new PaperSubCatogoryDto();

        SubscriptionDto subscriptionDto = convertToSubscriptionDto(subObj, listOfPriceMetaData);
        paperSubCatogoryDto.setSubscriptionDto(subscriptionDto);
        paperSubCatogoryDto.setPaperSubCategory(subObj.getPaperSubCategory().toString());

        // add to subscritionDto map
        mapOfSubscribtionDto.put(subObj.getPaperSubCategory(), subscriptionDto);
        // add subcategory in list subcategory
        listOfSubCategoryDto.add(paperSubCatogoryDto);
        // add list of subcategory to category obj
        categoryDto.setListOfSubCategoryDto(listOfSubCategoryDto);
        return categoryDto;
    }


    /**
     * @param listOfSubDto
     * @param paperIds
     * @return
     */
    public List<PaperInfo> getPaperInfoFromSubscribedDtoList(List<SubscribedDto> listOfSubDto, List<String> paperIds) {
        Map<String, PaperInfo> mapOfPaperInfo = new HashMap<>();
        if (listOfSubDto != null && !listOfSubDto.isEmpty())
            listOfSubDto.forEach((subDtoObj) -> {
                if (subDtoObj != null && subDtoObj.getListOfPaperInfo() != null)
                    subDtoObj.getListOfPaperInfo().forEach((paperInfoObj) -> {
                        if (paperIds != null && !paperIds.isEmpty()) {
                            if (paperIds.contains(paperInfoObj.getId())) {
                                mapOfPaperInfo.put(paperInfoObj.getId(), paperInfoObj);
                            }
                        } else {
                            mapOfPaperInfo.put(paperInfoObj.getId(), paperInfoObj);
                        }

                    });
            });
        List<PaperInfo> listOfPaperInfo = new LinkedList<>(mapOfPaperInfo.values());
        return listOfPaperInfo;
    }


    /**
     * @param listOfSubs
     * @return
     */
    public List<SubscriptionDto> convertToSubscriptionDtoList(List<Subscription> listOfSubs) {
        List<SubscriptionDto> listOfSubsDto = new LinkedList<>();
        Map<PaperSubCategory, SubscriptionDto> mapOfCategoryVsSubsDto = new HashMap<>();
        listOfSubs.forEach((subObj) -> {
            SubscriptionDto dto = null;
            if ((dto = mapOfCategoryVsSubsDto.get(subObj.getPaperSubCategory())) != null) {
                dto.getListOfSubscriptionIds().add(subObj.getId());
            } else {
                dto = convertToSubscriptionDto(subObj, null);
                listOfSubsDto.add(dto);
                mapOfCategoryVsSubsDto.put(subObj.getPaperSubCategory(), dto);

            }

        });
        return listOfSubsDto;
    }

    public List<PaperInfo> getPaperInfoFromSubscriptionList(List<Subscription> listOfSubs, List<String> paperIds) {

        List<PaperInfo> listOfPaperInfo = new LinkedList<>();
        listOfSubs.forEach((subObj) -> {
            if (paperIds != null && !paperIds.isEmpty()) {
                List<PaperInfo> listOfPaperIds = subObj.getListOfPaperInfo();
                if (listOfPaperIds != null && !listOfPaperIds.isEmpty()) {
                    listOfPaperIds.forEach((paperInfo) -> {
                        if (paperIds.contains(paperInfo.getId())) {
                            paperInfo.setCreatedDate(subObj.getCreatedDate());
                            paperInfo.setValidity(subObj.getValidity());
                            listOfPaperInfo.add(paperInfo);
                        }
                    });
                }
            } else {

            }
        });

        return listOfPaperInfo;
    }

    /**
     * @param timeIMillis
     * @param mapOfSubCategoryVsPriceMetaData
     * @param listOfPaidPaperTaken
     * @param listOfSubsToSave
     */
    public void addPaidPaperInSubscription(Long timeIMillis,
                                           Map<PaperSubCategory, PriceMetaData> mapOfSubCategoryVsPriceMetaData,
                                           List<PaidPaperCollection> listOfPaidPaperTaken, List<Subscription> listOfSubsToSave) {
        PaperSubCategory.getList().forEach((subCategory) -> {
            List<PaidPaperCollection> listOfPaidPaper = paidPaperCollectionRepository
                    .findByPaperSubCategoryAndTaken(subCategory, false, TestType.PAID, timeIMillis, timeIMillis);
            List<Subscription> listOfSubscription = subscriptionRepository
                    .findByPaperSubCategoryAndTestTypeOrder(subCategory, TestType.PAID, timeIMillis, timeIMillis);
            // expected this list to have only one entry
            Collections.sort(listOfPaidPaper, (o1, o2) -> {
                return (o1.getPriorty() > o2.getPriorty()) ? 0 : 1;
            });
            PriceMetaData priceMetaData = mapOfSubCategoryVsPriceMetaData.get(subCategory);
            if (listOfSubscription != null && !listOfSubscription.isEmpty()) {
                Subscription subObj = listOfSubscription.get(0);
                listOfSubsToSave.add(subObj);
                for (int i = 0; i < priceMetaData.getNumberOfPapers(); i++) {
                    if (i >= listOfPaidPaper.size())
                        break;

                    if (subObj.getListOfPaperInfo() == null) {
                        List<PaperInfo> listOfPaperInfo = new LinkedList<>();
                        List<String> paperIds = new LinkedList<>();
                        PaidPaperCollection paidPaperObj = listOfPaidPaper.get(i);
                        paidPaperObj.setTaken(true);
                        listOfPaidPaperTaken.add(paidPaperObj);
                        PaperInfo paperInfo = PaperInfo.builder().
                                id(paidPaperObj.getId()).
                                paperName(paidPaperObj.getPaperName()).
                                build();
                        listOfPaperInfo.add(paperInfo);
                        paperIds.add(paidPaperObj.getId());
                        subObj.setListOfPaperInfo(listOfPaperInfo);
                        subObj.setPaperIds(paperIds);

                    } else {

                        List<PaperInfo> listOfPaperInfo = subObj.getListOfPaperInfo();
                        List<String> paperIds = subObj.getPaperIds();
                        PaidPaperCollection paidPaperObj = listOfPaidPaper.get(i);
                        paidPaperObj.setTaken(true);
                        listOfPaidPaperTaken.add(paidPaperObj);
                        PaperInfo paperInfo = PaperInfo.builder().
                                id(paidPaperObj.getId()).
                                paperName(paidPaperObj.getPaperName()).
                                build();
                        listOfPaperInfo.add(paperInfo);
                        paperIds.add(paidPaperObj.getId());
                    }
                }
            }

        });
    }


    /**
     * @param timeIMillis
     * @param mapOfSubCategoryVsPriceMetaData
     * @param listOfPaidPaperTaken
     * @param listOfSubsToSave
     */
    public void addFreePaperInSubscription(Long timeIMillis,
                                           Map<PaperSubCategory, PriceMetaData> mapOfSubCategoryVsPriceMetaData,
                                           List<PaidPaperCollection> listOfPaidPaperTaken, List<Subscription> listOfSubsToSave) {
        PaperSubCategory.getList().forEach((subCategory) -> {
            List<PaidPaperCollection> listOfPaidPaper = paidPaperCollectionRepository
                    .findByPaperSubCategoryAndTaken(subCategory, false, TestType.FREE, timeIMillis, timeIMillis);
            List<Subscription> listOfSubscription = subscriptionRepository
                    .findByPaperSubCategoryAndTestTypeOrder(subCategory, TestType.FREE, timeIMillis, timeIMillis);
            // expected this list to have only one entry
            Collections.sort(listOfPaidPaper, (o1, o2) -> {
                return (o1.getPriorty() > o2.getPriorty()) ? 0 : 1;
            });
            PriceMetaData priceMetaData = mapOfSubCategoryVsPriceMetaData.get(subCategory);
            if (listOfSubscription != null && !listOfSubscription.isEmpty()) {
                Subscription subObj = listOfSubscription.get(0);
                listOfSubsToSave.add(subObj);
                for (int i = 0; i < priceMetaData.getNumberOfPapers(); i++) {
                    if (i >= listOfPaidPaper.size())
                        break;

                    if (subObj.getListOfPaperInfo() == null) {
                        List<PaperInfo> listOfPaperInfo = new LinkedList<>();
                        List<String> paperIds = new LinkedList<>();
                        PaidPaperCollection paidPaperObj = listOfPaidPaper.get(i);
                        paidPaperObj.setTaken(true);
                        listOfPaidPaperTaken.add(paidPaperObj);
                        PaperInfo paperInfo = PaperInfo.builder().
                                id(paidPaperObj.getId()).
                                paperName(paidPaperObj.getPaperName()).
                                build();
                        listOfPaperInfo.add(paperInfo);
                        paperIds.add(paidPaperObj.getId());
                        subObj.setListOfPaperInfo(listOfPaperInfo);
                        subObj.setPaperIds(paperIds);

                    } else {

                        List<PaperInfo> listOfPaperInfo = subObj.getListOfPaperInfo();
                        List<String> paperIds = subObj.getPaperIds();
                        PaidPaperCollection paidPaperObj = listOfPaidPaper.get(i);
                        paidPaperObj.setTaken(true);
                        listOfPaidPaperTaken.add(paidPaperObj);
                        PaperInfo paperInfo = PaperInfo.builder().
                                id(paidPaperObj.getId()).
                                paperName(paidPaperObj.getPaperName()).
                                build();
                        listOfPaperInfo.add(paperInfo);
                        paperIds.add(paidPaperObj.getId());
                    }
                }
            }

        });
    }


    public List<SubscribedDto> getSubscribedDtoFromUserPass(Subscription subscription) {

        return null;
    }

    public SubscribedDto convertToSubscribedDto(Subscription subscription) {
        return SubscribedDto.builder().id(subscription.getId()).
                listOfPaperInfo(subscription.getListOfPaperInfo()).
                paperIds(subscription.getPaperIds()).
                validity(subscription.getValidity()).
                createdDate(subscription.getCreatedDate()).
                paperType(subscription.getPaperType()).
                paperCategory(subscription.getPaperCategory()).
                paperSubCategory(subscription.getPaperSubCategory()).
                testType(subscription.getTestType()).
                build();
    }
}
