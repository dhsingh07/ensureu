package com.book.ensureu.flow.analytics.repository;

import com.book.ensureu.exception.unchecked.EntityNotFound;
import com.book.ensureu.flow.analytics.model.UserPaperTimeSeries;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
@AllArgsConstructor
public class UserPaperTimeSeriesRepository {

    private MongoTemplate mongoTemplate;

    /**
     * Throws EntityNotFound Exception when no data found
     */
    public UserPaperTimeSeries fetchUserPaperTimeSeries(String userId, String paperId) {
        Query query = new Query(Criteria.where("userId").is(userId).and("paperId").is(paperId));
        List<UserPaperTimeSeries> userPaperTimeSeriesList = mongoTemplate.find(query, UserPaperTimeSeries.class);
        if (Objects.isNull(userPaperTimeSeriesList) || userPaperTimeSeriesList.isEmpty()) {
            //throw new EntityNotFound("User has not given this paper");
            // TODO need to throw exception
            return null;
        }

        return userPaperTimeSeriesList.get(0);

    }

    public void saveUserPaperTimeSeries(UserPaperTimeSeries userPaperTimeSeries) {
        mongoTemplate.save(userPaperTimeSeries);
    }
}
