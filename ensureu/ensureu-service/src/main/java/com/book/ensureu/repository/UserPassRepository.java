package com.book.ensureu.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.book.ensureu.model.UserEntitlement;
import com.book.ensureu.model.UserPass;

public interface UserPassRepository extends MongoRepository<UserPass, Long>{

	public List<UserPass> findByActive(boolean active);

	
	
}
