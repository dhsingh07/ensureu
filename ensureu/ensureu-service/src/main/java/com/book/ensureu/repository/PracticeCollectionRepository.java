package com.book.ensureu.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.book.ensureu.model.PracticePaperCollection;

/**
 * @author jatin.bansal
 *
 */
@Repository
public interface PracticeCollectionRepository extends MongoRepository<PracticePaperCollection, String> {

}
