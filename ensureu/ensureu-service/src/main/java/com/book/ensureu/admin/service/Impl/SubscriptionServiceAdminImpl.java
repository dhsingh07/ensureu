package com.book.ensureu.admin.service.Impl;

import com.book.ensureu.admin.dao.SubscriptionDao;
import com.book.ensureu.admin.service.SubscriptionService;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.dto.SubscriptionDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class SubscriptionServiceAdminImpl implements SubscriptionService {

    private SubscriptionDao subscriptionDao;

    @Override
    public List<SubscriptionDto> fetchSubscription(PaperSubCategory paperSubCategory, Long crDate) {
        log.debug("[fetchSubscription] paperSubCategory {}, crDate {}",paperSubCategory,crDate);
        return subscriptionDao.fetchSubscription(paperSubCategory, crDate);
    }

    @Override
    public void createSubscription(SubscriptionDto subscriptionDto) {

        log.debug("[createSubscription] subscriptionDto {}",subscriptionDto);
        subscriptionDao.createSubscription(subscriptionDto);

    }

    @Override
    public SubscriptionDto patchSubscription(SubscriptionDto subscriptionDto) {
        log.debug("[createSubscription] subscriptionDto {}",subscriptionDto);
        return subscriptionDao.patchSubscription(subscriptionDto);
    }
}
