package com.book.ensureu.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.book.ensureu.model.ServiceCallTrace;

@Repository
public interface ServiceCallTraceRepository extends MongoRepository<ServiceCallTrace, String> {

}
