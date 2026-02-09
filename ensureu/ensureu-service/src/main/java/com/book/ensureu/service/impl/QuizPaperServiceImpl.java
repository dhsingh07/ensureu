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

import com.book.ensureu.constant.CounterEnum;
import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperStatus;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.dto.AttemptedPaperDto;
import com.book.ensureu.dto.PaperDto;
import com.book.ensureu.dto.PaperInfo;
import com.book.ensureu.model.QuizPaper;
import com.book.ensureu.model.QuizPaperCollection;
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
		// need to add paperIds from subscription and entitle table..

		List<PaperDto> resultDto = null;
		//to do in subscription table to quiz as well...
		
		/*
		 * List<PaperInfo> listPaperInfo =
		 * subscriptionService.getPaperInfoListForUser(userId, new Date().getTime(), new
		 * Date().getTime(), true, paperType, paperCategory, testType);
		 * 
		 * List<String> paperIds = new ArrayList<>(); long validityDate; if
		 * (listPaperInfo != null && !listPaperInfo.isEmpty()) { validityDate =
		 * listPaperInfo.get(0).getValidity(); listPaperInfo.forEach(paperInfo -> {
		 * paperIds.add(paperInfo.getId());
		 * 
		 * }); } else { log.info("User ["+userId+"] is not subscribed paper"); return
		 * null; }
		 */
		/*
		 * paperIds.add(3L); paperIds.add(4L); paperIds.add(5L); paperIds.add(6L);
		 */
		
		Long validityDate=new Date().getTime();
		
		List<String> paperIds = new ArrayList<>();
		paperIds.add("quiz1"); paperIds.add("quiz2"); paperIds.add("quiz3"); paperIds.add("quiz4");paperIds.add("quiz5");
		try {
			if (paperIds == null || paperIds.isEmpty()) {
				return null;
			}
			log.info("size Quiz paperIds [" + paperIds.size() + "] ");
			log.info("getPaperStatusDetailsByPaperIds QUIZ userId [" + userId + "] paperIds [" + paperIds + "]");
			List<QuizPaper> quizPaperList = quizPaperRepository.findQuizPaperUsingUserIdAndPaperIds(userId, paperIds);
			if (quizPaperList != null && !quizPaperList.isEmpty()) {
				log.info("getPaperStatusDetailsByPaperIds QUIZ paperSize [" + quizPaperList.size() + "]");
				
				List<QuizPaper> paperNotDoneOnlyList=new ArrayList<>();
				quizPaperList.forEach(paperQuiz->{
					
					if(paperQuiz!=null && !PaperStatus.DONE.equals(paperQuiz.getPaperStatus())) {
						paperNotDoneOnlyList.add(paperQuiz);
				}});
				
				log.info("getPaperStatusDetailsByPaperIds PAID paperSize after DONE remove [" + paperNotDoneOnlyList.size() + "]");

				// get from user collection..
				List<PaperDto> paperDtoFromUserPaper = QuizPaperConversionUtil.quizPaperToDto(paperNotDoneOnlyList);
				if (paperIds.size() > quizPaperList.size()) {
					quizPaperList.forEach(p -> paperIds.remove(p.getPaperId()));
					log.info("size after change paperSize [" + quizPaperList.size() + "]");
					// get from paperCollection...
					List<QuizPaperCollection> quizPaperColl = quizPaperCollectionRepository.findByIdListIns(paperIds);
					if (quizPaperColl != null && !quizPaperColl.isEmpty()) {
						List<QuizPaper> quizPaperList1 = new ArrayList<>();
						quizPaperColl
								.forEach(quizPaperCollItem -> quizPaperList1.add(createQuizPaperByQuizCollectionPaper(
										quizPaperCollItem, testType, userId, null, validityDate)));
						List<PaperDto> paperDtoFromColl = QuizPaperConversionUtil.quizPaperToDto(quizPaperList1);
						if(paperDtoFromUserPaper!=null && paperDtoFromColl!=null) {
							resultDto = Stream.of(paperDtoFromUserPaper, paperDtoFromColl).flatMap(x -> x.stream())
									.collect(Collectors.toList());
						}else if(paperDtoFromUserPaper!=null) {
							resultDto=paperDtoFromUserPaper;
						}else if(paperDtoFromColl!=null) {
							resultDto= paperDtoFromColl;
						}
					
					} else {
						resultDto = paperDtoFromUserPaper;
					}
				} else {
					resultDto = paperDtoFromUserPaper;
				}
				log.info("getPaperStatusDetailsByPaperIds PAID paperSize [" + paperIds.size() + "]");
			}

			else {
				List<QuizPaperCollection> quizPaperColl = quizPaperCollectionRepository.findByIdListIns(paperIds);
				List<QuizPaper> quizPaperList1 = new ArrayList<>();
				quizPaperColl.forEach(paidPaperCollItem -> quizPaperList1.add(
						createQuizPaperByQuizCollectionPaper(paidPaperCollItem, testType, userId, null, validityDate)));
				resultDto = QuizPaperConversionUtil.quizPaperToDto(quizPaperList1);
			}
		} catch (Exception ex) {
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
