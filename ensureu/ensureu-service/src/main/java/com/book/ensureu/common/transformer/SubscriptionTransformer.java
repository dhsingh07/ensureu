package com.book.ensureu.common.transformer;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.dto.SubscriptionDto;
import com.book.ensureu.model.Subscription;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class SubscriptionTransformer implements Transformer<Subscription, SubscriptionDto> {

    @Override
    public Subscription dtoToModel(SubscriptionDto subscriptionDto) {

        if (subscriptionDto == null) {
            throw new IllegalArgumentException("subscriptionDto cannot be null");
        }
        // If created date is missing, use current date
        Date createdDate = subscriptionDto.getCrDate() != null ? subscriptionDto.getCrDate()
                : new Date();

        // If validity is missing, default to 1 month from created date
        long validityMillis;
        if (subscriptionDto.getValidity() != null) {
            validityMillis = subscriptionDto.getValidity();
        } else {
            validityMillis = Date.from(
                    createdDate.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .plusMonths(1)
                            .toInstant()
            ).getTime();
        }

        return Subscription.builder()
                .amendmentNo(0)
                .id(subscriptionDto.getId())
                .createdDate(createdDate.getTime())
                .description(subscriptionDto.getDescription())
                .paperIds(subscriptionDto.getPaperIds())
                .listOfPaperInfo(subscriptionDto.getPaperInfoList())
                .paperType(PaperType.valueOf(subscriptionDto.getPaperType()))
                .paperCategory(PaperCategory.valueOf(subscriptionDto.getPaperCategory()))
                .paperSubCategory(PaperSubCategory.valueOf(subscriptionDto.getPaperSubCategory()))
                .testType(TestType.valueOf(subscriptionDto.getTestType()))
                .validity(validityMillis)
                .subscriptionId(subscriptionDto.getId())
                .state(subscriptionDto.getState())
                .build();
    }

    @Override
    public SubscriptionDto modelToDto(Subscription subscription) {
         return SubscriptionDto.builder()
                 .description(subscription.getDescription())
                 .id(subscription.getId())
                 .paperSubCategory(subscription.getPaperSubCategory().name())
                 .paperCategory(subscription.getPaperCategory().name())
                 .testType(subscription.getTestType().toString())
                 .paperType(subscription.getPaperType().name())
                 .paperIds(subscription.getPaperIds())
                 .paperInfoList(subscription.getListOfPaperInfo())
                 .validity(subscription.getValidity())
                 .crDate(new Date(subscription.getCreatedDate()))
                 .state(subscription.getState())
                 .build();
    }
}
