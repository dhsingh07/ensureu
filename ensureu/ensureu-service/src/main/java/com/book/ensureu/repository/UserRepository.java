package com.book.ensureu.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.book.ensureu.model.User;

/**
 * @author dharmendra.singh
 *
 */
@Repository
public interface UserRepository extends MongoRepository<User, Long> {

	public Optional<User> findById(Long id);

	public Optional<User> findByUserName(String userName);
	public Optional<User> findByEmailId(String EmialId);
	public Optional<User> findByMobileNumber(String mobileNumber);

	@Query("{ '$or': [ "
			+ "{ 'firstName': { '$regex': ?0, '$options': 'i' } }, "
			+ "{ 'lastName': { '$regex': ?0, '$options': 'i' } }, "
			+ "{ 'emailId': { '$regex': ?0, '$options': 'i' } }, "
			+ "{ 'userName': { '$regex': ?0, '$options': 'i' } } "
			+ "] }")
	Page<User> searchUsers(String searchTerm, Pageable pageable);
}
