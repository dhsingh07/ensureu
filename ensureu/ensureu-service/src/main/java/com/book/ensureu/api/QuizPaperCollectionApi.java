package com.book.ensureu.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.book.ensureu.admin.constant.PaperStateStatus;
import com.book.ensureu.model.QuizPaperCollection;
import com.book.ensureu.service.QuizPaperCollectionService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/quizPaperColl")
public class QuizPaperCollectionApi {

	@Autowired
	private QuizPaperCollectionService quizPaperCollectionService;

	@CrossOrigin
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'TEACHER')")
	public void saveQuizPaperCollection(@RequestBody QuizPaperCollection quizPaperCollection) {
		log.info("saveTestPaper ==={}", 1);
		try {
			quizPaperCollectionService.createQuizPaperInCollection(quizPaperCollection);
		} catch (Exception ex) {
			log.error("saveQuizPaperCollection ", ex);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/getbyid/{id}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'TEACHER')")
	public QuizPaperCollection getQuizPaperById(@PathVariable(value = "id") final String id) {
		log.info("getQuizPaperById by ID {}", id);
		try {
			return quizPaperCollectionService.getQuizPaperCollectionById(id);
		} catch (Exception ex) {
			log.error("getQuizPaperById " + id, ex);
		}
		return null;
	}

	@CrossOrigin
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'TEACHER')")
	public Page<QuizPaperCollection> getAllQuizPaper(Pageable pageable) {
		log.info("getAllQuizPaper list {}", 1);
		try {
			return quizPaperCollectionService.getAllQuizPaperCollection(pageable);
		} catch (Exception ex) {
			log.error("getAllQuizPaper ", ex);
		}
		return null;
	}

	@CrossOrigin
	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	@PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'TEACHER')")
	public ResponseEntity<String> updateQuizPaperCollection(@RequestBody QuizPaperCollection quizPaperCollection) {
		log.info("updateQuizPaperCollection ==={}", quizPaperCollection.getId());
		try {
			if (quizPaperCollection.getId() == null) {
				return ResponseEntity.badRequest().body("Quiz ID is required for update");
			}

			// Check if existing quiz is APPROVED or ACTIVE - only SUPERADMIN can edit
			QuizPaperCollection existingQuiz = quizPaperCollectionService.getQuizPaperCollectionById(quizPaperCollection.getId());
			if (existingQuiz != null && isApprovedOrActive(existingQuiz) && !isSuperAdmin()) {
				log.warn("Non-SUPERADMIN user attempted to update APPROVED/ACTIVE quiz: {}", quizPaperCollection.getId());
				return ResponseEntity.status(HttpStatus.FORBIDDEN)
						.body("Only SUPERADMIN can update APPROVED or ACTIVE quizzes");
			}

			quizPaperCollectionService.createQuizPaperInCollection(quizPaperCollection);
			return ResponseEntity.ok("Quiz updated successfully");
		} catch (Exception ex) {
			log.error("updateQuizPaperCollection ", ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update quiz");
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'TEACHER')")
	public ResponseEntity<String> deleteQuizPaperCollection(@PathVariable(value = "id") final String id) {
		log.info("deleteQuizPaperCollection by ID {}", id);
		try {
			QuizPaperCollection quiz = quizPaperCollectionService.getQuizPaperCollectionById(id);
			if (quiz == null) {
				return ResponseEntity.notFound().build();
			}

			// Check if quiz is APPROVED or ACTIVE - only SUPERADMIN can delete
			if (isApprovedOrActive(quiz) && !isSuperAdmin()) {
				log.warn("Non-SUPERADMIN user attempted to delete APPROVED/ACTIVE quiz: {}", id);
				return ResponseEntity.status(HttpStatus.FORBIDDEN)
						.body("Only SUPERADMIN can delete APPROVED or ACTIVE quizzes");
			}

			quiz.setPaperStateStatus(PaperStateStatus.DELETED);
			quizPaperCollectionService.createQuizPaperInCollection(quiz);
			return ResponseEntity.ok("Quiz deleted successfully");
		} catch (Exception ex) {
			log.error("deleteQuizPaperCollection " + id, ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete quiz");
		}
	}

	/**
	 * Check if the quiz is in APPROVED or ACTIVE status
	 */
	private boolean isApprovedOrActive(QuizPaperCollection quiz) {
		PaperStateStatus status = quiz.getPaperStateStatus();
		return status == PaperStateStatus.APPROVED || status == PaperStateStatus.ACTIVE;
	}

	/**
	 * Check if the current user has SUPERADMIN role
	 */
	private boolean isSuperAdmin() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return false;
		}
		return authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.anyMatch(auth -> auth.equals("ROLE_SUPERADMIN"));
	}
}
