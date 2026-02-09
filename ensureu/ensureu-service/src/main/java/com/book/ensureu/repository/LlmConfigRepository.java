package com.book.ensureu.repository;

import com.book.ensureu.admin.model.LlmConfig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LlmConfigRepository extends MongoRepository<LlmConfig, String> {

    /**
     * Find the active LLM configuration
     */
    Optional<LlmConfig> findByIsActiveTrue();
}
