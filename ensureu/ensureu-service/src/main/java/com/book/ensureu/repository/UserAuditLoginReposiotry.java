package com.book.ensureu.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.book.ensureu.model.UserAuditLogin;

@Repository
public interface UserAuditLoginReposiotry extends MongoRepository<UserAuditLogin, Long> {

	public List<UserAuditLogin> findByUserId(String userId);
}
