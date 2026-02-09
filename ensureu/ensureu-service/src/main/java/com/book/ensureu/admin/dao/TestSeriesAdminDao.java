package com.book.ensureu.admin.dao;

import com.book.ensureu.common.model.TestSeries;
import com.book.ensureu.common.transformer.TestSeriesTransformer;
import com.book.ensureu.constant.CounterEnum;
import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.common.dto.TestSeriesDto;
import com.book.ensureu.service.CounterService;
import com.book.ensureu.util.HashUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
@Component
public class TestSeriesAdminDao {

    private MongoTemplate mongoTemplate;

    private CounterService counterService;


    public List<TestSeriesDto> getTestSeries(PaperCategory paperCategory, Date crDate, Date validity){

        Criteria paperSubCategoryCriteria = Criteria.where("paperCategory").is(paperCategory);
        Criteria crDateCriteria = Criteria.where("crDate").gte(crDate);
        Criteria validityCriteria = Criteria.where("validity").gte(validity.getTime());

        paperSubCategoryCriteria.andOperator(crDateCriteria,validityCriteria);
        List<TestSeries> testSeriesList = mongoTemplate.find(Query.query(paperSubCategoryCriteria), TestSeries.class);

        return TestSeriesTransformer.toDTOs(testSeriesList);
    }

    public void createTestSeries(TestSeriesDto testSeriesDto){

        TestSeries testSeries = TestSeriesTransformer.dtoToModel(testSeriesDto);
        long id = counterService.increment(CounterEnum.TEST_SERIES);
        testSeries.setId(id);
        testSeries.setUuid(HashUtil.hashByMD5(String.valueOf(id)));
        mongoTemplate.save(testSeries);
    }

    public void patchTestSeries(TestSeriesDto testSeriesDto){


        TestSeries testSeries = mongoTemplate.findOne(Query.query(Criteria.where("uuid").is(testSeriesDto.getUuid())), TestSeries.class);

        if(Objects.isNull(testSeries)){
            log.error("[patchTestSeries] invalid UUId {}",testSeriesDto.getUuid());
        }
        long id = testSeries.getId();
        testSeries = TestSeriesTransformer.dtoToModel(testSeriesDto);
        mongoTemplate.save(testSeries);

    }

}
