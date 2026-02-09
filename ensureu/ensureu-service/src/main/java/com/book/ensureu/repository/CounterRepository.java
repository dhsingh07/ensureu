package com.book.ensureu.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.book.ensureu.model.Counter;

/**
 * @author dharmendra.singh
 *
 */
@Repository
public interface CounterRepository extends MongoRepository<Counter, String> {

}
