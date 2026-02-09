package com.book.ensureu.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.book.ensureu.admin.constant.DifficultyLevel;
import com.book.ensureu.admin.constant.QuestionBankStatus;
import com.book.ensureu.admin.model.QuestionBank;
import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;

@Repository
public interface QuestionBankRepository extends MongoRepository<QuestionBank, String> {

    Optional<QuestionBank> findByQuestionId(String questionId);

    Page<QuestionBank> findByStatus(QuestionBankStatus status, Pageable pageable);

    Page<QuestionBank> findByCreatedBy(String createdBy, Pageable pageable);

    Page<QuestionBank> findByCreatedByAndStatus(String createdBy, QuestionBankStatus status, Pageable pageable);

    Page<QuestionBank> findByPaperType(PaperType paperType, Pageable pageable);

    Page<QuestionBank> findByPaperCategory(PaperCategory paperCategory, Pageable pageable);

    Page<QuestionBank> findByPaperSubCategory(PaperSubCategory paperSubCategory, Pageable pageable);

    Page<QuestionBank> findBySubject(String subject, Pageable pageable);

    Page<QuestionBank> findByTopic(String topic, Pageable pageable);

    Page<QuestionBank> findByDifficultyLevel(DifficultyLevel difficultyLevel, Pageable pageable);

    // Complex queries
    @Query("{ 'paperCategory': ?0, 'subject': ?1, 'status': ?2 }")
    Page<QuestionBank> findByCategorySubjectAndStatus(
            PaperCategory paperCategory,
            String subject,
            QuestionBankStatus status,
            Pageable pageable);

    @Query("{ 'paperSubCategory': ?0, 'subject': ?1, 'status': 'APPROVED' }")
    List<QuestionBank> findApprovedBySubCategoryAndSubject(
            PaperSubCategory paperSubCategory,
            String subject);

    @Query("{ 'paperSubCategory': ?0, 'subject': ?1, 'difficultyLevel': ?2, 'status': 'APPROVED' }")
    List<QuestionBank> findApprovedBySubCategorySubjectAndDifficulty(
            PaperSubCategory paperSubCategory,
            String subject,
            DifficultyLevel difficultyLevel);

    // Count queries
    long countByStatus(QuestionBankStatus status);

    long countByCreatedBy(String createdBy);

    long countByCreatedByAndStatus(String createdBy, QuestionBankStatus status);

    long countBySubject(String subject);

    long countByDifficultyLevel(DifficultyLevel difficultyLevel);

    long countByPaperCategory(PaperCategory paperCategory);

    // Find distinct values for filters
    @Query(value = "{}", fields = "{ 'subject': 1 }")
    List<QuestionBank> findDistinctSubjects();

    @Query(value = "{ 'subject': ?0 }", fields = "{ 'topic': 1 }")
    List<QuestionBank> findDistinctTopicsBySubject(String subject);

    // Text search
    @Query("{ '$text': { '$search': ?0 } }")
    Page<QuestionBank> searchByText(String searchText, Pageable pageable);

    // Random questions for paper generation
    @Query("{ 'paperSubCategory': ?0, 'subject': ?1, 'status': 'APPROVED' }")
    List<QuestionBank> findRandomApprovedQuestions(
            PaperSubCategory paperSubCategory,
            String subject);
}
