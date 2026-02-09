package com.book.ensureu.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.book.ensureu.constant.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.book.ensureu.dto.PaperCategoryDto;
import com.book.ensureu.dto.PaperInfo;
import com.book.ensureu.dto.PaperPackageDto;
import com.book.ensureu.dto.PaperTypeDto;
import com.book.ensureu.dto.PurchaseSubscriptionsDto;
import com.book.ensureu.dto.SubscribedDto;
import com.book.ensureu.dto.SubscriptionDto;
import com.book.ensureu.model.PaidPaperCollection;
import com.book.ensureu.common.model.PriceMetaData;
import com.book.ensureu.model.PurchaseSubscriptions;
import com.book.ensureu.model.Subscription;
import com.book.ensureu.model.UserEntitlement;
import com.book.ensureu.model.UserPass;
import com.book.ensureu.repository.PaidPaperCollectionRepository;
import com.book.ensureu.repository.PriceMetaDataRepository;
import com.book.ensureu.repository.PurchaseSubscriptionsRespository;
import com.book.ensureu.repository.SubscriptionRepository;
import com.book.ensureu.repository.UserEntitlementRepository;
import com.book.ensureu.response.dto.Response;
import com.book.ensureu.service.CounterService;
import com.book.ensureu.service.SubscriptionService;
import com.book.ensureu.service.impl.helper.SubscriptionServiceImplHelper;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionServiceImpl.class.getName());

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserEntitlementRepository entitlementRepository;

    @Autowired
    private PaidPaperCollectionRepository paidPaperCollectionRepository;

    @Autowired
    private PriceMetaDataRepository priceMetaDataRepository;

    @Autowired
    private PurchaseSubscriptionsRespository purchaseSubscriptionsRespository;

    @Autowired
    private SubscriptionServiceImplHelper implHelper;

    @Autowired
    private CounterService counterService;

    @Value("${day.milliseconds:86400000}")
    private Long milliseconds;

    @Override
    public ResponseEntity<Response> subscribe(String userId, SubscribedDto subscribedDto) {

        LOGGER.info(" User {} subscribing subs : {} id: {}", userId, subscribedDto.getDescription(), subscribedDto.getId());
        Long timeInMills = System.currentTimeMillis();
        PurchaseSubscriptions purchaseSubscriptions = null;
        try {
            //TODO
            // will use this subscriptionList later as of now using Ids list sent by UI and
            // User entitle
            // TODO : need to sort subscriptionList, it will be used when user buys single paper
            List<Subscription> subObj = subscriptionRepository.findByPaperSubCategoryInAndTestTypeOrder(
                    Arrays.asList(subscribedDto.getPaperSubCategory()), subscribedDto.getTestType(), timeInMills);

            // Query both SUBSCRIPTION and FREE_SUBSCRIPTION entitlements
            List<EntitlementType> subscriptionTypes = Arrays.asList(EntitlementType.SUBSCRIPTION, EntitlementType.FREE_SUBSCRIPTION);
            List<UserEntitlement> listOfUserEntitlement = entitlementRepository
                    .findByUserIdAndPaperTypeAndPaperCategoryActiveAndGreaterThanValidityAndEntitlementTypeIn(userId, subscribedDto.getPaperType(),subscribedDto.getPaperCategory(),true, timeInMills, subscriptionTypes);

            if (subObj != null && !subObj.isEmpty()) {
                Subscription subscription = subObj.get(0);

                // For FREE subscriptions, set subscription ID from DB if not provided
                if (TestType.FREE.equals(subscribedDto.getTestType())) {
                    if (subscribedDto.getId() == null) {
                        subscribedDto.setId(subscription.getId());
                    }
                    if (subscribedDto.getListOfSubscriptionIds() == null || subscribedDto.getListOfSubscriptionIds().isEmpty()) {
                        subscribedDto.setListOfSubscriptionIds(Arrays.asList(subscription.getId()));
                    }
                    LOGGER.info("FREE subscription - set subscriptionId: {} for userId: {}", subscription.getId(), userId);
                }

                // For PAID subscriptions, save to PurchaseSubscriptions first
                if (TestType.PAID.equals(subscribedDto.getTestType())) {
                    purchaseSubscriptions = saveOrUpdatePurchaseSubscriptions(userId, subscribedDto, null, PurchaseStatus.INPROGRESS);
                    LOGGER.info("Created PurchaseSubscriptions for PAID subscription, userId: {}, status: INPROGRESS", userId);
                }

                UserEntitlement userLastEntitlement = null;
                List<String> listOfPaperIds = new LinkedList<>();

                // Check for existing entitlements
                List<Long> subscriptionIds = subscribedDto.getListOfSubscriptionIds();
                if (listOfUserEntitlement != null && !listOfUserEntitlement.isEmpty() && subscriptionIds != null && !subscriptionIds.isEmpty()) {
                    // find if any day is subscribed or any package for given subscription Id

                    for (UserEntitlement userEntitle : listOfUserEntitlement) {
                        if (subscriptionIds.contains(userEntitle.getSubscriptionId())) {
                            if (SubscriptionType.DAY.equals(userEntitle.getSubscriptionType())) {
                                listOfPaperIds.add(userEntitle.getPaperId());
                            } else {
                                // conditions to get last subscribed entitle to User
                                if (null != userLastEntitlement) {
                                    if (userLastEntitlement.getValidity() < userEntitle.getValidity())
                                        userLastEntitlement = userEntitle;
                                } else {
                                    userLastEntitlement = userEntitle;
                                }
                                // no need as we need last entitlement to user for this subcription id
                                // break; // no need as we need the last most subscription
                            }
                        }
                    } // loop ends

                    if (userLastEntitlement != null) {
                        // case 1 when buying another subscription type for same subscription
                        // case 2 2 paper were bought and now whole package is bought.

                        implHelper.createUserEntitlementAndSave(userId, subscribedDto, null, userLastEntitlement);

                    } else if (!SubscriptionType.DAY.equals(subscribedDto.getSubscriptionType())) {

                        implHelper.createUserEntitlementAndSave(userId, subscribedDto, null, null);

                    } else {

                        List<String> listOfSubPaperIds = subscription.getPaperIds();
                        listOfSubPaperIds.removeAll(listOfPaperIds);
                        if (!listOfSubPaperIds.isEmpty()) {
                            implHelper.createUserEntitlementAndSave(userId, subscribedDto, listOfSubPaperIds.get(0),
                                    null);

                        }else{
                            // this is when user buys all paper for a month
                            return ResponseEntity.ok(Response.builder()
                                    .message("DAY subscription limit exceeds")
                                    .status(429)
                                    .build());
                        }
                    }

                } else {
                    // No existing entitlement - create new one
                    String paperId = null;
                    if (SubscriptionType.DAY.equals(subscribedDto.getSubscriptionType())) {
                        paperId = subscription.getPaperIds().get(0); // assigned first paperId
                    }
                    LOGGER.info("Creating new entitlement for userId: {}, subscriptionId: {}, testType: {}",
                                userId, subscribedDto.getId(), subscribedDto.getTestType());
                    implHelper.createUserEntitlementAndSave(userId, subscribedDto, paperId, null);
                }

                // For PAID subscriptions, mark as COMPLETED after UserEntitlement is created
                if (TestType.PAID.equals(subscribedDto.getTestType()) && purchaseSubscriptions != null) {
                    saveOrUpdatePurchaseSubscriptions(userId, subscribedDto, purchaseSubscriptions, PurchaseStatus.COMPLETED);
                    LOGGER.info("Updated PurchaseSubscriptions for PAID subscription, userId: {}, status: COMPLETED", userId);
                }

            } else {
                //TODO throw exception like invalid request with exception code
                LOGGER.info("subscription id is not present {}", (subObj != null && !subObj.isEmpty()) ? subObj.get(0).getId() : "null");
            }
        } catch (Exception e) {
            LOGGER.error(" Exception occurred {}", e.getMessage());
            // For PAID subscriptions, mark as FAILED on error
            if (TestType.PAID.equals(subscribedDto.getTestType()) && purchaseSubscriptions != null) {
                saveOrUpdatePurchaseSubscriptions(userId, subscribedDto, purchaseSubscriptions, PurchaseStatus.FAILED);
                LOGGER.error("Updated PurchaseSubscriptions for PAID subscription, userId: {}, status: FAILED", userId);
            }
        }

        return ResponseEntity.ok(Response.builder()
                .message("Subscribed successfully")
                .build());

    }

    private PurchaseSubscriptions saveOrUpdatePurchaseSubscriptions(String userId, SubscribedDto subscribedDto,
                                                                    PurchaseSubscriptions purchaseSubscriptions, PurchaseStatus purchaseStatus) {
        PurchaseSubscriptions purchaseSubscriptionObj = null;
        if (purchaseSubscriptions != null && purchaseSubscriptions.getId() != null) {
            purchaseSubscriptions.setModifiedDate(new Date().getTime());
            purchaseSubscriptions.setParchaseStatus(purchaseStatus);
            purchaseSubscriptionObj = purchaseSubscriptionsRespository.save(purchaseSubscriptions);
        } else {
            if (subscribedDto != null && userId != null) {
                purchaseSubscriptionObj = new PurchaseSubscriptions();
                purchaseSubscriptionObj.setUserId(userId);
                purchaseSubscriptionObj.setCreatedDate(new Date().getTime());
                purchaseSubscriptionObj.setPaperType(subscribedDto.getPaperType());
                purchaseSubscriptionObj.setPaperCategory(subscribedDto.getPaperCategory());
                purchaseSubscriptionObj.setPaperSubCategory(subscribedDto.getPaperSubCategory());
                purchaseSubscriptionObj.setSubscriptionType(subscribedDto.getSubscriptionType());
                purchaseSubscriptionObj.setActualPrice(0D);
                purchaseSubscriptionObj.setTestType(subscribedDto.getTestType());
                purchaseSubscriptionObj.setParchaseStatus(purchaseStatus);
                // purchaseSubscription.setValidity(validity);
                purchaseSubscriptionObj.setId(counterService.increment(CounterEnum.PURCHASESUBSCRIPTION));
                purchaseSubscriptionObj = purchaseSubscriptionsRespository.save(purchaseSubscriptionObj);
            }
        }
        return purchaseSubscriptionObj;
    }

    @Override
    public List<SubscribedDto> getSubscriptionListForUser(String userId, PaperType paperType, TestType testType) {

        Long timeInMills = System.currentTimeMillis();
        LOGGER.info("getting user subscription for current timeInMills: {} for user: {}", timeInMills, userId);
        return implHelper.getSubscribedDto(userId, true, timeInMills, timeInMills,
                paperType, null, testType);
    }

    @Override
    public List<SubscribedDto> getSubscriptionListForUser(String userId) {

        Long timeInMills = System.currentTimeMillis();
        LOGGER.info("getting user subscription for current timeInMills:" + timeInMills + " for user :" + userId);
        List<SubscribedDto> listOfSubDto = implHelper.getSubscribedDto(userId, true, timeInMills, timeInMills, null,
                null, null);
        return listOfSubDto;
    }

    @Override
    public void movePaperToSubscription() {

        Long timeIMillis = System.currentTimeMillis();
        Map<PaperSubCategory, PriceMetaData> mapOfSubCategoryVsPriceMetaData = new HashMap<>();
        List<PriceMetaData> listOfPriceMetaData = priceMetaDataRepository.findAll();

        listOfPriceMetaData.forEach((priceObj) -> {
            if (mapOfSubCategoryVsPriceMetaData.containsKey(priceObj.getPaperSubCategory())) {
                // below condition so that we can have maximum subscription type in order to add
                // max paperIds to subscription
                if (mapOfSubCategoryVsPriceMetaData.get(priceObj.getPaperSubCategory()).getSubscriptionType()
                        .getVal() < priceObj.getSubscriptionType().getVal())
                    mapOfSubCategoryVsPriceMetaData.put(priceObj.getPaperSubCategory(), priceObj);
            } else {
                mapOfSubCategoryVsPriceMetaData.put(priceObj.getPaperSubCategory(), priceObj);
            }
        });

        List<PaidPaperCollection> listOfPaidPaperTaken = new LinkedList<>();
        List<Subscription> listOfSubsToSave = new LinkedList<>();
        implHelper.addPaidPaperInSubscription(timeIMillis, mapOfSubCategoryVsPriceMetaData, listOfPaidPaperTaken,
                listOfSubsToSave);

        // save paid paper with updated taken flag as true

        if (!listOfPaidPaperTaken.isEmpty())
            paidPaperCollectionRepository.saveAll(listOfPaidPaperTaken);

        if (!listOfSubsToSave.isEmpty())
            subscriptionRepository.saveAll(listOfSubsToSave);

    }

    @Override
    public PaperPackageDto getSubscriptionList(TestType testType) {

        LOGGER.info(" in method getSubscriptionList for testType :" + testType);
        PaperPackageDto paperPackageDto = null;
        ;
        try {
            List<Subscription> listOfSubs = implHelper.getSubscriptionByTestType(testType);
            paperPackageDto = new PaperPackageDto();
            Map<PaperType, PaperTypeDto> mapOfPaperType = new HashMap<>();
            // List<PaperTypeDto> listOfTypeDto = new link
            Map<PaperCategory, PaperCategoryDto> mapOfPaperCatogory = new HashMap<>();
            Map<PaperSubCategory, SubscriptionDto> mapOfSubscribtionDto = new HashMap<>();
            if (TestType.PAID.equals(testType)) {
               // List<PriceMetaData> listOfPriceMetaData = priceMetaDataRepository.findAll();
            	List<PriceMetaData> listOfPriceMetaData = priceMetaDataRepository.findBySubscriptionTypeIn(SubscriptionType.getAll());
                // implHelper.addDumyData(listOfPriceMetaData);

                listOfSubs.forEach((subObj) -> {
                    if (!mapOfPaperType.containsKey(subObj.getPaperType())) {
                        PaperTypeDto paperTypeDto = new PaperTypeDto();
                        paperTypeDto.setPaperType(subObj.getPaperType().toString());
                        // put papertype in map papertype
                        mapOfPaperType.put(subObj.getPaperType(), paperTypeDto);
                        if (!mapOfPaperCatogory.containsKey(subObj.getPaperCategory())) {
                            PaperCategoryDto categoryDto = implHelper.addCategoryAndSubCategoryToPaperTypeDto(
                                    mapOfPaperCatogory, mapOfSubscribtionDto, listOfPriceMetaData, subObj);
                            List<PaperCategoryDto> listOfCategoryDto = new LinkedList<>();
                            listOfCategoryDto.add(categoryDto);
                            paperTypeDto.setListOfCategoryDto(listOfCategoryDto);
                        } else {
                            implHelper.addSubCategoryToCategoryDto(mapOfPaperCatogory, mapOfSubscribtionDto,
                                    listOfPriceMetaData, subObj);

                        }
                    } else {
                        PaperTypeDto paperTypeDto = mapOfPaperType.get(subObj.getPaperType());
                        if (!mapOfPaperCatogory.containsKey(subObj.getPaperCategory())) {
                            PaperCategoryDto categoryDto = implHelper.addCategoryAndSubCategoryToPaperTypeDto(
                                    mapOfPaperCatogory, mapOfSubscribtionDto, listOfPriceMetaData, subObj);
                            List<PaperCategoryDto> listOfCategoryDto = paperTypeDto.getListOfCategoryDto();
                            listOfCategoryDto.add(categoryDto);
                        } else {
                            implHelper.addSubCategoryToCategoryDto(mapOfPaperCatogory, mapOfSubscribtionDto,
                                    listOfPriceMetaData, subObj);
                        }
                    }
                });
                paperPackageDto.setListOfPaperTypeDto(new LinkedList<>(mapOfPaperType.values()));

            } else {
                List<SubscriptionDto> listOfSubsDto = implHelper.convertToSubscriptionDtoList(listOfSubs);
                paperPackageDto.setListOfFreeSubscription(listOfSubsDto);
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred " + e.getMessage());
            throw e;
        }
        return paperPackageDto;
    }

    @Override
    public Response<UserPass> getUserPassList() {
        return null;
    }

    @Override
    public List<PaperInfo> getPaperInfoListForUser(String userId, Long createdDate, Long validity, Boolean active,
                                                   PaperType paperType, PaperCategory paperCategory, TestType testType) {

        LOGGER.info("Getting user subscription for  createdDate: {} for user: {}", createdDate, userId);
        List<SubscribedDto> listOfSubDto = implHelper.getSubscribedDto(userId, active, createdDate, validity, paperType,
                paperCategory, testType);

        return implHelper.getPaperInfoFromSubscribedDtoList(listOfSubDto, null);
    }

    @Override
    public List<SubscribedDto> getAllSubscriptionListForUser(String userId) {
        Long timeInMilliSec = 1000l;
        Long time = System.currentTimeMillis();
        timeInMilliSec = time + timeInMilliSec * milliseconds;
        List<SubscribedDto> listOfSubDto = implHelper.getSubscribedDto(userId, true, null, null, null, null,
                TestType.PAID);
        return listOfSubDto;
    }

    @Override
    public List<SubscribedDto> getAllSubscriptionListForUser(String userId, PaperType paperType, TestType testType) {
        Long timeInMilliSec = 1000l;
        Long time = System.currentTimeMillis();
        timeInMilliSec = time + timeInMilliSec * milliseconds;
        List<SubscribedDto> listOfSubDto = implHelper.getSubscribedDto(userId, true, null, null, paperType, null,
                testType);
        return listOfSubDto;
    }

    @Override
    public List<PaperInfo> getPaperInfoListForUser(String userName, List<String> paperIds, TestType testType) {
        LOGGER.info(" inside method getPaperInfoListForUser " + userName + " testtype " + testType);
        List<PaperInfo> listOfPaperInfo = null;
        Long timeInMillis = System.currentTimeMillis();
        if (testType != null) {
            if (TestType.PAID.equals(testType)) {
                List<UserEntitlement> listOfEntitle = entitlementRepository
                        .findByUserIdAndActiveAndCreatedDateAndValidityAndEntitlementType(userName, true, timeInMillis, timeInMillis, EntitlementType.SUBSCRIPTION);

                List<SubscribedDto> listOfSubDto = implHelper.getSubscriptionFromUserEntitle(null, null, testType,
                        listOfEntitle);
                listOfPaperInfo = implHelper.getPaperInfoFromSubscribedDtoList(listOfSubDto, paperIds);
            } else if (TestType.FREE.equals(testType)) {
                // Query FREE_SUBSCRIPTION entitlements for free papers
                List<UserEntitlement> listOfEntitle = entitlementRepository
                        .findByUserIdAndActiveAndCreatedDateAndValidityAndEntitlementType(userName, true, timeInMillis, timeInMillis, EntitlementType.FREE_SUBSCRIPTION);

                if (listOfEntitle != null && !listOfEntitle.isEmpty()) {
                    List<SubscribedDto> listOfSubDto = implHelper.getSubscriptionFromUserEntitle(null, null, testType,
                            listOfEntitle);
                    listOfPaperInfo = implHelper.getPaperInfoFromSubscribedDtoList(listOfSubDto, paperIds);
                } else {
                    // Fallback to subscription list if no entitlement found
                    List<Subscription> listOfSubs = implHelper.getSubscriptionByTestType(testType);
                    listOfPaperInfo = implHelper.getPaperInfoFromSubscriptionList(listOfSubs, paperIds);
                }
            }
        } else {

        }

        return listOfPaperInfo;
    }

    @Override
    public List<PaperInfo> getLastPaperInfoListForUser(String userName, Boolean active, PaperType paperType,
                                                       PaperCategory paperCategory, TestType testType) {
        Long timeInMilli = System.currentTimeMillis();
        List<PaperInfo> listOfPaperInfo = null;
        if (testType != null) {
            if (TestType.PAID.equals(testType)) {
                // Query both SUBSCRIPTION and FREE_SUBSCRIPTION
                List<EntitlementType> subscriptionTypes = Arrays.asList(EntitlementType.SUBSCRIPTION, EntitlementType.FREE_SUBSCRIPTION);
                List<UserEntitlement> listOfEntitle = entitlementRepository.findByUserIdAndActiveAndEntitlementTypeIn(userName, true, subscriptionTypes);
                int count = -1;
                for (UserEntitlement userEntitlement : listOfEntitle) {
                    count++;
                    if (userEntitlement.getCreatedDate() < timeInMilli && userEntitlement.getValidity() > timeInMilli) {
                        break;
                    }
                }

                if (count > 0) {
                    int startIndex = 0;
                    if (count > 2) {
                        startIndex = count - 2;
                    }
                    List<SubscribedDto> listOfSubDto = implHelper.getSubscriptionFromUserEntitle(null, null, testType,
                            listOfEntitle.subList(startIndex, count));
                    listOfPaperInfo = implHelper.getPaperInfoFromSubscribedDtoList(listOfSubDto, null);
                } else {
                    LOGGER.info(" no past subscriptions for userId :" + userName);
                }
            } else {

            }
        } else {
        }
        return listOfPaperInfo;
    }

    @Override
    public List<PurchaseSubscriptionsDto> getPurchaseSubscription(String userId, PaperType paperType,
                                                                  TestType testType) {
        List<PurchaseSubscriptions> purchaseSubscriptions = purchaseSubscriptionsRespository
                .findByUserIdAndPaperTypeAndTestType(userId, paperType, testType);
        List<PurchaseSubscriptionsDto> purchaseSubscriptionsDtos = convertPurcahseSubscriptionDtos(
                purchaseSubscriptions);
        return purchaseSubscriptionsDtos;
    }

    private List<PurchaseSubscriptionsDto> convertPurcahseSubscriptionDtos(
            List<PurchaseSubscriptions> purchaseSubscriptions) {

        List<PurchaseSubscriptionsDto> purchaseSubscriptionsDtos = new ArrayList<>();
        if (purchaseSubscriptions != null && !purchaseSubscriptions.isEmpty()) {
            purchaseSubscriptions.forEach(subscribed -> {
                PurchaseSubscriptionsDto purchaseSubscriptionDto = new PurchaseSubscriptionsDto();
                purchaseSubscriptionDto.setUserId(subscribed.getUserId());
                purchaseSubscriptionDto.setCreatedDate(subscribed.getCreatedDate());
                purchaseSubscriptionDto.setPaperType(subscribed.getPaperType());
                purchaseSubscriptionDto.setPaperCategory(subscribed.getPaperCategory());
                purchaseSubscriptionDto.setPaperSubCategory(subscribed.getPaperSubCategory());
                purchaseSubscriptionDto.setSubscriptionType(subscribed.getSubscriptionType());
                purchaseSubscriptionDto.setActualPrice(subscribed.getActualPrice());
                purchaseSubscriptionDto.setTestType(subscribed.getTestType());
                purchaseSubscriptionDto.setParchaseStatus(subscribed.getParchaseStatus());
                purchaseSubscriptionDto.setId(subscribed.getId());
                // purchaseSubscription.setValidity(validity);
                purchaseSubscriptionsDtos.add(purchaseSubscriptionDto);
            });
        }
        return purchaseSubscriptionsDtos;
    }

    private PurchaseSubscriptionsDto convertPurcahseSubscriptionDto(PurchaseSubscriptions purchaseSubscriptions) {

        PurchaseSubscriptionsDto purchaseSubscriptionDto = null;
        if (purchaseSubscriptions != null) {
            purchaseSubscriptionDto = new PurchaseSubscriptionsDto();
            purchaseSubscriptionDto.setUserId(purchaseSubscriptions.getUserId());
            purchaseSubscriptionDto.setCreatedDate(purchaseSubscriptions.getCreatedDate());
            purchaseSubscriptionDto.setPaperType(purchaseSubscriptions.getPaperType());
            purchaseSubscriptionDto.setPaperCategory(purchaseSubscriptions.getPaperCategory());
            purchaseSubscriptionDto.setPaperSubCategory(purchaseSubscriptions.getPaperSubCategory());
            purchaseSubscriptionDto.setSubscriptionType(purchaseSubscriptions.getSubscriptionType());
            purchaseSubscriptionDto.setActualPrice(purchaseSubscriptions.getActualPrice());
            purchaseSubscriptionDto.setTestType(purchaseSubscriptions.getTestType());
            purchaseSubscriptionDto.setParchaseStatus(purchaseSubscriptions.getParchaseStatus());
            purchaseSubscriptionDto.setId(purchaseSubscriptions.getId());
        }
        return purchaseSubscriptionDto;
    }

    private PurchaseSubscriptions convertPurcahseSubscriptionModel(PurchaseSubscriptionsDto purchaseSubscriptionsDto,
                                                                   String userId) {

        PurchaseSubscriptions purchaseSubscriptions = null;
        if (purchaseSubscriptionsDto != null) {
            purchaseSubscriptions = new PurchaseSubscriptions();
            purchaseSubscriptions.setUserId(purchaseSubscriptionsDto.getUserId());
            purchaseSubscriptions.setCreatedDate(purchaseSubscriptionsDto.getCreatedDate());
            purchaseSubscriptions.setPaperType(purchaseSubscriptionsDto.getPaperType());
            purchaseSubscriptions.setPaperCategory(purchaseSubscriptionsDto.getPaperCategory());
            purchaseSubscriptions.setPaperSubCategory(purchaseSubscriptionsDto.getPaperSubCategory());
            purchaseSubscriptions.setSubscriptionType(purchaseSubscriptionsDto.getSubscriptionType());
            purchaseSubscriptions.setActualPrice(purchaseSubscriptionsDto.getActualPrice());
            purchaseSubscriptions.setTestType(purchaseSubscriptionsDto.getTestType());
            purchaseSubscriptions.setParchaseStatus(purchaseSubscriptionsDto.getParchaseStatus());
        }
        return purchaseSubscriptions;
    }

    @Override
    public void savePurchaseSubscriptions(String userId, PurchaseSubscriptionsDto purchaseSubscriptionsDto,
                                          PurchaseStatus purchaseStatus) {
        PurchaseSubscriptions purchaseSubscriptions = convertPurcahseSubscriptionModel(purchaseSubscriptionsDto,
                userId);
        if (purchaseSubscriptionsDto.getId() != null) {
            purchaseSubscriptions.setId(purchaseSubscriptionsDto.getId());
        } else {
            purchaseSubscriptions.setId(counterService.increment(CounterEnum.PURCHASESUBSCRIPTION));
        }

        purchaseSubscriptionsRespository.save(purchaseSubscriptions);

    }
}
