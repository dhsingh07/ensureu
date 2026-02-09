package com.book.ensureu.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.book.ensureu.model.Customer;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {
	List<Customer> findByLastName(String lastName);
}