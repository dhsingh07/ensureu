package com.book.ensureu.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import com.book.ensureu.flow.analytics.repository.PaperStatRepository;
import com.book.ensureu.model.FreePaper;
import com.book.ensureu.model.FreePaperCollection;
import com.book.ensureu.repository.FreePaperCollectionRepository;
import com.book.ensureu.repository.FreePaperRepository;
import com.book.ensureu.service.CounterService;
import com.book.ensureu.service.PaperService;
import com.book.ensureu.service.SubscriptionService;
import com.book.ensureu.util.PaperConversionUtil;

@Service
@Qualifier("freePaper")
public class PaperServiceImpl implements PaperService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PaperServiceImpl.class.getName());

	@Autowired
	FreePaperRepository freePaperRepository;

	@Autowired
	FreePaperCollectionRepository freePaperCollectionRepository;

	@Autowired
	CounterService counterService;
	
	@Autowired
	PaperStatRepository paperStatRepository;

	@Autowired
	private SubscriptionService subscriptionService;

	@Override
	public void savePaper(PaperDto paperDto) {
		FreePaper freePaper = PaperConversionUtil.freePaperDtoToModel(paperDto);
		if (freePaper != null && freePaper.getUserId() != null && freePaper.getPaperId() != null) {
			FreePaper existing = freePaperRepository.findByUserIdAndPaperId(freePaper.getUserId(), freePaper.getPaperId());
			if (existing != null && PaperStatus.DONE.equals(existing.getPaperStatus())
					&& !PaperStatus.DONE.equals(freePaper.getPaperStatus())) {
				LOGGER.info("Skip save: paper already DONE for userId [{}], paperId [{}]", freePaper.getUserId(),
						freePaper.getPaperId());
				return;
			}
		}
		if (paperDto.getId() == null) {
			freePaper.setId(counterService.increment(CounterEnum.FREEPAPER));
		}
		LOGGER.info("User Mapping FreePaper UserId [" + freePaper.getUserId() + "] paperId [" + freePaper.getPaperId()
				+ "]");
		freePaperRepository.save(freePaper);
	}

	@Override
	public void updatePaper(PaperDto paperDto) {
		FreePaper freePaper = PaperConversionUtil.freePaperDtoToModel(paperDto);
		if (freePaper != null && freePaper.getUserId() != null && freePaper.getPaperId() != null) {
			FreePaper existing = freePaperRepository.findByUserIdAndPaperId(freePaper.getUserId(), freePaper.getPaperId());
			if (existing != null && PaperStatus.DONE.equals(existing.getPaperStatus())
					&& !PaperStatus.DONE.equals(freePaper.getPaperStatus())) {
				LOGGER.info("Skip update: paper already DONE for userId [{}], paperId [{}]", freePaper.getUserId(),
						freePaper.getPaperId());
				return;
			}
		}
		if (paperDto.getId() == null) {
			throw new IllegalArgumentException("id can not be null");
		}
		LOGGER.info("User Mapping FreePaper UserId [" + freePaper.getUserId() + "] paperId [" + freePaper.getPaperId()
				+ "]");
		freePaperRepository.save(freePaper);
	}

	@Override
	public void submitPaper(AttemptedPaperDto attemptedPaper) {

	}

	@Override
	public PaperDto getPaperById(Long id, TestType testPaper) {
		return null;
	}

	@Override
	public List<PaperDto> getPaperByPaperId(String paperId, TestType testPaper) {

		System.out.println("FRee Paper");
		return null;
	}

	@Override
	public long getPaperCountByPaperType(PaperType paperType) {
		return 0;
	}

	@Override
	public PaperDto getPaperByPaperIdAndUserId(String paperId, String userId, TestType testType) {

		FreePaper freePaper = freePaperRepository.findByUserIdAndPaperId(userId, paperId);
		FreePaperCollection freePaperColl = null;
		if (freePaper != null) {
			return PaperConversionUtil.freePaperToDto(freePaper);
		} else {
			Optional<FreePaperCollection> freePaperCollOp = freePaperCollectionRepository.findById(paperId);
			if (freePaperCollOp.isPresent()) {

				List<String> paperList = new ArrayList<String>();
				paperList.add(paperId);
				List<PaperInfo> paperInfo = subscriptionService.getPaperInfoListForUser(userId, paperList, testType);
				long valididtyDate = 0;
				if (paperInfo != null && !paperInfo.isEmpty()) {
					valididtyDate = paperInfo.get(0).getValidity();
				}
				freePaperColl = freePaperCollOp.get();
				return PaperConversionUtil.freePaperToDto(
						createFreePaperByFreeCollectionPaper(freePaperColl, testType, userId, null, valididtyDate));
			} else {
				throw new IllegalArgumentException("paperId is not valid" + paperId);
			}
		}
	}

	@Override
	public long getAllCountPaper() {
		return 0;
	}

	@Override
	public long getAllCountPaperCollectionByPaperType(PaperType paperType, TestType testPaper) {
		return 0;// freePaperCollectionRepository.coun(testPaper);
	}

	@Override
	public List<PaperDto> getPaperByUserId(String userId) {
		List<FreePaper> freePaperList = freePaperRepository.findByUserId(userId);
		if (freePaperList != null && !freePaperList.isEmpty()) {
			return PaperConversionUtil.freePaperToDto(freePaperList);
		}
		return null;
	}

	@Override
	public List<PaperDto> getPaperByUserIdAndPaperType(String userId, PaperType paperType) {
		List<FreePaper> freePaperList = freePaperRepository.findByUserIdAndPaperType(userId, paperType);
		if (freePaperList != null && !freePaperList.isEmpty()) {
			return PaperConversionUtil.freePaperToDto(freePaperList);
		}
		return null;
	}

	@Override
	public List<PaperDto> getPaperByUserIdAndPaperTypeAndTestType(String userId, PaperType paperType,
			TestType testType) {
		List<FreePaper> freePaperList = freePaperRepository.findByUserIdAndPaperTypeAndTestType(userId, paperType,
				testType);
		if (freePaperList != null && !freePaperList.isEmpty()) {
			return PaperConversionUtil.freePaperToDto(freePaperList);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.book.assessu.service.PaperService#getPaperCountByPaperTypeAndTestType(com
	 * .book.assessu.constant.PaperType, com.book.assessu.constant.TestType)
	 * 
	 * FreePaperColletion count by paperTyoe and TestType
	 */
	@Override
	public long getPaperCountByPaperTypeAndTestType(PaperType paperType, TestType testType) {
		return freePaperCollectionRepository.countByPaperTypeAndTestType(paperType, testType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.book.assessu.service.PaperService#
	 * getPaperCountByPaperTypeAndTestTypeAndPaperCategory(com.book.assessu.constant
	 * .PaperType, com.book.assessu.constant.TestType,
	 * com.book.assessu.constant.PaperCategory) FreePaperColletion count by
	 * paperType, TestType and paperCategory...
	 */
	@Override
	public long getPaperCountByPaperTypeAndTestTypeAndPaperCategory(PaperType paperType, TestType testType,
			PaperCategory paperCategory) {
		return freePaperCollectionRepository.countByPaperTypeAndPaperCategoryAndTestType(paperType, paperCategory,
				testType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.book.assessu.service.PaperService#
	 * getPaperCountByPaperTypeAndTestTypeAndPaperCategoryAndPaperSubCategory(com.
	 * book.assessu.constant.PaperType, com.book.assessu.constant.TestType,
	 * com.book.assessu.constant.PaperCategory,
	 * com.book.assessu.constant.PaperSubCategory) FreePaperColletion count by
	 * paperType, TestType,paperCategory and paperSubCategory...
	 */
	@Override
	public long getPaperCountByPaperTypeAndTestTypeAndPaperCategoryAndPaperSubCategory(PaperType paperType,
			TestType testType, PaperCategory paperCategory, PaperSubCategory paperSubCategory) {
		return freePaperCollectionRepository.countByPaperTypeAndPaperCategoryAndPaperSubCategoryAndTestType(paperType,
				paperCategory, paperSubCategory, testType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.book.assessu.service.PaperService#paperMappedUserByPaperStatus(java.lang.
	 * String, com.book.assessu.constant.TestType,
	 * com.book.assessu.constant.PaperStatus, java.lang.Long)
	 */
	@Override
	public PaperDto paperMappedUserByPaperStatus(String userId, TestType testType, PaperStatus paperStatus,
			String paperId) {

		if (PaperStatus.DONE.equals(paperStatus)) {
			FreePaper freePaperFromDb = freePaperRepository.findByUserIdAndPaperId(userId, paperId);
			if (freePaperFromDb != null) {
				return PaperConversionUtil.freePaperToDto(freePaperFromDb);
			} else {
				LOGGER.info("PaperId is not valid");
				return null;
			}

		} else {
			FreePaper freePaperFromDb = freePaperRepository.findByUserIdAndPaperId(userId, paperId);
			// in start status need to check from userPaperCollection as well for without
			// login check..
			if (freePaperFromDb != null && freePaperFromDb.getPaperStatus().equals(paperStatus.DONE)) {
				LOGGER.info("PaperId is already completed");
				return null;
			}

			if (paperStatus.equals(paperStatus.START)) {
				Optional<FreePaperCollection> freePaperCollOpt = null;
				if (freePaperFromDb != null) {
					return PaperConversionUtil.freePaperToDto(freePaperFromDb);
				} else {

					// need to add validity date to paper...
					// valididy date from subscription and user entitlemt collection.
					List<String> paperList = new ArrayList<String>();
					paperList.add(paperId);
					List<PaperInfo> paperInfo = subscriptionService.getPaperInfoListForUser(userId, paperList,
							testType);
					long valididtyDate = 0;
					if (paperInfo != null && !paperInfo.isEmpty()) {
						valididtyDate = paperInfo.get(0).getValidity();
					}

					freePaperCollOpt = freePaperCollectionRepository.findById(paperId);
					if (freePaperCollOpt.isPresent()) {
						FreePaperCollection freePaperCollection = freePaperCollOpt.get();
						FreePaper freePaper = createFreePaperByFreeCollectionPaper(freePaperCollection, testType,
								userId, "UserMap", valididtyDate);
						freePaper.setId(counterService.increment(CounterEnum.FREEPAPER));
						freePaper = freePaperRepository.save(freePaper);
						return PaperConversionUtil.freePaperToDto(freePaper);

					} else {
						throw new IllegalArgumentException("PaperId is not valid");
					}
				}

			} else if (paperStatus.equals(paperStatus.RESUME) || paperStatus.equals(paperStatus.INPROGRESS)) {
				return PaperConversionUtil.freePaperToDto(freePaperFromDb);
			}
		}
		return null;

	}

	private FreePaper createFreePaperByFreeCollectionPaper(FreePaperCollection freePaperColl, TestType testType,
			String userId, String userMap, long validityDate) {

		FreePaper freePaper = null;
		if (freePaperColl != null) {
			freePaper = new FreePaper();

			freePaper.setPaperId(freePaperColl.getId());
			if (userMap != null) {
				freePaper.setPaperStatus(PaperStatus.INPROGRESS);
				freePaper.setCreateDateTime(System.currentTimeMillis());
				freePaper.setPaper(freePaperColl);
			} else {
				freePaper.setPaperStatus(PaperStatus.START);
			}

			freePaper.setPaperSubCategory(freePaperColl.getPaperSubCategory());
			freePaper.setPaperType(freePaperColl.getPaperType());
			freePaper.setPaperCategory(freePaperColl.getPaperCategory());
			freePaper.setPaperSubCategoryName(freePaperColl.getPaperSubCategoryName());
			freePaper.setPaperName(freePaperColl.getPaperName());
			freePaper.setTestType(testType);
			freePaper.setUserId(userId);
			// freePaper.setCreateDateTime(System.currentTimeMillis());
			freePaper.setPaperValidityEndDate(validityDate);
			// freePaper.setPaperValidityStartDate(freePaperColl.getValidityRangeStartDateTime());

			freePaper.setTotalScore(freePaperColl.getTotalScore());
			freePaper.setTotalTime(freePaperColl.getTotalTime());
			freePaper.setNegativeMarks(freePaperColl.getNegativeMarks());
			freePaper.setTotalQuestionCount(freePaperColl.getTotalQuestionCount());
			freePaper.setPerQuestionScore(freePaperColl.getPerQuestionScore());
		}

		return freePaper;

	}

	@Override
	public List<PaperDto> getPaperStatusDetailsByPaperIds(String userId, List<String> paperIds, TestType testType)
			throws Exception {

		List<PaperDto> resultDto = null;
		try {
			if (paperIds == null || paperIds.isEmpty()) {
				return null;
			}

			// need to add validity date to paper...
			// valididy date from subscription and user entitlemt collection.
			List<PaperInfo> paperInfo = subscriptionService.getPaperInfoListForUser(userId, paperIds, testType);
			AtomicLong valididtyDate = new AtomicLong();
			if (paperInfo != null && !paperInfo.isEmpty()) {
				valididtyDate.set(paperInfo.get(0).getValidity());
			}

			LOGGER.info("size Free paperIds [" + paperIds.size() + "] ");
			LOGGER.info("getPaperStatusDetailsByPaperIds FRee userId [" + userId + "] paperIds [" + paperIds + "]");
			List<FreePaper> freePaperList = freePaperRepository.findFreePaperUsingUserIdAndPaperIds(userId, paperIds);
			if (freePaperList != null && !freePaperList.isEmpty()) {
				LOGGER.info("getPaperStatusDetailsByPaperIds Free paperSize [" + freePaperList.size() + "]");
				// get from user collection..
				List<PaperDto> paperDtoFromUserPaper = PaperConversionUtil.freePaperToDto(freePaperList);
				if (paperIds.size() > freePaperList.size()) {
					freePaperList.forEach(p -> paperIds.remove(p.getPaperId()));
					LOGGER.info("size after change paperSize [" + freePaperList.size() + "]");
					// get from paperCollection...
					List<FreePaperCollection> freePaperColl = freePaperCollectionRepository.findByIdListIns(paperIds);
					if (freePaperColl != null && !freePaperColl.isEmpty()) {
						List<FreePaper> freePaperList1 = new ArrayList<>();
						freePaperColl
								.forEach(freePaperCollItem -> freePaperList1.add(createFreePaperByFreeCollectionPaper(
										freePaperCollItem, testType, userId, null, valididtyDate.get())));
						List<PaperDto> paperDtoFromColl = PaperConversionUtil.freePaperToDto(freePaperList1);
						resultDto = Stream.of(paperDtoFromUserPaper, paperDtoFromColl).flatMap(x -> x.stream())
								.collect(Collectors.toList());
					} else {
						resultDto = paperDtoFromUserPaper;
					}
				} else {
					resultDto = paperDtoFromUserPaper;
				}
				LOGGER.info("getPaperStatusDetailsByPaperIds PAID paperSize [" + paperIds.size() + "]");
			}

			else {
				List<FreePaperCollection> freePaperColl = freePaperCollectionRepository.findByIdListIns(paperIds);
				List<FreePaper> freePaperList1 = new ArrayList<>();
				freePaperColl.forEach(
						freePaperCollItem -> freePaperList1.add(createFreePaperByFreeCollectionPaper(freePaperCollItem,
								testType, userId, null, valididtyDate.get())));
				resultDto = PaperConversionUtil.freePaperToDto(freePaperList1);
			}
		} catch (Exception ex) {
			throw ex;
		}
		return resultDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.book.assessu.service.PaperService#
	 * getPaperStatusDetailsByStatusAndPaperType(java.lang.String,
	 * com.book.assessu.constant.TestType, com.book.assessu.constant.PaperType,
	 * com.book.assessu.constant.PaperStatus,
	 * com.book.assessu.constant.PaperCategory)
	 */
	@Override
	public List<PaperDto> getPaperStatusDetailsByStatusAndPaperType(String userId, TestType testType,
			PaperType paperType, PaperStatus paperStatus, PaperCategory paperCategory) {

		List<PaperDto> paperDto = null;
		try {
			List<FreePaper> freePaperList = null;
			LOGGER.info("getPaperStatusDetailsByStatusAndPaperType FRee userId [" + userId + "] paperStatus ["
					+ paperStatus + "]");
			if (paperCategory != null) {
				freePaperList = freePaperRepository.findFreePaperUsingUserIdAndPaperStatusAndPaperCategory(userId,
						paperStatus, paperType, PaperCategory.valueOf(paperCategory.name()));
			} else {
				freePaperList = freePaperRepository.findFreePaperUsingUserIdAndPaperStatus(userId, paperStatus,
						paperType);
			}
			if (freePaperList != null && !freePaperList.isEmpty()) {
				//User percentiles for completed papers
				// Percentile for free papers not implemented; avoid analytics lookup.
				paperDto = PaperConversionUtil.freePaperToDto(freePaperList);
			}
		} catch (Exception ex) {
			LOGGER.error("error getPaperStatusDetailsByStatusAndPaperType free ", ex);
			throw ex;
		}
		return paperDto;
	}

	// will show missded paper to user later on for now we only shows missed paid
	// paper
	@Override
	public List<PaperDto> getMissedPapersByUsers(String userId, TestType testType, PaperType paperType,
			PaperCategory paperCategory) throws Exception {
		return null;
	}

	@Override
	public List<PaperDto> getPaperStatusDetailsByPaperCateoryORTestType(String userId, TestType testType,
			PaperType paperType, PaperCategory paperCategory) throws Exception {
		// TODO Auto-generated method stub
		List<PaperDto> resultDto = null;

		// need to add paperIds from subscription and entitle table..

		//Comenting it free paper don't need subscriptions.... for now later we will check and descide - Dharmendra.
		List<PaperInfo> listPaperInfo = subscriptionService.getPaperInfoListForUser(userId, new Date().getTime(),
				new Date().getTime(), true, paperType, paperCategory, testType);

		List<String> paperIds = new ArrayList<>();
		long validityDate;
		if (listPaperInfo != null && !listPaperInfo.isEmpty()) {
			validityDate = listPaperInfo.get(0).getValidity();
			listPaperInfo.forEach(paperInfo -> {
				paperIds.add(paperInfo.getId());

			});
		} else {
			LOGGER.info("Paper is not subscribed for user " + userId);
			return null;
			// throw new IllegalArgumentException("Paper is not subscribed");
		}

		/*
		 * List<Long> paperIds=new ArrayList<>(); paperIds.add(3L); paperIds.add(4L);
		 * paperIds.add(5L); paperIds.add(6L);
		 */

		try {
			if (paperIds == null || paperIds.isEmpty()) {
				return null;
			}
			LOGGER.info("size Free paperIds [" + paperIds.size() + "] ");
			LOGGER.info("getPaperStatusDetailsByPaperIds FRee userId [" + userId + "] paperIds [" + paperIds + "]");
			List<FreePaper> freePaperList = freePaperRepository.findFreePaperUsingUserIdAndPaperIds(userId, paperIds);
			if (freePaperList != null && !freePaperList.isEmpty()) {
				LOGGER.info("getPaperStatusDetailsByPaperIds Free paperSize [" + freePaperList.size() + "]");

				List<FreePaper> paperNotDoneOnlyList = new ArrayList<>();
				freePaperList.forEach(paperFree -> {

					if (paperFree != null && !PaperStatus.DONE.equals(paperFree.getPaperStatus())) {
						paperNotDoneOnlyList.add(paperFree);
					}
				});

				LOGGER.info("getPaperStatusDetailsByPaperIds after remove DONE statuc [" + paperNotDoneOnlyList.size()
						+ "]");

				// get from user collection..
				List<PaperDto> paperDtoFromUserPaper = PaperConversionUtil.freePaperToDto(paperNotDoneOnlyList);
				if (paperIds.size() > freePaperList.size()) {
					freePaperList.forEach(p -> paperIds.remove(p.getPaperId()));
					LOGGER.info("size after change paperSize [" + freePaperList.size() + "]");
					// get from paperCollection...
					List<FreePaperCollection> freePaperColl = freePaperCollectionRepository.findByIdListIns(paperIds);
					if (freePaperColl != null && !freePaperColl.isEmpty()) {
						List<FreePaper> freePaperList1 = new ArrayList<>();
						freePaperColl
								.forEach(freePaperCollItem -> freePaperList1.add(createFreePaperByFreeCollectionPaper(
										freePaperCollItem, testType, userId, null, validityDate)));
						List<PaperDto> paperDtoFromColl = PaperConversionUtil.freePaperToDto(freePaperList1);
						if (paperDtoFromUserPaper != null && paperDtoFromColl != null) {
							resultDto = Stream.of(paperDtoFromUserPaper, paperDtoFromColl).flatMap(x -> x.stream())
									.collect(Collectors.toList());
						} else if (paperDtoFromUserPaper != null) {
							resultDto = paperDtoFromUserPaper;
						} else if (paperDtoFromColl != null) {
							resultDto = paperDtoFromColl;
						}

					} else {
						resultDto = paperDtoFromUserPaper;
					}
				} else {
					resultDto = paperDtoFromUserPaper;
				}
				LOGGER.info("getPaperStatusDetailsByPaperIds PAID paperSize [" + paperIds.size() + "]");
			}

			else {
				List<FreePaperCollection> freePaperColl = freePaperCollectionRepository.findByIdListIns(paperIds);
				List<FreePaper> freePaperList1 = new ArrayList<>();
				freePaperColl.forEach(freePaperCollItem -> freePaperList1.add(
						createFreePaperByFreeCollectionPaper(freePaperCollItem, testType, userId, null, validityDate)));
				resultDto = PaperConversionUtil.freePaperToDto(freePaperList1);
			}
		} catch (Exception ex) {
			throw ex;
		}
		return resultDto;
	}

}
