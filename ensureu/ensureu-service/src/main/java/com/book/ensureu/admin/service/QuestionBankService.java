package com.book.ensureu.admin.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.book.ensureu.admin.constant.DifficultyLevel;
import com.book.ensureu.admin.constant.QuestionBankStatus;
import com.book.ensureu.admin.dto.QuestionBankCreateDto;
import com.book.ensureu.admin.dto.QuestionBankDto;
import com.book.ensureu.admin.dto.QuestionBankStatsDto;
import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;

public interface QuestionBankService {

    // CRUD operations
    QuestionBankDto create(QuestionBankCreateDto createDto, String userId, String userName);

    QuestionBankDto update(String id, QuestionBankCreateDto updateDto, String userId);

    QuestionBankDto getById(String id);

    QuestionBankDto getByQuestionId(String questionId);

    void delete(String id, String userId);

    // List operations with filtering
    Page<QuestionBankDto> list(
            PaperType paperType,
            PaperCategory paperCategory,
            PaperSubCategory paperSubCategory,
            String subject,
            String topic,
            DifficultyLevel difficultyLevel,
            QuestionBankStatus status,
            String createdBy,
            Pageable pageable);

    Page<QuestionBankDto> listByUser(String userId, QuestionBankStatus status, Pageable pageable);

    // Approval workflow
    QuestionBankDto submitForReview(String id, String userId);

    QuestionBankDto approve(String id, String approverId, String approverName);

    QuestionBankDto reject(String id, String rejectionReason, String rejectorId, String rejectorName);

    // Pending approvals
    Page<QuestionBankDto> getPendingApprovals(Pageable pageable);

    // Statistics
    QuestionBankStatsDto getStats(String userId, boolean isAdmin);

    // Search
    Page<QuestionBankDto> search(String searchText, Pageable pageable);
}
