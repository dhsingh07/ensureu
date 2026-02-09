package com.book.ensureu.repository;

import com.book.ensureu.model.PurchaseTestSeries;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchasedTestSeriesRepository extends MongoRepository<PurchaseTestSeries,String> {

     Optional<PurchaseTestSeries> findByUserIdAndAndTestSeriesId(String userId, String testSeriesId);


     List<PurchaseTestSeries> findByUserIdAndCreatedDateLongAfterAndCreatedDateLongBefore(String userId,long after,long before);
}
