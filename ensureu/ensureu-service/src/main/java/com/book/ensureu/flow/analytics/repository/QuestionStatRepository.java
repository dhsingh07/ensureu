package com.book.ensureu.flow.analytics.repository;

import com.book.ensureu.flow.analytics.model.QuestionStat;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class QuestionStatRepository {

    private MongoTemplate mongoTemplate;

    public List<QuestionStat> fetchQuestionStatByIdIn(List<String> questionIds){

        Query query = new Query(Criteria.where("questionId").in(questionIds));
        return mongoTemplate.find(query,QuestionStat.class);
    }

    public void saveQuestionStat(List<QuestionStat> questionStatList){
        mongoTemplate.save(questionStatList);
    }
}
