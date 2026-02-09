package com.book.ensureu.repository;

import com.book.ensureu.common.model.TestSeries;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestSeriesRepository extends MongoRepository<TestSeries,String> {

    @Query(" { 'validity' : { '$lte' : ?0 }, 'active' : ?1 } ")
    Optional<List<TestSeries>> findTestSeriesByValidity(long validity, boolean active);

    List<TestSeries> findByUuidIn(List<String> testSeriesIdList);


}
