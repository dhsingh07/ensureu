package com.book.ensureu.admin.dao;

import com.book.ensureu.admin.constant.AdminConstant;
import com.book.ensureu.admin.service.PaidPaperCollectionService;
import com.book.ensureu.common.transformer.SubscriptionTransformer;
import com.book.ensureu.constant.CounterEnum;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.dto.PaperInfo;
import com.book.ensureu.dto.SubscriptionDto;
import com.book.ensureu.exception.RuntimeEUException;
import com.book.ensureu.model.Subscription;
import com.book.ensureu.service.CounterService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Component
public class SubscriptionDao {

    private MongoTemplate mongoTemplate;

    private SubscriptionTransformer subscriptionTransformer;

    private CounterService counterService;

    private PaidPaperCollectionService paidPaperCollectionService;

    public List<SubscriptionDto> fetchSubscription(PaperSubCategory paperSubCategory, Long crDate){

        crDate = (null!=crDate) ? crDate : Instant.now().minusMillis(AdminConstant.monthMilliSeconds*2).getEpochSecond();
        Criteria cdDateCriteria = Criteria.where("createdDate").gte(crDate);
        Criteria subCategoryCriteria = Criteria.where("paperSubCategory").is(paperSubCategory);
        subCategoryCriteria.andOperator(cdDateCriteria);

        Query query = Query.query(subCategoryCriteria);
        List<Subscription>  subscriptionList = mongoTemplate.find(query, Subscription.class);
        return subscriptionTransformer.modelToDto(subscriptionList);

    }

    /**
     * @apiNote Based upon Subscription state as DRAFT or ACTIVE
     * Paper's taken flag is set. In ACTIVE flag is set to true or vice-versa
     *
     */
    public void createSubscription(SubscriptionDto subscriptionDto) {
        // Fetch available paper infos
        List<PaperInfo> paperInfos = Optional.ofNullable(
                paidPaperCollectionService.fetchFreshPaperInfoList(subscriptionDto.getPaperIds())
        ).orElseGet(Collections::emptyList);

        // Only keep paperIds that are actually available
        Set<String> availableIds = paperInfos.stream()
                .map(PaperInfo::getId)
                .collect(Collectors.toSet());

        List<String> validPaperIds = subscriptionDto.getPaperIds().stream()
                .filter(availableIds::contains)
                .collect(Collectors.toList());

        subscriptionDto.setPaperIds(validPaperIds);  // keep only valid IDs
        subscriptionDto.setPaperInfoList(paperInfos); // already filtered list

        // Build subscription
        Subscription subscription = subscriptionTransformer.dtoToModel(subscriptionDto);

        long id = counterService.increment(CounterEnum.SUBSCRIPTION);
        subscription.setId(id);

        log.debug("[createSubscription] subscription {}", subscription);

        // Mark as taken if active
        if (subscription.getState().equals(Subscription.SubscriptionState.ACTIVE)) {
            paidPaperCollectionService.setTakenPaidPaperCollectionFlag(validPaperIds, true);
        }

        mongoTemplate.save(subscription);
    }


    /**
     * @apiNote Based upon Subscription state as DRAFT or ACTIVE
     * paidPaper taken flag are changed, if state changed from ACTIVE to DRAFT
     * all paper's taken flag is set to false. In DRAFT state paper flag is not set
     *
      */
    public SubscriptionDto patchSubscription(SubscriptionDto subscriptionDto){

        Long id = subscriptionDto.getId();
        Subscription subscription = mongoTemplate.findOne(Query.query(Criteria.where("id").is(id)),Subscription.class);
        if(Objects.isNull(subscription)){
            log.error("[patchSubscription] failed subscriptionDto {}",subscriptionDto);
            throw new RuntimeEUException("Bad Request Invalid SubscriptionId");
        }
        if(subscription.getState().equals(Subscription.SubscriptionState.ACTIVE)){
            List<String> paperIds = subscription.getListOfPaperInfo()
                    .stream()
                    .map(PaperInfo::getId)
                    .collect(Collectors.toList());
            paidPaperCollectionService.setTakenPaidPaperCollectionFlag(paperIds,false);

        }

        subscription = subscriptionTransformer.dtoToModel(subscriptionDto);
        if(subscription.getState().equals(Subscription.SubscriptionState.ACTIVE)){
            List<String> paperIds = subscription.getListOfPaperInfo()
                    .stream()
                    .map(PaperInfo::getId)
                    .collect(Collectors.toList());
            paidPaperCollectionService.setTakenPaidPaperCollectionFlag(paperIds,true);
        }
        mongoTemplate.save(subscription);
        return subscriptionDto;
    }


}
