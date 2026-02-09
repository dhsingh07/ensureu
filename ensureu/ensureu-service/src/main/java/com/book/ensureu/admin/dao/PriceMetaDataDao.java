package com.book.ensureu.admin.dao;

import com.book.ensureu.common.dto.PriceMetaDataDto;
import com.book.ensureu.common.model.PriceMetaData;
import com.book.ensureu.common.transformer.PriceMetaDataTransformer;
import com.book.ensureu.constant.CounterEnum;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.SubscriptionType;
import com.book.ensureu.model.Subscription;
import com.book.ensureu.service.CounterService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
@Component
public class PriceMetaDataDao {

    private MongoTemplate mongoTemplate;

    private PriceMetaDataTransformer priceMetaDataTransformer;

    private CounterService counterService;

    public List<PriceMetaDataDto> getPriceMetaData(PaperSubCategory paperSubCategory){

        log.debug("[getPriceMetaData] paperSubCategory : {}",paperSubCategory);
        Query query = Query.query(Criteria.where("paperSubCategory").is(paperSubCategory));
        List<PriceMetaData> priceMetaDataList = mongoTemplate.find(query,PriceMetaData.class);
        return priceMetaDataTransformer.modelToDto(priceMetaDataList);
    }

    public void savePriceMetaData(PriceMetaDataDto priceMetaDataDto){
        log.debug("[savePriceMetaData] priceMetaDataDto : {}",priceMetaDataDto);
        PriceMetaData priceMetaData = priceMetaDataTransformer.dtoToModel(priceMetaDataDto);
        long id = counterService.increment(CounterEnum.PRICEMETADATA);
        priceMetaData.setId(id);
        mongoTemplate.save(priceMetaData);
    }

    public void patchPriceMetaData(PriceMetaDataDto priceMetaDataDto){

        Long id = priceMetaDataDto.getId();
        PriceMetaData priceMetaData = mongoTemplate.findOne(Query.query(Criteria.where("id").is(id)), PriceMetaData.class);
        if(Objects.isNull(priceMetaData)){

        }
        priceMetaData = priceMetaDataTransformer.dtoToModel(priceMetaDataDto);
        mongoTemplate.save(priceMetaData);

    }

}
