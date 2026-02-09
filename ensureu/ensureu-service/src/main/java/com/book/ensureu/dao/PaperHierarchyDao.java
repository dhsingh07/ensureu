package com.book.ensureu.dao;

import com.book.ensureu.dto.SubscribedDto;
import com.book.ensureu.exception.unchecked.EntityNotFound;
import com.book.ensureu.model.PaperHierarchy;
import com.book.ensureu.model.Subscription;
import com.book.ensureu.repository.PaperHierarchyRepository;
import com.book.ensureu.repository.SubscriptionRepository;
import com.book.ensureu.service.impl.helper.SubscriptionServiceImplHelper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@AllArgsConstructor
public class PaperHierarchyDao {

    private PaperHierarchyRepository paperHierarchyRepository;

    private SubscriptionRepository subscriptionRepository;

    private SubscriptionServiceImplHelper implHelper;

    public PaperHierarchy fetchPaperHierarchyById(String paperHierarchyId) {
        return paperHierarchyRepository.findById(paperHierarchyId).
                orElseThrow(
                        () -> new EntityNotFound("PaperHierarchy not found :Invalid paperHierarchyId :" + paperHierarchyId));
    }


    public SubscribedDto fetchSubscriptionByPaperHierarchyId(String paperHierarchyId) {

        PaperHierarchy paperHierarchy = paperHierarchyRepository.findById(paperHierarchyId).
                orElseThrow(
                        () -> new EntityNotFound("PaperHierarchy not found :Invalid paperHierarchyId :" + paperHierarchyId));

       return fetchSubscriptionByPaperHierarchy(paperHierarchy);
    }

    public SubscribedDto fetchSubscriptionByPaperHierarchy(PaperHierarchy paperHierarchy) {

        long time = new Date().getTime();
        List<Subscription> subscriptionList = subscriptionRepository.findByPaperSubCategoryAndTestTypeOrder(paperHierarchy.getPaperSubCategory(),
                paperHierarchy.getTestType(), time, time);

        SubscribedDto subscribedDto = null;
        if (subscriptionList != null && !subscriptionList.isEmpty())
            subscribedDto = implHelper.convertToSubscribedDto(subscriptionList.get(0));
        return subscribedDto;
    }
}
