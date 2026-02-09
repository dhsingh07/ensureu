package com.book.ensureu.admin.service.Impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import com.book.ensureu.admin.constant.DifficultyLevel;
import com.book.ensureu.admin.constant.QuestionBankStatus;
import com.book.ensureu.admin.dto.QuestionBankCreateDto;
import com.book.ensureu.admin.dto.QuestionBankDto;
import com.book.ensureu.admin.dto.QuestionBankStatsDto;
import com.book.ensureu.admin.model.QuestionBank;
import com.book.ensureu.admin.service.QuestionBankService;
import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.repository.QuestionBankRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class QuestionBankServiceImpl implements QuestionBankService {

    @Autowired
    private QuestionBankRepository questionBankRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public QuestionBankDto create(QuestionBankCreateDto createDto, String userId, String userName) {
        log.info("Creating question by user: {}", userId);

        QuestionBank question = QuestionBank.builder()
                .questionId(generateQuestionId())
                .paperType(createDto.getPaperType())
                .paperCategory(createDto.getPaperCategory())
                .paperSubCategory(createDto.getPaperSubCategory())
                .subject(createDto.getSubject())
                .topic(createDto.getTopic())
                .subTopic(createDto.getSubTopic())
                .problem(createDto.getProblem())
                .questionType(createDto.getQuestionType())
                .difficultyLevel(createDto.getDifficultyLevel())
                .marks(createDto.getMarks() != null ? createDto.getMarks() : 2.0)
                .negativeMarks(createDto.getNegativeMarks() != null ? createDto.getNegativeMarks() : 0.5)
                .averageTime(createDto.getAverageTime() != null ? createDto.getAverageTime() : 60)
                .hasImage(createDto.getHasImage())
                .imageUrl(createDto.getImageUrl())
                .imagePosition(createDto.getImagePosition())
                .tags(createDto.getTags())
                .year(createDto.getYear())
                .source(createDto.getSource())
                .status(Boolean.TRUE.equals(createDto.getSubmitForReview())
                        ? QuestionBankStatus.PENDING_REVIEW
                        : QuestionBankStatus.DRAFT)
                .createdBy(userId)
                .createdByName(userName)
                .createdAt(System.currentTimeMillis())
                .usageCount(0)
                .build();

        QuestionBank saved = questionBankRepository.save(question);
        log.info("Question created with id: {}, questionId: {}", saved.getId(), saved.getQuestionId());

        return toDto(saved);
    }

    @Override
    public QuestionBankDto update(String id, QuestionBankCreateDto updateDto, String userId) {
        log.info("Updating question: {} by user: {}", id, userId);

        QuestionBank question = questionBankRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found: " + id));

        // Only allow creator or admin to update
        // Note: Role check should be done at API layer

        question.setPaperType(updateDto.getPaperType());
        question.setPaperCategory(updateDto.getPaperCategory());
        question.setPaperSubCategory(updateDto.getPaperSubCategory());
        question.setSubject(updateDto.getSubject());
        question.setTopic(updateDto.getTopic());
        question.setSubTopic(updateDto.getSubTopic());
        question.setProblem(updateDto.getProblem());
        question.setQuestionType(updateDto.getQuestionType());
        question.setDifficultyLevel(updateDto.getDifficultyLevel());
        question.setMarks(updateDto.getMarks());
        question.setNegativeMarks(updateDto.getNegativeMarks());
        question.setAverageTime(updateDto.getAverageTime());
        question.setHasImage(updateDto.getHasImage());
        question.setImageUrl(updateDto.getImageUrl());
        question.setImagePosition(updateDto.getImagePosition());
        question.setTags(updateDto.getTags());
        question.setYear(updateDto.getYear());
        question.setSource(updateDto.getSource());
        question.setUpdatedBy(userId);
        question.setUpdatedAt(System.currentTimeMillis());

        // If submitting for review, update status
        if (Boolean.TRUE.equals(updateDto.getSubmitForReview())
                && question.getStatus() == QuestionBankStatus.DRAFT) {
            question.setStatus(QuestionBankStatus.PENDING_REVIEW);
        }

        QuestionBank saved = questionBankRepository.save(question);
        log.info("Question updated: {}", saved.getId());

        return toDto(saved);
    }

    @Override
    public QuestionBankDto getById(String id) {
        return questionBankRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Question not found: " + id));
    }

    @Override
    public QuestionBankDto getByQuestionId(String questionId) {
        return questionBankRepository.findByQuestionId(questionId)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Question not found: " + questionId));
    }

    @Override
    public void delete(String id, String userId) {
        log.info("Deleting question: {} by user: {}", id, userId);

        QuestionBank question = questionBankRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found: " + id));

        // Only creator can delete DRAFT, Admin can delete any
        // Role check should be done at API layer

        if (question.getUsageCount() != null && question.getUsageCount() > 0) {
            // Don't delete questions that are used in papers, archive instead
            question.setStatus(QuestionBankStatus.ARCHIVED);
            question.setUpdatedBy(userId);
            question.setUpdatedAt(System.currentTimeMillis());
            questionBankRepository.save(question);
            log.info("Question archived (in use): {}", id);
        } else {
            questionBankRepository.deleteById(id);
            log.info("Question deleted: {}", id);
        }
    }

    @Override
    public Page<QuestionBankDto> list(
            PaperType paperType,
            PaperCategory paperCategory,
            PaperSubCategory paperSubCategory,
            String subject,
            String topic,
            DifficultyLevel difficultyLevel,
            QuestionBankStatus status,
            String createdBy,
            Pageable pageable) {

        Query query = new Query();

        if (paperType != null) {
            query.addCriteria(Criteria.where("paperType").is(paperType));
        }
        if (paperCategory != null) {
            query.addCriteria(Criteria.where("paperCategory").is(paperCategory));
        }
        if (paperSubCategory != null) {
            query.addCriteria(Criteria.where("paperSubCategory").is(paperSubCategory));
        }
        if (subject != null && !subject.isEmpty()) {
            query.addCriteria(Criteria.where("subject").is(subject));
        }
        if (topic != null && !topic.isEmpty()) {
            query.addCriteria(Criteria.where("topic").is(topic));
        }
        if (difficultyLevel != null) {
            query.addCriteria(Criteria.where("difficultyLevel").is(difficultyLevel));
        }
        if (status != null) {
            query.addCriteria(Criteria.where("status").is(status));
        }
        if (createdBy != null && !createdBy.isEmpty()) {
            query.addCriteria(Criteria.where("createdBy").is(createdBy));
        }

        query.with(pageable);

        var results = mongoTemplate.find(query, QuestionBank.class);
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), QuestionBank.class);

        return PageableExecutionUtils.getPage(
                results.stream().map(this::toDto).toList(),
                pageable,
                () -> total);
    }

    @Override
    public Page<QuestionBankDto> listByUser(String userId, QuestionBankStatus status, Pageable pageable) {
        Page<QuestionBank> page;
        if (status != null) {
            page = questionBankRepository.findByCreatedByAndStatus(userId, status, pageable);
        } else {
            page = questionBankRepository.findByCreatedBy(userId, pageable);
        }
        return page.map(this::toDto);
    }

    @Override
    public QuestionBankDto submitForReview(String id, String userId) {
        log.info("Submitting question for review: {} by user: {}", id, userId);

        QuestionBank question = questionBankRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found: " + id));

        if (!question.getCreatedBy().equals(userId)) {
            throw new RuntimeException("Only creator can submit for review");
        }

        if (question.getStatus() != QuestionBankStatus.DRAFT
                && question.getStatus() != QuestionBankStatus.REJECTED) {
            throw new RuntimeException("Only DRAFT or REJECTED questions can be submitted for review");
        }

        question.setStatus(QuestionBankStatus.PENDING_REVIEW);
        question.setUpdatedBy(userId);
        question.setUpdatedAt(System.currentTimeMillis());
        question.setRejectionReason(null); // Clear rejection reason

        QuestionBank saved = questionBankRepository.save(question);
        return toDto(saved);
    }

    @Override
    public QuestionBankDto approve(String id, String approverId, String approverName) {
        log.info("Approving question: {} by: {}", id, approverId);

        QuestionBank question = questionBankRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found: " + id));

        if (question.getStatus() != QuestionBankStatus.PENDING_REVIEW) {
            throw new RuntimeException("Only PENDING_REVIEW questions can be approved");
        }

        question.setStatus(QuestionBankStatus.APPROVED);
        question.setApprovedBy(approverId);
        question.setApprovedAt(System.currentTimeMillis());
        question.setUpdatedBy(approverId);
        question.setUpdatedAt(System.currentTimeMillis());

        QuestionBank saved = questionBankRepository.save(question);
        return toDto(saved);
    }

    @Override
    public QuestionBankDto reject(String id, String rejectionReason, String rejectorId, String rejectorName) {
        log.info("Rejecting question: {} by: {} reason: {}", id, rejectorId, rejectionReason);

        QuestionBank question = questionBankRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found: " + id));

        if (question.getStatus() != QuestionBankStatus.PENDING_REVIEW) {
            throw new RuntimeException("Only PENDING_REVIEW questions can be rejected");
        }

        question.setStatus(QuestionBankStatus.REJECTED);
        question.setRejectionReason(rejectionReason);
        question.setUpdatedBy(rejectorId);
        question.setUpdatedAt(System.currentTimeMillis());

        QuestionBank saved = questionBankRepository.save(question);
        return toDto(saved);
    }

    @Override
    public Page<QuestionBankDto> getPendingApprovals(Pageable pageable) {
        return questionBankRepository.findByStatus(QuestionBankStatus.PENDING_REVIEW, pageable)
                .map(this::toDto);
    }

    @Override
    public QuestionBankStatsDto getStats(String userId, boolean isAdmin) {
        QuestionBankStatsDto.QuestionBankStatsDtoBuilder builder = QuestionBankStatsDto.builder();

        // Overall stats (for admins)
        if (isAdmin) {
            builder.totalQuestions(questionBankRepository.count());
            builder.draftCount(questionBankRepository.countByStatus(QuestionBankStatus.DRAFT));
            builder.pendingReviewCount(questionBankRepository.countByStatus(QuestionBankStatus.PENDING_REVIEW));
            builder.approvedCount(questionBankRepository.countByStatus(QuestionBankStatus.APPROVED));
            builder.rejectedCount(questionBankRepository.countByStatus(QuestionBankStatus.REJECTED));
            builder.archivedCount(questionBankRepository.countByStatus(QuestionBankStatus.ARCHIVED));

            // Stats by subject
            Map<String, Long> bySubject = new HashMap<>();
            bySubject.put("Reasoning", questionBankRepository.countBySubject("General Intelligence and Reasoning"));
            bySubject.put("English", questionBankRepository.countBySubject("English Comprehension"));
            bySubject.put("Quant", questionBankRepository.countBySubject("Quantitative Aptitude"));
            bySubject.put("GK", questionBankRepository.countBySubject("General Awareness"));
            builder.questionsBySubject(bySubject);

            // Stats by difficulty
            Map<String, Long> byDifficulty = new HashMap<>();
            byDifficulty.put("EASY", questionBankRepository.countByDifficultyLevel(DifficultyLevel.EASY));
            byDifficulty.put("MEDIUM", questionBankRepository.countByDifficultyLevel(DifficultyLevel.MEDIUM));
            byDifficulty.put("HARD", questionBankRepository.countByDifficultyLevel(DifficultyLevel.HARD));
            builder.questionsByDifficulty(byDifficulty);

            // Stats by category
            Map<String, Long> byCategory = new HashMap<>();
            byCategory.put("SSC_CGL", questionBankRepository.countByPaperCategory(PaperCategory.SSC_CGL));
            byCategory.put("SSC_CHSL", questionBankRepository.countByPaperCategory(PaperCategory.SSC_CHSL));
            byCategory.put("SSC_CPO", questionBankRepository.countByPaperCategory(PaperCategory.SSC_CPO));
            byCategory.put("BANK_PO", questionBankRepository.countByPaperCategory(PaperCategory.BANK_PO));
            builder.questionsByCategory(byCategory);
        }

        // User stats (for teachers)
        if (userId != null) {
            builder.myTotalQuestions(questionBankRepository.countByCreatedBy(userId));
            builder.myDraftCount(questionBankRepository.countByCreatedByAndStatus(userId, QuestionBankStatus.DRAFT));
            builder.myPendingCount(questionBankRepository.countByCreatedByAndStatus(userId, QuestionBankStatus.PENDING_REVIEW));
            builder.myApprovedCount(questionBankRepository.countByCreatedByAndStatus(userId, QuestionBankStatus.APPROVED));
            builder.myRejectedCount(questionBankRepository.countByCreatedByAndStatus(userId, QuestionBankStatus.REJECTED));
        }

        return builder.build();
    }

    @Override
    public Page<QuestionBankDto> search(String searchText, Pageable pageable) {
        // For now, simple regex search. Later can add MongoDB text index
        Query query = new Query();
        query.addCriteria(new Criteria().orOperator(
                Criteria.where("problem.question").regex(searchText, "i"),
                Criteria.where("tags").regex(searchText, "i"),
                Criteria.where("topic").regex(searchText, "i"),
                Criteria.where("subject").regex(searchText, "i")));
        query.with(pageable);

        var results = mongoTemplate.find(query, QuestionBank.class);
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), QuestionBank.class);

        return PageableExecutionUtils.getPage(
                results.stream().map(this::toDto).toList(),
                pageable,
                () -> total);
    }

    private String generateQuestionId() {
        return "QB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private QuestionBankDto toDto(QuestionBank entity) {
        QuestionBankDto dto = new QuestionBankDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
