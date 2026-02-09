package com.book.ensureu.admin.service;

import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.dto.SubscriptionDto;

import java.util.List;

public interface SubscriptionService {

    List<SubscriptionDto> fetchSubscription(PaperSubCategory paperSubCategory, Long crDate);

    void createSubscription(SubscriptionDto subscriptionDto);

    SubscriptionDto patchSubscription(SubscriptionDto subscriptionDto);
}
