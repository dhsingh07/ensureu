package com.book.ensureu.transformer;

import com.book.ensureu.dto.SubscribedDto;
import com.book.ensureu.model.SubscriptionInfo;

public class SubscriptionInfoTransformer {

    public static SubscribedDto toSubscribedDTO(SubscriptionInfo from){

        return SubscribedDto.builder().
               // id(from.getSubscriptionId()).
                subscriptionType(from.getSubscriptionType()).

                build();

    }
}
