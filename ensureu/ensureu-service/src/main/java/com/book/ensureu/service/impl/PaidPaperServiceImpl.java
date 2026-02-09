package com.book.ensureu.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.book.ensureu.flow.analytics.model.PaperStat;
import com.book.ensureu.flow.analytics.model.PercentileDataObject;
import com.book.ensureu.flow.analytics.repository.PaperStatRepository;
import com.book.ensureu.model.PaidPaper;
import com.book.ensureu.model.PaidPaperCollection;
import com.book.ensureu.repository.PaidPaperCollectionRepository;
import com.book.ensureu.repository.PaidPaperRepository;
import com.book.ensureu.service.CounterService;
import com.book.ensureu.service.PaperService;
import com.book.ensureu.service.SubscriptionService;
import com.book.ensureu.util.PaperConversionUtil;

@Service
@Qualifier("paidPaper")
public class PaidPaperServiceImpl implements PaperService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PaidPaperServiceImpl.class.getName());

	@Autowired
	PaidPaperRepository paidPaperRepository;

	@Autowired
	PaidPaperCollectionRepository paidPaperCollectionRepository;

	@Autowired
	CounterService counterService;

	@Autowired
	SubscriptionService subscriptionService;
	
	@Autowired
	private PaperStatRepository paperStatRepository;

	/*
	 * savePaper method will save and update the paper
	 * 
	 */
	@Override
	public void savePaper(PaperDto paperDto) {
		PaidPaper testPaper = PaperConversionUtil.paidPaperDtoToModel(paperDto);
		if (testPaper != null && testPaper.getUserId() != null && testPaper.getPaperId() != null) {
			PaidPaper existing = paidPaperRepository.findByUserIdAndPaperId(testPaper.getUserId(), testPaper.getPaperId());
			if (existing != null && PaperStatus.DONE.equals(existing.getPaperStatus())
					&& !PaperStatus.DONE.equals(testPaper.getPaperStatus())) {
				LOGGER.info("Skip save: paper already DONE for userId [{}], paperId [{}]", testPaper.getUserId(),
						testPaper.getPaperId());
				return;
			}
		}
		if (paperDto.getId() == null) {
			testPaper.setId(counterService.increment(CounterEnum.TESTPAPER));
		}
		LOGGER.info("User Mapping TestPaper UserId [" + testPaper.getUserId() + "] paperId [" + testPaper.getPaperId()
				+ "]");
		paidPaperRepository.save(testPaper);

	}

	/*
	 * update paper only update the existing paper
	 * 
	 */
	@Override
	public void updatePaper(PaperDto paperDto) {
		LOGGER.info("Update User with paper..");
		PaidPaper testPaper = PaperConversionUtil.paidPaperDtoToModel(paperDto);
		if (testPaper != null && testPaper.getUserId() != null && testPaper.getPaperId() != null) {
			PaidPaper existing = paidPaperRepository.findByUserIdAndPaperId(testPaper.getUserId(), testPaper.getPaperId());
			if (existing != null && PaperStatus.DONE.equals(existing.getPaperStatus())
					&& !PaperStatus.DONE.equals(testPaper.getPaperStatus())) {
				LOGGER.info("Skip update: paper already DONE for userId [{}], paperId [{}]", testPaper.getUserId(),
						testPaper.getPaperId());
				return;
			}
		}
		LOGGER.info("User Mapping TestPaper UserId [" + testPaper.getUserId() + "] paperId [" + testPaper.getPaperId()
				+ "]");
		paidPaperRepository.save(testPaper);
	}

	@Override
	public void submitPaper(AttemptedPaperDto attemptedPaper) {

	}

	@Override
	public PaperDto getPaperById(Long id, TestType testType) {
		Optional<PaidPaper> testPaperOptional = paidPaperRepository.findById(id);
		if (testPaperOptional.isPresent()) {
			return PaperConversionUtil.paidPaperToDto(testPaperOptional.get());
		}
		return null;
	}

	@Override
	public PaperDto getPaperByPaperIdAndUserId(String paperId, String userId, TestType testType) {

		
		PaidPaper testPaper = paidPaperRepository.findByUserIdAndPaperId(userId, paperId);
		PaidPaperCollection testPaperColl = null;
		if (testPaper != null) {
			return PaperConversionUtil.paidPaperToDto(testPaper);
		} else {
			Optional<PaidPaperCollection> testPaperCollOp = paidPaperCollectionRepository.findById(paperId);
			// valididy date from subscription and user entitlemt collection.
			List<String> paperList = new ArrayList<String>();
			paperList.add(paperId);
			List<PaperInfo> paperInfo = subscriptionService.getPaperInfoListForUser(userId, paperList, testType);
			long valididtyDate = 0;
			if (paperInfo != null && !paperInfo.isEmpty()) {
				valididtyDate = paperInfo.get(0).getValidity();
			}
			
			if (testPaperCollOp.isPresent()) {
				testPaperColl = testPaperCollOp.get();
				testPaper = new PaidPaper();
				testPaper.setPaper(testPaperColl);
				testPaper.setPaperId(testPaperColl.getId());
				testPaper.setPaperStatus(PaperStatus.START);
				testPaper.setPaperSubCategory(testPaperColl.getPaperSubCategory());
				testPaper.setPaperType(testPaperColl.getPaperType());
				testPaper.setPaperCategory(testPaperColl.getPaperCategory());
				testPaper.setPaperSubCategoryName(testPaperColl.getPaperSubCategoryName());
				testPaper.setTestType(testType);
				testPaper.setUserId(userId);
				testPaper.setCreateDateTime(System.currentTimeMillis());
				testPaper.setPaperValidityEndDate(valididtyDate);
			    testPaper.setPaperValidityStartDate(testPaperColl.getValidityRangeStartDateTime());

			} else {
				throw new IllegalArgumentException("paperId is not valid" + paperId);
			}
		}

		return PaperConversionUtil.paidPaperToDto(testPaper);
	}

	@Override
	public List<PaperDto> getPaperByPaperId(String paperId, TestType typePaper) {
		System.out.println("PAID Paper");
		List<PaidPaper> testPaperList = paidPaperRepository.findByPaperId(paperId);
		return PaperConversionUtil.paidPaperToDto(testPaperList);
	}


	@Override
	public long getAllCountPaper() {
		return paidPaperCollectionRepository.count();
	}


	@Override
	public long getPaperCountByPaperType(PaperType paperType) {
		return paidPaperCollectionRepository.count();
	}

	@Override
	public long getAllCountPaperCollectionByPaperType(PaperType paperType, TestType Paper) {
		return 0;
	}

	@Override
	public List<PaperDto> getPaperByUserId(String userId) {
		List<PaidPaper> paidPaperList = paidPaperRepository.findByUserId(userId);
		if (paidPaperList != null && !paidPaperList.isEmpty()) {
			return PaperConversionUtil.paidPaperToDto(paidPaperList);
		}
		return null;
	}

	@Override
	public List<PaperDto> getPaperByUserIdAndPaperType(String userId, PaperType paperType) {
		List<PaidPaper> paidPaperList = paidPaperRepository.findByUserIdAndPaperType(userId, paperType);
		if (paidPaperList != null && !paidPaperList.isEmpty()) {
			return PaperConversionUtil.paidPaperToDto(paidPaperList);
		}
		return null;
	}

	@Override
	public List<PaperDto> getPaperByUserIdAndPaperTypeAndTestType(String userId, PaperType paperType,
			TestType testType) {
		List<PaidPaper> paidPaperList = paidPaperRepository.findByUserIdAndPaperTypeAndTestType(userId, paperType,
				testType);
		if (paidPaperList != null && !paidPaperList.isEmpty()) {
			return PaperConversionUtil.paidPaperToDto(paidPaperList);
		}
		return null;
	}

	@Override
	public long getPaperCountByPaperTypeAndTestType(PaperType paperType, TestType testType) {
		return paidPaperCollectionRepository.countByPaperTypeAndTestType(paperType, testType);
	}

	@Override
	public long getPaperCountByPaperTypeAndTestTypeAndPaperCategory(PaperType paperType, TestType testType,
			PaperCategory paperCategory) {
		// TODO Auto-generated method stub
		return paidPaperCollectionRepository.countByPaperTypeAndPaperCategoryAndTestType(paperType, paperCategory,
				testType);
	}

	@Override
	public long getPaperCountByPaperTypeAndTestTypeAndPaperCategoryAndPaperSubCategory(PaperType paperType,
			TestType testType, PaperCategory paperCategory, PaperSubCategory paperSubCategory) {
		// TODO Auto-generated method stub
		return paidPaperCollectionRepository.countByPaperTypeAndPaperCategoryAndPaperSubCategoryAndTestType(paperType,
				paperCategory, paperSubCategory, testType);
	}

	@Override
	public PaperDto paperMappedUserByPaperStatus(String userId,
			TestType testType, PaperStatus paperStatus, String paperId) {

		if (PaperStatus.DONE.equals(paperStatus)) {
			PaidPaper paidPaperFromDb = paidPaperRepository
					.findByUserIdAndPaperId(userId, paperId);
			if (paidPaperFromDb != null) {
				return PaperConversionUtil.paidPaperToDto(paidPaperFromDb);
			} else {
				LOGGER.info("PaperId is not valid");
				return null;
			}

		} else {
			
			PaidPaper paidPaperFromDb = paidPaperRepository
					.findByUserIdAndPaperId(userId, paperId);
			
			if(paidPaperFromDb!=null && paidPaperFromDb.getPaperStatus().equals(PaperStatus.DONE)) {
				LOGGER.info("PaperId is already completed");
				return null;
			}
			
			if (paperStatus.equals(paperStatus.START)) {
				if (paidPaperFromDb != null) {
					return PaperConversionUtil.paidPaperToDto(paidPaperFromDb);
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

					Optional<PaidPaperCollection> paidPaperCollOpt = paidPaperCollectionRepository
							.findById(paperId);
					if (paidPaperCollOpt.isPresent()) {
						PaidPaperCollection paidPaperCollection = paidPaperCollOpt
								.get();
						PaidPaper paidPaper = createPaidPaperByPaidCollectionPaper(
								paidPaperCollection, testType, userId,
								"UserMapped", valididtyDate);
						paidPaper.setId(counterService
								.increment(CounterEnum.TESTPAPER));
						paidPaper = paidPaperRepository.save(paidPaper);
						return PaperConversionUtil.paidPaperToDto(paidPaper);

					} else {
						throw new IllegalArgumentException(
								"PaperId is not valid");
					}
				}
			} else if (paperStatus.equals(paperStatus.RESUME)
					|| paperStatus.equals(paperStatus.INPROGRESS)) {
				return PaperConversionUtil.paidPaperToDto(paidPaperFromDb);
			}
		}
		return null;
	}

	private PaidPaper createPaidPaperByPaidCollectionPaper(PaidPaperCollection paidPaperColl, TestType testType,
			String userId, String userMapped, long validityDate) {

		PaidPaper paidPaper = null;
		if (paidPaperColl != null) {
			paidPaper = new PaidPaper();
			paidPaper.setPaperId(paidPaperColl.getId());
			if (userMapped != null) {
				paidPaper.setPaper(paidPaperColl);
				paidPaper.setPaperStatus(PaperStatus.INPROGRESS);
				paidPaper.setCreateDateTime(System.currentTimeMillis());
			} else {
				paidPaper.setPaperStatus(PaperStatus.START);
			}
			paidPaper.setPaperCategory(paidPaperColl.getPaperCategory());
			paidPaper.setPaperSubCategory(paidPaperColl.getPaperSubCategory());
			paidPaper.setPaperType(paidPaperColl.getPaperType());
			paidPaper.setPaperName(paidPaperColl.getPaperName());
			paidPaper.setPaperSubCategoryName(paidPaperColl.getPaperSubCategoryName());
			paidPaper.setTestType(testType);
			paidPaper.setUserId(userId);
			paidPaper.setPaperValidityEndDate(validityDate);
			paidPaper.setTotalScore(paidPaperColl.getTotalScore());
			paidPaper.setTotalTime(paidPaperColl.getTotalTime());
			paidPaper.setNegativeMarks(paidPaperColl.getNegativeMarks());
			paidPaper.setTotalQuestionCount(paidPaperColl.getTotalQuestionCount());
			paidPaper.setPerQuestionScore(paidPaperColl.getPerQuestionScore());
			paidPaper.setTotalGetScore(paidPaperColl.getTotalGetScore());
			// paidPaper.setPaperValidityStartDate(paidPaperColl.getValidityRangeStartDateTime());
		}

		return paidPaper;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.book.assessu.service.PaperService#getPaperStatusDetailsByPaperIds(java.
	 * lang.String, java.util.List)
	 * 
	 * return paper test status for a user. take input as list of paperIds from
	 * subscription.
	 * 
	 */
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
			
			
			LOGGER.info("size PAID paperIds [" + paperIds.size() + "] ");
			LOGGER.info("getPaperStatusDetailsByPaperIds PAID userId [" + userId + "] paperIds [" + paperIds + "]");
			List<PaidPaper> paidPaperList = paidPaperRepository.findPaidPaperUsingUserIdAndPaperIds(userId, paperIds);
			if (paidPaperList != null && !paidPaperList.isEmpty()) {
				LOGGER.info("getPaperStatusDetailsByPaperIds PAID paperSize [" + paidPaperList.size() + "]");
				// get from user collection..
				
				List<PaidPaper> paperNotDoneOnlyList=new ArrayList<>();
				paidPaperList.forEach(paperPaid->{
					
					if(paperPaid!=null && !PaperStatus.DONE.equals(paperPaid.getPaperStatus())) {
						paperNotDoneOnlyList.add(paperPaid);
				}});
				
				List<PaperDto> paperDtoFromUserPaper = PaperConversionUtil.paidPaperToDto(paperNotDoneOnlyList);
				if (paperIds.size() > paidPaperList.size()) {
					paidPaperList.forEach(p -> paperIds.remove(p.getPaperId()));
					LOGGER.info("size after change paperSize [" + paidPaperList.size() + "]");
					// get from paperCollection...
					List<PaidPaperCollection> paidPaperColl = paidPaperCollectionRepository.findByIdListIns(paperIds);
					if (paidPaperColl != null && !paidPaperColl.isEmpty()) {
						List<PaidPaper> paidPaperList1 = new ArrayList<>();
						paidPaperColl.forEach(paidPaperCollItem -> paidPaperList1.add(
								createPaidPaperByPaidCollectionPaper(paidPaperCollItem, testType, userId, null, valididtyDate.get())));
						List<PaperDto> paperDtoFromColl = PaperConversionUtil.paidPaperToDto(paidPaperList1);
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
				List<PaidPaperCollection> paidPaperColl = paidPaperCollectionRepository.findByIdListIns(paperIds);
				List<PaidPaper> paidPaperList1 = new ArrayList<>();
				paidPaperColl.forEach(paidPaperCollItem -> paidPaperList1
						.add(createPaidPaperByPaidCollectionPaper(paidPaperCollItem, testType, userId, null, valididtyDate.get())));
				resultDto = PaperConversionUtil.paidPaperToDto(paidPaperList1);
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
			List<PaidPaper> paidPaperList = null;
			LOGGER.info("getPaperStatusDetailsByStatusAndPaperType PAID userId [" + userId + "] paperStatus ["
					+ paperStatus + "]");
			if (paperCategory != null) {
				paidPaperList = paidPaperRepository.findPaidPaperUsingUserIdAndPaperStatusAndPaperCategory(userId,
						paperStatus, paperType, paperCategory);
			} else {
				paidPaperList = paidPaperRepository.findPaidPaperUsingUserIdAndPaperStatus(userId, paperStatus,
						paperType);
			}
			if (paidPaperList != null && !paidPaperList.isEmpty()) {
				List<String> paperIds=paidPaperList.stream().map(PaidPaper :: getPaperId).collect(Collectors.toList());
				List<PaperStat> paperStats=paperStatRepository.fetchPaperStatByIdIn(paperIds);
				Map<String,Double> paperIdVsPercentile=new HashMap<String, Double>();
				if (paperStats != null) {
					paperStats.forEach(paperStat->{
						if (paperStat.getPercentileDataObjectList() == null) {
							return;
						}
						for(PercentileDataObject percentileDataObject : paperStat.getPercentileDataObjectList()) {
							if(percentileDataObject.getUserIds().contains(userId)) {
								LOGGER.info(userId + " percentile " + percentileDataObject.getPercentile() + " for paper "
										+ paperStat.getPaperId());
								paperIdVsPercentile.put(paperStat.getPaperId(), percentileDataObject.getPercentile());
								break;
							}
						}
					});
				}
				paperDto = PaperConversionUtil.paidPaperToDto(paidPaperList,paperIdVsPercentile);
			}
		} catch (Exception ex) {
			LOGGER.error("error getPaperStatusDetailsByStatusAndPaperType ", ex);
			throw ex;
		}
		return paperDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.book.assessu.service.PaperService#getMissedPapersByUsers(java.lang.
	 * String, com.book.assessu.constant.TestType,
	 * com.book.assessu.constant.PaperType, com.book.assessu.constant.PaperCategory)
	 */
	@Override
	public List<PaperDto> getMissedPapersByUsers(String userId, TestType testType, PaperType paperType,
			PaperCategory paperCategory) throws Exception {
		LOGGER.info("getMissedPapersByUsers PAID userId [" + userId + "] paperCategory [" + paperCategory + "]");
		// call userEntitlement to get list paperId subscribed by user...

		/*
		 * List<PaperInfo> listOfPaperInfo =
		 * subscriptionService.getPaperInfoListForUser(userId, null, new
		 * Date().getTime(), true,paperType,paperCategory,testType);
		 */
		// need to get validity date;

		List<PaperInfo> listPaperInfo=subscriptionService.getLastPaperInfoListForUser(userId, true, paperType, paperCategory, testType);
		
		List<String> paperIds = new ArrayList<>();
		long validityDate;
		if (listPaperInfo != null && !listPaperInfo.isEmpty()) {
			validityDate = listPaperInfo.get(0).getValidity();
			listPaperInfo.forEach(paperInfo -> {
				paperIds.add(paperInfo.getId());
 
			});
		} else {
			LOGGER.info("No Missed Paper for this user "+userId);
			return null;
		}

		List<PaperDto> resultDto = null;
		try {
			if (paperIds == null || paperIds.isEmpty()) {
				return null;
			}
			LOGGER.info("size PAID paperIds [" + paperIds.size() + "] ");
			LOGGER.info("getMissedPapersByUsers PAID userId [" + userId + "] paperIds [" + paperIds + "]");
			List<PaidPaper> paidPaperListInDb = paidPaperRepository.findPaidPaperUsingUserIdAndPaperIds(userId, paperIds);
			List<PaidPaper> paidPaperListClone=new ArrayList<PaidPaper>();
			if (paidPaperListInDb != null && !paidPaperListInDb.isEmpty()) {
				paidPaperListInDb.stream().forEach(paidPaper -> {
					if(!PaperStatus.DONE.equals(paidPaper.getPaperStatus())) {
						paidPaperListClone.add(paidPaper);
					}
					paperIds.remove(paidPaper.getPaperId());

				});
				
			}
			// create paper by paperId from paperCollection.
			List<PaidPaperCollection> paidPaperColl = paidPaperCollectionRepository.findByIdListIns(paperIds);
			List<PaidPaper> paidPaperValue = new ArrayList<>();
			if(paidPaperColl!=null && !paidPaperColl.isEmpty()) {
			paidPaperColl.forEach(paperCollList -> paidPaperValue
					.add(createPaidPaperByPaidCollectionPaper(paperCollList, testType, userId, null, validityDate)));
			}
			
			List<PaidPaper> paidPaperValueRes=null;
			if(paidPaperValue!=null && paidPaperListInDb!=null) {
				paidPaperValueRes=Stream.of(paidPaperValue,paidPaperListClone).flatMap(x->x.stream()).collect(Collectors.toList());
			}else if(paidPaperValue!=null) {
				paidPaperValueRes=paidPaperValue;
			}else {
				paidPaperValueRes=paidPaperListInDb;
			}
			resultDto = PaperConversionUtil.paidPaperToDto(paidPaperValueRes);
			return resultDto;
		} catch (Exception ex) {
			LOGGER.error("error getMissedPapersByUsers ", ex);
			throw ex;
		}

	}

	@Override
	public List<PaperDto> getPaperStatusDetailsByPaperCateoryORTestType(String userId, TestType testType,
			PaperType paperType, PaperCategory paperCategory) throws Exception {
		List<PaperDto> resultDto = null;

		// need to add paperIds from subscription and entitle table..

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
			LOGGER.info("User ["+userId+"] is not subscribed paper, fallback to user papers");
			List<PaidPaper> paidPaperList = paidPaperRepository.findByUserIdAndPaperTypeAndTestType(userId, paperType, testType);
			if (paidPaperList == null || paidPaperList.isEmpty()) {
				return null;
			}
			List<PaidPaper> filtered = new ArrayList<>();
			paidPaperList.forEach(paper -> {
				if (paper == null) {
					return;
				}
				if (paperCategory != null && !paperCategory.equals(paper.getPaperCategory())) {
					return;
				}
				if (!PaperStatus.DONE.equals(paper.getPaperStatus())) {
					filtered.add(paper);
				}
			});
			return PaperConversionUtil.paidPaperToDto(filtered);
		}
		/*
		 * paperIds.add(3L); paperIds.add(4L); paperIds.add(5L); paperIds.add(6L);
		 */
		try {
			if (paperIds == null || paperIds.isEmpty()) {
				return null;
			}
			LOGGER.info("size PAID paperIds [" + paperIds.size() + "] ");
			LOGGER.info("getPaperStatusDetailsByPaperIds PAID userId [" + userId + "] paperIds [" + paperIds + "]");
			List<PaidPaper> paidPaperList = paidPaperRepository.findPaidPaperUsingUserIdAndPaperIds(userId, paperIds);
			//List<PaidPaper> paidPaperList = paidPaperRepository.findPaidPaperUsingUserIdAndPaperIdsAndNotDone(userId, paperIds);
			if (paidPaperList != null && !paidPaperList.isEmpty()) {
				LOGGER.info("getPaperStatusDetailsByPaperIds PAID paperSize [" + paidPaperList.size() + "]");
				
				List<PaidPaper> paperNotDoneOnlyList=new ArrayList<>();
				paidPaperList.forEach(paperPaid->{
					
					if(paperPaid!=null && !PaperStatus.DONE.equals(paperPaid.getPaperStatus())) {
						paperNotDoneOnlyList.add(paperPaid);
				}});
				
				LOGGER.info("getPaperStatusDetailsByPaperIds PAID paperSize after DONE remove [" + paperNotDoneOnlyList.size() + "]");

				// get from user collection..
				List<PaperDto> paperDtoFromUserPaper = PaperConversionUtil.paidPaperToDto(paperNotDoneOnlyList);
				if (paperIds.size() > paidPaperList.size()) {
					paidPaperList.forEach(p -> paperIds.remove(p.getPaperId()));
					LOGGER.info("size after change paperSize [" + paidPaperList.size() + "]");
					// get from paperCollection...
					List<PaidPaperCollection> paidPaperColl = paidPaperCollectionRepository.findByIdListIns(paperIds);
					if (paidPaperColl != null && !paidPaperColl.isEmpty()) {
						List<PaidPaper> paidPaperList1 = new ArrayList<>();
						paidPaperColl
								.forEach(paidPaperCollItem -> paidPaperList1.add(createPaidPaperByPaidCollectionPaper(
										paidPaperCollItem, testType, userId, null, validityDate)));
						List<PaperDto> paperDtoFromColl = PaperConversionUtil.paidPaperToDto(paidPaperList1);
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
				LOGGER.info("getPaperStatusDetailsByPaperIds PAID paperSize [" + paperIds.size() + "]");
			}

			else {
				List<PaidPaperCollection> paidPaperColl = paidPaperCollectionRepository.findByIdListIns(paperIds);
				List<PaidPaper> paidPaperList1 = new ArrayList<>();
				paidPaperColl.forEach(paidPaperCollItem -> paidPaperList1.add(
						createPaidPaperByPaidCollectionPaper(paidPaperCollItem, testType, userId, null, validityDate)));
				resultDto = PaperConversionUtil.paidPaperToDto(paidPaperList1);
			}
		} catch (Exception ex) {
			throw ex;
		}
		return resultDto;
	}

}
