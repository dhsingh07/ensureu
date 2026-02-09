package com.book.ensureu.admin.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.book.ensureu.admin.constant.DifficultyLevel;
import com.book.ensureu.admin.constant.QuestionBankStatus;
import com.book.ensureu.admin.dto.QuestionBankCreateDto;
import com.book.ensureu.admin.dto.QuestionBankDto;
import com.book.ensureu.admin.dto.QuestionBankStatsDto;
import com.book.ensureu.admin.service.QuestionBankService;
import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.RoleType;
import com.book.ensureu.model.JwtUser;
import com.book.ensureu.model.Role;
import com.book.ensureu.security.UserPrincipalService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/admin/question-bank")
public class QuestionBankApi {

    @Autowired
    private QuestionBankService questionBankService;

    @Autowired
    private UserPrincipalService userPrincipalService;

    /**
     * Create a new question
     * Access: TEACHER, ADMIN, SUPERADMIN
     */
    @CrossOrigin
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<QuestionBankDto> create(@RequestBody QuestionBankCreateDto createDto) {
        JwtUser user = userPrincipalService.getCurrentUserDetails();
        String userName = user.getFirstname() + " " + user.getLastname();
        log.info("Creating question by user: {} ({})", user.getUsername(), userName);

        QuestionBankDto result = questionBankService.create(
                createDto,
                user.getUsername(),
                userName);

        return ResponseEntity.ok(result);
    }

    /**
     * Update a question
     * Access: Owner, ADMIN, SUPERADMIN
     */
    @CrossOrigin
    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    public ResponseEntity<QuestionBankDto> update(
            @PathVariable("id") String id,
            @RequestBody QuestionBankCreateDto updateDto) {

        JwtUser user = userPrincipalService.getCurrentUserDetails();
        log.info("Updating question: {} by user: {}", id, user.getUsername());

        // Get existing question to check ownership
        QuestionBankDto existing = questionBankService.getById(id);

        // Teachers can only update their own questions
        if (hasRole(user, RoleType.TEACHER) && !hasRole(user, RoleType.ADMIN) && !hasRole(user, RoleType.SUPERADMIN)) {
            if (!existing.getCreatedBy().equals(user.getUsername())) {
                return ResponseEntity.status(403).build();
            }
        }

        QuestionBankDto result = questionBankService.update(id, updateDto, user.getUsername());
        return ResponseEntity.ok(result);
    }

    /**
     * Get question by ID
     * Access: TEACHER, ADMIN, SUPERADMIN
     */
    @CrossOrigin
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<QuestionBankDto> getById(@PathVariable("id") String id) {
        log.info("Getting question: {}", id);
        QuestionBankDto result = questionBankService.getById(id);
        return ResponseEntity.ok(result);
    }

    /**
     * Delete a question
     * Access: Owner (DRAFT only), ADMIN, SUPERADMIN
     */
    @CrossOrigin
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        JwtUser user = userPrincipalService.getCurrentUserDetails();
        log.info("Deleting question: {} by user: {}", id, user.getUsername());

        // Get existing question to check ownership and status
        QuestionBankDto existing = questionBankService.getById(id);

        // Teachers can only delete their own DRAFT questions
        if (hasRole(user, RoleType.TEACHER) && !hasRole(user, RoleType.ADMIN) && !hasRole(user, RoleType.SUPERADMIN)) {
            if (!existing.getCreatedBy().equals(user.getUsername())) {
                return ResponseEntity.status(403).build();
            }
            if (existing.getStatus() != QuestionBankStatus.DRAFT) {
                return ResponseEntity.status(403).build();
            }
        }

        questionBankService.delete(id, user.getUsername());
        return ResponseEntity.ok().build();
    }

    /**
     * List questions with filters and pagination
     * Access: TEACHER (own + approved), ADMIN, SUPERADMIN (all)
     */
    @CrossOrigin
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Page<QuestionBankDto> list(
            @RequestParam(value = "paperType", required = false) PaperType paperType,
            @RequestParam(value = "paperCategory", required = false) PaperCategory paperCategory,
            @RequestParam(value = "paperSubCategory", required = false) PaperSubCategory paperSubCategory,
            @RequestParam(value = "subject", required = false) String subject,
            @RequestParam(value = "topic", required = false) String topic,
            @RequestParam(value = "difficultyLevel", required = false) DifficultyLevel difficultyLevel,
            @RequestParam(value = "status", required = false) QuestionBankStatus status,
            @RequestParam(value = "createdBy", required = false) String createdBy,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {

        log.info("Listing questions - paperType={}, category={}, subject={}, status={}, page={}, size={}",
                paperType, paperCategory, subject, status, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        return questionBankService.list(
                paperType, paperCategory, paperSubCategory,
                subject, topic, difficultyLevel, status, createdBy,
                pageable);
    }

    /**
     * List questions created by current user
     * Access: TEACHER, ADMIN, SUPERADMIN
     */
    @CrossOrigin
    @RequestMapping(value = "/my-questions", method = RequestMethod.GET)
    public Page<QuestionBankDto> myQuestions(
            @RequestParam(value = "status", required = false) QuestionBankStatus status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {

        JwtUser user = userPrincipalService.getCurrentUserDetails();
        log.info("Listing my questions for user: {}", user.getUsername());

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        return questionBankService.listByUser(user.getUsername(), status, pageable);
    }

    /**
     * Submit question for review
     * Access: TEACHER (owner)
     */
    @CrossOrigin
    @RequestMapping(value = "/submit-for-review/{id}", method = RequestMethod.PUT)
    public ResponseEntity<QuestionBankDto> submitForReview(@PathVariable("id") String id) {
        JwtUser user = userPrincipalService.getCurrentUserDetails();
        log.info("Submitting question for review: {} by user: {}", id, user.getUsername());

        QuestionBankDto result = questionBankService.submitForReview(id, user.getUsername());
        return ResponseEntity.ok(result);
    }

    /**
     * Approve a question
     * Access: ADMIN, SUPERADMIN
     */
    @CrossOrigin
    @RequestMapping(value = "/approve/{id}", method = RequestMethod.PUT)
    public ResponseEntity<QuestionBankDto> approve(@PathVariable("id") String id) {
        JwtUser user = userPrincipalService.getCurrentUserDetails();
        log.info("Approving question: {} by user: {}", id, user.getUsername());

        // Only Admin or SuperAdmin can approve
        if (!hasRole(user, RoleType.ADMIN) && !hasRole(user, RoleType.SUPERADMIN)) {
            return ResponseEntity.status(403).build();
        }

        String userName = user.getFirstname() + " " + user.getLastname();
        QuestionBankDto result = questionBankService.approve(id, user.getUsername(), userName);
        return ResponseEntity.ok(result);
    }

    /**
     * Reject a question
     * Access: ADMIN, SUPERADMIN
     */
    @CrossOrigin
    @RequestMapping(value = "/reject/{id}", method = RequestMethod.PUT)
    public ResponseEntity<QuestionBankDto> reject(
            @PathVariable("id") String id,
            @RequestParam("reason") String reason) {

        JwtUser user = userPrincipalService.getCurrentUserDetails();
        log.info("Rejecting question: {} by user: {} reason: {}", id, user.getUsername(), reason);

        // Only Admin or SuperAdmin can reject
        if (!hasRole(user, RoleType.ADMIN) && !hasRole(user, RoleType.SUPERADMIN)) {
            return ResponseEntity.status(403).build();
        }

        String userName = user.getFirstname() + " " + user.getLastname();
        QuestionBankDto result = questionBankService.reject(id, reason, user.getUsername(), userName);
        return ResponseEntity.ok(result);
    }

    /**
     * Get pending approvals
     * Access: ADMIN, SUPERADMIN
     */
    @CrossOrigin
    @RequestMapping(value = "/pending", method = RequestMethod.GET)
    public Page<QuestionBankDto> getPendingApprovals(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {

        log.info("Getting pending approvals page={} size={}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));

        return questionBankService.getPendingApprovals(pageable);
    }

    /**
     * Get question bank statistics
     * Access: TEACHER (own), ADMIN, SUPERADMIN (all)
     */
    @CrossOrigin
    @RequestMapping(value = "/stats", method = RequestMethod.GET)
    public ResponseEntity<QuestionBankStatsDto> getStats() {
        JwtUser user = userPrincipalService.getCurrentUserDetails();
        boolean isAdmin = hasRole(user, RoleType.ADMIN) || hasRole(user, RoleType.SUPERADMIN);

        log.info("Getting stats for user: {} isAdmin: {}", user.getUsername(), isAdmin);

        QuestionBankStatsDto stats = questionBankService.getStats(user.getUsername(), isAdmin);
        return ResponseEntity.ok(stats);
    }

    /**
     * Search questions
     * Access: TEACHER, ADMIN, SUPERADMIN
     */
    @CrossOrigin
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public Page<QuestionBankDto> search(
            @RequestParam("q") String searchText,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {

        log.info("Searching questions: q={} page={} size={}", searchText, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        return questionBankService.search(searchText, pageable);
    }

    private boolean hasRole(JwtUser user, RoleType roleType) {
        if (user == null || user.getRoles() == null) {
            return false;
        }
        List<Role> roles = user.getRoles();
        for (Role role : roles) {
            if (role.getRoleType() == roleType) {
                return true;
            }
        }
        return false;
    }
}
