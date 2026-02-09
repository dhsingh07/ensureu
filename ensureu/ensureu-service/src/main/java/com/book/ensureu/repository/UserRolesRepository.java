package com.book.ensureu.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.book.ensureu.model.UserRole;

@Repository
public interface UserRolesRepository extends MongoRepository<UserRole, String> {

}
