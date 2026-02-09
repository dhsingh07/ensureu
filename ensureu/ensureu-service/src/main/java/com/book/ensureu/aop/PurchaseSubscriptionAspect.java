package com.book.ensureu.aop;

import com.book.ensureu.constant.CounterEnum;
import com.book.ensureu.constant.PurchaseStatus;
import com.book.ensureu.dto.SubscribedDto;
import com.book.ensureu.model.PurchaseSubscriptions;
import com.book.ensureu.model.UserEntitlement;
import com.book.ensureu.repository.PurchaseSubscriptionsRespository;
import com.book.ensureu.service.CounterService;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Aspect
@Component
@Slf4j
public class PurchaseSubscriptionAspect {

    @Autowired
    private PurchaseSubscriptionsRespository purchaseSubscriptionsRespository;

    @Autowired
    private CounterService counterService;

    @Value("${day.milliseconds:86400000}")
    private Long milliseconds;

    /**
     * Around AOP is used as we need to update purchaseSubscription in all scenarios
     * like +ve, failed and before also
     */
    @Around("@annotation(com.book.ensureu.annotation.PurchaseSubscription) && args(userId,subscribedDto,paperId,userLastEntitlement)")
    public Object purchaseSubscription(JoinPoint joinPoint, String userId, SubscribedDto subscribedDto, String paperId,
                                       UserEntitlement userLastEntitlement ){

        log.info("[purchaseSubscription] called for userId [{}]",userId);
        Object value = null;
        PurchaseSubscriptions purchaseSubscriptionObj  = saveOrUpdatePurchaseSubscriptions(userId, subscribedDto, null,
                PurchaseStatus.INPROGRESS,userLastEntitlement);
        try {
            value = ((ProceedingJoinPoint)joinPoint).proceed();
            saveOrUpdatePurchaseSubscriptions(userId, subscribedDto, purchaseSubscriptionObj, PurchaseStatus.COMPLETED,userLastEntitlement);

        } catch (Throwable throwable) {
            saveOrUpdatePurchaseSubscriptions(userId, subscribedDto, purchaseSubscriptionObj, PurchaseStatus.FAILED,userLastEntitlement);
            return throwable;
        }
        return value;
    }

    private PurchaseSubscriptions saveOrUpdatePurchaseSubscriptions(String userId, SubscribedDto subscribedDto,
                                                                    PurchaseSubscriptions purchaseSubscriptions, PurchaseStatus purchaseStatus, UserEntitlement userLastEntitlement) {
        PurchaseSubscriptions purchaseSubscriptionObj = null;
        if (purchaseSubscriptions != null && purchaseSubscriptions.getId() != null) {
            purchaseSubscriptions.setModifiedDate(new Date().getTime());
            purchaseSubscriptions.setParchaseStatus(purchaseStatus);
            purchaseSubscriptionObj = purchaseSubscriptionsRespository.save(purchaseSubscriptions);
        } else {
            if (subscribedDto != null && userId != null) {
                purchaseSubscriptionObj = new PurchaseSubscriptions();
                Long createdDate = System.currentTimeMillis();
                Double daysToAdd = subscribedDto.getSubscriptionType().getVal();
                Long milliSecToAdd = 1l;
                milliSecToAdd = (long) ((milliSecToAdd * 30 * milliseconds) * daysToAdd + createdDate);
                purchaseSubscriptionObj.setUserId(userId);
                purchaseSubscriptionObj.setCreatedDate(new Date().getTime());
                purchaseSubscriptionObj.setPaperType(subscribedDto.getPaperType());
                purchaseSubscriptionObj.setPaperCategory(subscribedDto.getPaperCategory());
                purchaseSubscriptionObj.setPaperSubCategory(subscribedDto.getPaperSubCategory());
                purchaseSubscriptionObj.setSubscriptionType(subscribedDto.getSubscriptionType());
                purchaseSubscriptionObj.setActualPrice(0D);
                purchaseSubscriptionObj.setTestType(subscribedDto.getTestType());
                purchaseSubscriptionObj.setParchaseStatus(purchaseStatus);
                purchaseSubscriptionObj.setValidity(milliSecToAdd);
                purchaseSubscriptionObj.setId(counterService.increment(CounterEnum.PURCHASESUBSCRIPTION));
                purchaseSubscriptionObj = purchaseSubscriptionsRespository.save(purchaseSubscriptionObj);
            }
        }
        return purchaseSubscriptionObj;
    }
}
