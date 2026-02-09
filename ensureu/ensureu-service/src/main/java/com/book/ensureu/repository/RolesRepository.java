package com.book.ensureu.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.book.ensureu.model.Role;

@Repository
public interface RolesRepository extends MongoRepository<Role, String> {

}
