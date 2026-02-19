package com.book.ensureu.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.book.ensureu.admin.constant.PaperStateStatus;
import com.book.ensureu.constant.CounterEnum;
import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperStatus;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.PurchaseStatus;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.dto.AttemptedPaperDto;
import com.book.ensureu.dto.PaperDto;
import com.book.ensureu.dto.PaperInfo;
import com.book.ensureu.model.PurchaseSubscriptions;
import com.book.ensureu.model.QuizPaper;
import com.book.ensureu.model.QuizPaperCollection;
import com.book.ensureu.repository.PurchaseSubscriptionsRespository;
import com.book.ensureu.repository.QuizPaperCollectionRepository;
import com.book.ensureu.repository.QuizPaperRepository;
import com.book.ensureu.service.CounterService;
import com.book.ensureu.service.PaperService;
import com.book.ensureu.service.SubscriptionService;
import com.book.ensureu.util.QuizPaperConversionUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Qualifier("quizPaper")
public class QuizPaperServiceImpl implements PaperService {

	@Autowired
	QuizPaperRepository quizPaperRepository;

	@Autowired
	@Lazy
	QuizPaperCollectionRepository quizPaperCollectionRepository;

	@Autowired
	CounterService counterService;

	@Autowired
	@Lazy
	private SubscriptionService subscriptionService;

	@Autowired
	@Lazy
	private PurchaseSubscriptionsRespository purchaseSubscriptionsRepository;
	
	@Override
	public void savePaper(PaperDto paperDto) {

		QuizPaper quizPaper = QuizPaperConversionUtil.quizPaperDtoToModel(paperDto);
		if (paperDto.getId() == null) {
			quizPaper.setId(counterService.increment(CounterEnum.QUIZPAPER));
		}
		log.info("User Mapping quizPaper UserId [" + quizPaper.getUserId() + "] paperId [" + quizPaper.getPaperId()
				+ "]");
		quizPaperRepository.save(quizPaper);
		
	}

	@Override
	public void updatePaper(PaperDto paperDto) {
		log.info("Update User with quiz paper..");
		QuizPaper quizPaper = QuizPaperConversionUtil.quizPaperDtoToModel(paperDto);
		log.info("User Mapping QuizPaper UserId [" + quizPaper.getUserId() + "] paperId [" + quizPaper.getPaperId()
				+ "]");
		quizPaperRepository.save(quizPaper);
	}

	@Override
	public void submitPaper(AttemptedPaperDto attemptedPaper) {

	}

	@Override
	public PaperDto getPaperById(Long id, TestType typePaper) {
		Optional<QuizPaper> quizPaperOptional = quizPaperRepository.findById(id);
		if (quizPaperOptional.isPresent()) {
			return QuizPaperConversionUtil.quizPaperToDto(quizPaperOptional.get());
		}
		return null;
	}

	@Override
	public List<PaperDto> getPaperByPaperId(String paperId, TestType typePaper) {
		return null;
	}

	@Override
	public PaperDto getPaperByPaperIdAndUserId(String paperId, String userId, TestType typePaper) {
		return null;
	}

	@Override
	public List<PaperDto> getPaperByUserId(String userId) {
		return null;
	}

	@Override
	public List<PaperDto> getPaperByUserIdAndPaperType(String userId, PaperType paperType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PaperDto> getPaperByUserIdAndPaperTypeAndTestType(String userId, PaperType paperType,
			TestType testType) {
		return null;
	}

	@Override
	public PaperDto paperMappedUserByPaperStatus(String userId, TestType testType, PaperStatus paperStatus,
			String paperId) {
		

		if (PaperStatus.DONE.equals(paperStatus)) {
			QuizPaper quizPaperFromDb = quizPaperRepository
					.findByUserIdAndPaperId(userId, paperId);
			if (quizPaperFromDb != null) {
				return QuizPaperConversionUtil.quizPaperToDto(quizPaperFromDb);
			} else {
				log.info("PaperId is not valid");
				return null;
			}

		} else {
			
			QuizPaper quizPaperFromDb = quizPaperRepository
					.findByUserIdAndPaperId(userId, paperId);
			
			if(quizPaperFromDb!=null && quizPaperFromDb.getPaperStatus().equals(PaperStatus.DONE)) {
				log.info("PaperId is already completed");
				return null;
			}
			
			if (paperStatus.equals(paperStatus.START)) {
				if (quizPaperFromDb != null) {
					return QuizPaperConversionUtil.quizPaperToDto(quizPaperFromDb);
				} else {

					// need to add validity date to paper...
					// valididy date from subscription and user entitlemt
					// collection.
					List<String> paperList = new ArrayList<String>();
					paperList.add(paperId);
					List<PaperInfo> paperInfo = subscriptionService
							.getPaperInfoListForUser(userId, paperList,
									testType);
					long valididtyDate = 0;
					if (paperInfo != null && !paperInfo.isEmpty()) {
						valididtyDate = paperInfo.get(0).getValidity();
					}

					Optional<QuizPaperCollection> quizPaperCollOpt = quizPaperCollectionRepository
							.findById(paperId);
					if (quizPaperCollOpt.isPresent()) {
						QuizPaperCollection quizPaperCollection = quizPaperCollOpt
								.get();
						QuizPaper quizPaper = createQuizPaperByQuizCollectionPaper(
								quizPaperCollection, testType, userId,
								"UserMapped", valididtyDate);
						quizPaper.setId(counterService
								.increment(CounterEnum.QUIZPAPER));
						quizPaper = quizPaperRepository.save(quizPaper);
						return QuizPaperConversionUtil.quizPaperToDto(quizPaper);

					} else {
						throw new IllegalArgumentException(
								"PaperId is not valid");
					}
				}
			} else if (paperStatus.equals(paperStatus.RESUME)
					|| paperStatus.equals(paperStatus.INPROGRESS)) {
				return QuizPaperConversionUtil.quizPaperToDto(quizPaperFromDb);
			}
		}
		
		return null;
	}

	@Override
	public long getAllCountPaper() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getPaperCountByPaperTypeAndTestType(PaperType paperType, TestType testType) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getPaperCountByPaperTypeAndTestTypeAndPaperCategory(PaperType paperType, TestType testType,
			PaperCategory paperCategory) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getPaperCountByPaperTypeAndTestTypeAndPaperCategoryAndPaperSubCategory(PaperType paperType,
			TestType testType, PaperCategory paperCategory, PaperSubCategory paperSubCategory) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getPaperCountByPaperType(PaperType paperType) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getAllCountPaperCollectionByPaperType(PaperType paperType, TestType Paper) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<PaperDto> getPaperStatusDetailsByPaperIds(String userId, List<String> paperIds, TestType testType)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PaperDto> getPaperStatusDetailsByStatusAndPaperType(String userId, TestType testType,
			PaperType paperType, PaperStatus paperStatus, PaperCategory paperCategory) throws Exception {
		List<PaperDto> paperDto = null;
		try {
			List<QuizPaper> quizPaperList = null;
			log.info("getPaperStatusDetailsByStatusAndPaperType Quiz userId [" + userId + "] paperStatus ["
					+ paperStatus + "]");
			if (paperCategory != null) {
				quizPaperList = quizPaperRepository.findQuizPaperUsingUserIdAndPaperStatusAndPaperCategory(userId,
						paperStatus, paperType, paperCategory);
			} else {
				quizPaperList = quizPaperRepository.findQuizPaperUsingUserIdAndPaperStatus(userId, paperStatus,
						paperType);
			}
			if (quizPaperList != null && !quizPaperList.isEmpty()) {
				paperDto = QuizPaperConversionUtil.quizPaperToDto(quizPaperList);
			}
		} catch (Exception ex) {
			log.error("error getPaperStatusDetailsByStatusAndPaperType ", ex);
			throw ex;
		}
		return paperDto;
	}

	@Override
	public List<PaperDto> getMissedPapersByUsers(String userId, TestType testType, PaperType paperType,
			PaperCategory paperCategory) throws Exception {
		return null;
	}

	@Override
	public List<PaperDto> getPaperStatusDetailsByPaperCateoryORTestType(String userId, TestType testType,
			PaperType paperType, PaperCategory paperCategory) throws Exception {

		List<PaperDto> resultDto = null;
		Long currentTime = System.currentTimeMillis();

		try {
			// Step 1: Check if user has an active subscription for this category
			if (userId != null && paperCategory != null) {
				List<PurchaseSubscriptions> activeSubscriptions = purchaseSubscriptionsRepository
						.findActiveSubscriptionsByUserAndCategory(
								userId,
								paperCategory,
								PurchaseStatus.COMPLETED,
								currentTime);

				if (activeSubscriptions == null || activeSubscriptions.isEmpty()) {
					log.info("User [{}] does not have an active subscription for category [{}]", userId, paperCategory);
					return null;
				}
				log.info("User [{}] has {} active subscription(s) for category [{}]",
						userId, activeSubscriptions.size(), paperCategory);
			}

			// Step 2: Fetch ACTIVE quizzes within validity date range for this category
			List<QuizPaperCollection> activeQuizzes;
			if (paperCategory != null) {
				activeQuizzes = quizPaperCollectionRepository.findActiveQuizzesForCategory(
						PaperStateStatus.ACTIVE,
						paperCategory,
						currentTime);
			} else {
				activeQuizzes = quizPaperCollectionRepository.findActiveQuizzesForPaperType(
						PaperStateStatus.ACTIVE,
						paperType,
						currentTime);
			}

			if (activeQuizzes == null || activeQuizzes.isEmpty()) {
				log.info("No active quizzes found for category [{}] at current time [{}]", paperCategory, currentTime);
				return null;
			}

			log.info("Found [{}] active quizzes for category [{}]", activeQuizzes.size(), paperCategory);

			// Step 3: Get paper IDs from active quizzes
			List<String> paperIds = activeQuizzes.stream()
					.map(QuizPaperCollection::getId)
					.collect(Collectors.toList());

			// Step 4: Check if user has already started/completed any of these quizzes
			List<QuizPaper> userQuizPaperList = null;
			if (userId != null) {
				userQuizPaperList = quizPaperRepository.findQuizPaperUsingUserIdAndPaperIds(userId, paperIds);
			}

			if (userQuizPaperList != null && !userQuizPaperList.isEmpty()) {
				log.info("User [{}] has [{}] quiz attempts", userId, userQuizPaperList.size());

				// Filter out completed quizzes
				List<QuizPaper> paperNotDoneOnlyList = userQuizPaperList.stream()
						.filter(paperQuiz -> paperQuiz != null && !PaperStatus.DONE.equals(paperQuiz.getPaperStatus()))
						.collect(Collectors.toList());

				log.info("After filtering DONE, [{}] quizzes remain in progress", paperNotDoneOnlyList.size());

				// Get DTOs from user's quiz attempts
				List<PaperDto> paperDtoFromUserPaper = QuizPaperConversionUtil.quizPaperToDto(paperNotDoneOnlyList);

				// Find quizzes not yet started by the user
				List<String> startedPaperIds = userQuizPaperList.stream()
						.map(QuizPaper::getPaperId)
						.collect(Collectors.toList());
				List<QuizPaperCollection> notStartedQuizzes = activeQuizzes.stream()
						.filter(quiz -> !startedPaperIds.contains(quiz.getId()))
						.collect(Collectors.toList());

				if (!notStartedQuizzes.isEmpty()) {
					// Get validity date from subscription
					Long validityDate = currentTime;

					List<QuizPaper> newQuizPaperList = notStartedQuizzes.stream()
							.map(quiz -> createQuizPaperByQuizCollectionPaper(quiz, testType, userId, null, validityDate))
							.collect(Collectors.toList());

					List<PaperDto> paperDtoFromColl = QuizPaperConversionUtil.quizPaperToDto(newQuizPaperList);

					// Merge both lists
					if (paperDtoFromUserPaper != null && paperDtoFromColl != null) {
						resultDto = Stream.of(paperDtoFromUserPaper, paperDtoFromColl)
								.flatMap(List::stream)
								.collect(Collectors.toList());
					} else if (paperDtoFromUserPaper != null) {
						resultDto = paperDtoFromUserPaper;
					} else {
						resultDto = paperDtoFromColl;
					}
				} else {
					resultDto = paperDtoFromUserPaper;
				}
			} else {
				// User has not started any quiz yet - return all active quizzes
				Long validityDate = currentTime;
				List<QuizPaper> quizPaperList = activeQuizzes.stream()
						.map(quiz -> createQuizPaperByQuizCollectionPaper(quiz, testType, userId, null, validityDate))
						.collect(Collectors.toList());
				resultDto = QuizPaperConversionUtil.quizPaperToDto(quizPaperList);
			}

			log.info("Returning [{}] quizzes to user [{}]",
					resultDto != null ? resultDto.size() : 0, userId);

		} catch (Exception ex) {
			log.error("Error in getPaperStatusDetailsByPaperCateoryORTestType", ex);
			throw ex;
		}
		return resultDto;
	}

	private QuizPaper createQuizPaperByQuizCollectionPaper(QuizPaperCollection quizPaperColl, TestType testType,
			String userId, String userMapped, Long validityDate) {
		QuizPaper quizPaper = null;
		if (quizPaperColl != null) {
			quizPaper = new QuizPaper();
			quizPaper.setPaperId(quizPaperColl.getId());
			if (userMapped != null) {
				quizPaper.setPaper(quizPaperColl);
				quizPaper.setPaperStatus(PaperStatus.INPROGRESS);
				quizPaper.setCreateDateTime(System.currentTimeMillis());
			} else {
				quizPaper.setPaperStatus(PaperStatus.START);
			}
			quizPaper.setPaperCategory(quizPaperColl.getPaperCategory());
			quizPaper.setPaperSubCategory(quizPaperColl.getPaperSubCategory());
			quizPaper.setPaperType(quizPaperColl.getPaperType());
			quizPaper.setPaperName(quizPaperColl.getPaperName());
			quizPaper.setPaperSubCategoryName(quizPaperColl.getPaperSubCategoryName());
			quizPaper.setTestType(testType);
			quizPaper.setUserId(userId);
			quizPaper.setPaperValidityEndDate(validityDate);
			quizPaper.setTotalScore(quizPaperColl.getTotalScore());
			quizPaper.setTotalTime(quizPaperColl.getTotalTime());
			quizPaper.setNegativeMarks(quizPaperColl.getNegativeMarks());
			quizPaper.setTotalQuestionCount(quizPaperColl.getTotalQuestionCount());
			quizPaper.setPerQuestionScore(quizPaperColl.getPerQuestionScore());
			quizPaper.setTotalGetScore(quizPaperColl.getTotalGetScore());
			// paidPaper.setPaperValidityStartDate(paidPaperColl.getValidityRangeStartDateTime());
		}
		return quizPaper; 
	}

}
