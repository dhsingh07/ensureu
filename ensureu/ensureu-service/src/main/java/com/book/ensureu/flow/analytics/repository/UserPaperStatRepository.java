package com.book.ensureu.flow.analytics.repository;

import com.book.ensureu.constant.TestType;
import com.book.ensureu.exception.unchecked.EntityNotFound;
import com.book.ensureu.flow.analytics.model.UserPaperStat;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;


@Repository
@AllArgsConstructor
@Slf4j
public class UserPaperStatRepository {

    private MongoTemplate mongoTemplate;

    public UserPaperStat fetchUserPaperStat(String userId, String paperId) {
        Query query = new Query(Criteria.where("userId").is(userId).and("paperId").is(paperId));
        List<UserPaperStat> paperStatList = mongoTemplate.find(query, UserPaperStat.class);

        if (Objects.isNull(paperStatList) || paperStatList.isEmpty()){
            log.error("[fetchUserPaperStat] User [{}] has not given paper: {}", userId, paperId);
            throw new EntityNotFound("User has not given paper: " + paperId);
        }

        return paperStatList.get(0);
    }

    public List<UserPaperStat> fetchUserPaperStat(String userId, String topperId, TestType testType) {

        Criteria cr1 = Criteria.where("userId").in(userId,topperId).and("testType").is(testType);
        Query query = new Query(cr1);
        List<UserPaperStat> paperStatList = mongoTemplate.find(query, UserPaperStat.class);

        if (Objects.isNull(paperStatList) || paperStatList.isEmpty()) {
            log.error("[fetchUserPaperStat] User [{}] has not given testType: {}", userId,testType);
            throw new EntityNotFound("User has not given testType: " + testType);
        }

        return paperStatList;
    }

    public List<UserPaperStat> fetchUserPaperStat(String userId, String topperId, String paperId) {

        Criteria cr1 = Criteria.where("userId").in(userId,topperId).and("paperId").is(paperId);
        Query query = new Query(cr1);
        List<UserPaperStat> paperStatList = mongoTemplate.find(query, UserPaperStat.class);

        if (Objects.isNull(paperStatList) || paperStatList.isEmpty()) {
            log.error("[fetchUserPaperStat] User [{}] has not given paper: {}", userId, paperId);
            throw new EntityNotFound("User has not given paper: " + paperId);
        }

        return paperStatList;
    }

    public void saveUserPaperStat(UserPaperStat userPaperStat) {
        //userPaperStat
        //TODO : need to add unique id
        mongoTemplate.save(userPaperStat);
    }

    public List<UserPaperStat> fetchPreviousPaper(String userId, Integer numOfPaper){
        Query query = new Query(Criteria.where("userId").is(userId)).limit(numOfPaper).with(Sort.by(Sort.Direction.DESC,"createdAt"));
        return mongoTemplate.find(query,UserPaperStat.class);
    }
}
